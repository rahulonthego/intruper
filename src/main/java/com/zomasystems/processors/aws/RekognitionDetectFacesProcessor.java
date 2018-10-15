/**
 * @author rahul
 * @created 12/10/2018
 */
package com.zomasystems.processors.aws;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

public class RekognitionDetectFacesProcessor implements Callable<HashMap<String, BoundingBox>> {

    private static final Logger log = LoggerFactory.getLogger(RekognitionDetectFacesProcessor.class);

    private Image image;
    private AmazonRekognition rekognitionClient;

    public RekognitionDetectFacesProcessor(AmazonRekognition client, Image image) {
        rekognitionClient = client;
        this.image = image;
    }

    @Override
    public HashMap call() throws Exception {
        HashMap<String, BoundingBox> resultingMap = null;

        DetectFacesResult result = null;

        DetectFacesRequest request = new DetectFacesRequest()
                .withImage(image)
                .withAttributes(Attribute.ALL);
        // Replace Attribute.ALL with Attribute.DEFAULT to get default values.

        try {
            result = rekognitionClient.detectFaces(request);
            if (log.isDebugEnabled()) {
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
                    stringBuilder.append(" Facial features of : " + j + " ");
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

    public static Logger getLog() {
        return log;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public AmazonRekognition getRekognitionClient() {
        return rekognitionClient;
    }

    public void setRekognitionClient(AmazonRekognition rekognitionClient) {
        this.rekognitionClient = rekognitionClient;
    }
}
