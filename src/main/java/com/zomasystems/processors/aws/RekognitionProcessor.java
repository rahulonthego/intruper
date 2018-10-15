/**
 * @author rahul
 * @created 22/09/2018
 */
package com.zomasystems.processors.aws;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.*;
import com.zomasystems.config.AwsRekognitionProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

@Component
public class RekognitionProcessor  {


    private static final Logger log = LoggerFactory.getLogger(RekognitionProcessor.class);

    private AmazonRekognition getClient(){
        if(log.isDebugEnabled()){
            return AmazonRekognitionClientBuilder.standard().withCredentials(new ProfileCredentialsProvider("rahul-personal")).build();
        }
        return AmazonRekognitionClientBuilder.standard().withCredentials(new ClasspathPropertiesFileCredentialsProvider()).build();
    }

    public DetectLabelsResult detectLabels(Image image) {

        AmazonRekognition client = getClient();

        DetectLabelsRequest request = new DetectLabelsRequest().withImage(image)
                .withMaxLabels(123)
                .withMinConfidence(70f);
        DetectLabelsResult response = client.detectLabels(request);

        return response;
    }

    /**
     * For each of the face detected in the image, build a list of bounding boxes and labels
     * @param image
     * @return
     * @throws Exception
     */
    public HashMap<String, BoundingBox> detectFaces(Image image) throws Exception {

        HashMap<String, BoundingBox> resultingMap = null;
        AmazonRekognition rekognitionClient = getClient();

        DetectFacesResult result = null;

        DetectFacesRequest request = new DetectFacesRequest()
                .withImage(image)
                .withAttributes(Attribute.ALL);
        // Replace Attribute.ALL with Attribute.DEFAULT to get default values.

        try {
            result = rekognitionClient.detectFaces(request);
            if(log.isDebugEnabled()){
                log.debug(result.toString());
            }

        } catch (AmazonRekognitionException e) {
            log.error("An error occured while detecting faces for image", e);
        }
        if (result != null) {

            resultingMap = new HashMap<>();

            List<FaceDetail> faceDetails = result.getFaceDetails();
            log.info("Number of faces found: " + faceDetails.size());
            int j = 1;

            for (FaceDetail detail : faceDetails) {
                StringBuilder stringBuilder = new StringBuilder();
                if (detail != null && stringBuilder.length() < 1200) {
                    stringBuilder.append(" >> For face: " + j + " .........");
                    log.info((" features  >>>> " + j));
                    log.info(detail.toString());
                    if (detail.getAgeRange() != null) {
                        stringBuilder.append(" Age range: " + detail.getAgeRange());
                    }
                    if (detail.getGender() != null) {
                        stringBuilder.append(" Gender: " + detail.getGender());
                    }

                    if (detail.getBeard() != null) {
                        stringBuilder.append(" Beard: " + detail.getBeard());
                    }

                    if (detail.getEmotions() != null) {
                        stringBuilder.append(" " +
                                "Emotions: " + detail.getEmotions());
                    }
                    //stringBuilder.append()
                    resultingMap.put(stringBuilder.toString(), detail.getBoundingBox());
                }
                j++;
            }

            return resultingMap;
        }

        return null;
    }

}
