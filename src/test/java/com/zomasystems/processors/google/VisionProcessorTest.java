/**
 * @author rahul
 * @created 05/10/2018
 */
package com.zomasystems.processors.google;

import com.amazonaws.util.IOUtils;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import com.zomasystems.config.GoogleAutoMLProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class VisionProcessorTest {

    private static final Logger logger = LoggerFactory.getLogger(VisionProcessorTest.class);

    private ImageAnnotatorSettings imageAnnotatorSettings;
    private Credentials googleCredentials;

    @Autowired
    private GoogleAutoMLProperties googleAutoMLProperties;

    private Credentials getCredentials() throws FileNotFoundException, IOException {

        if (googleCredentials == null) {
            googleCredentials = ServiceAccountCredentials.fromStream(googleAutoMLProperties.getCredentialsFileResource().getInputStream());
        }

        return googleCredentials;
    }

    private ImageAnnotatorSettings getImageAnnotatorSettings() throws IOException {
        if (imageAnnotatorSettings == null) {
            imageAnnotatorSettings =
                    ImageAnnotatorSettings.newBuilder()
                            .setCredentialsProvider(FixedCredentialsProvider.create(getCredentials()))
                            .build();

        }
        return imageAnnotatorSettings;
    }

    @Test
    public void getImageLabels() throws Exception {

        byte[] byteArray = IOUtils.toByteArray((new ClassPathResource("ManWalkingToDoorWithCane.jpg")).getInputStream());
        Image image = Image.newBuilder().setContent(ByteString.copyFrom(byteArray)).build();

        StringBuilder responseBuilder = new StringBuilder();

        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create(getImageAnnotatorSettings())) {
            List<AnnotateImageRequest> requests = new ArrayList<>();
            Feature feat = Feature.newBuilder().setType(Feature.Type.FACE_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feat)
                    .setImage(image)
                    .build();
            requests.add(request);

            // Performs label detection on the image file
            BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    logger.error("Error: %s\n", res.getError().getMessage());
                }

                logger.debug("****************** Entity Annotations***********************************");
                for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {

                    //annotation.getAllFields().forEach((k, v) ->responseBuilder.append(k + "=" + v + "\r"));
                    //annotation.getAllFields().forEach((k, v) ->logger.info(k + "=" + v + "\r"));

                    logger.info(annotation.getDescription());
                    BoundingPoly boundingPoly = annotation.getBoundingPoly();
                    for (Vertex vertex : boundingPoly.getVerticesList()) {
                        logger.info("x: " + vertex.getX() + ", " + "y: " + vertex.getY());
                    }
                }
                logger.debug("******************** Face Anotations ********************************");
                for (FaceAnnotation faceAnnotation : res.getFaceAnnotationsList()) {
                    logger.info(faceAnnotation.getJoyLikelihood().name());
                    //faceAnnotation.getAllFields().forEach((k, v) ->logger.info(k + "=" + v + "\r"));
                    BoundingPoly boundingPoly = faceAnnotation.getBoundingPoly();
                    for (Vertex vertex : boundingPoly.getVerticesList()) {
                        logger.info("x: " + vertex.getX() + ", " + "y: " + vertex.getY());
                    }
                }
            }
        }
    }
}
