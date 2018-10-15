/**
 * @author rahul
 * @created 04/10/2018
 */
package com.zomasystems.processors.google;

import com.google.cloud.vision.v1.*;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.protobuf.ByteString;
import com.zomasystems.config.GoogleAutoMLProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class VisionProcessor implements Callable<String> {

    private static final Logger logger = LoggerFactory.getLogger(VisionProcessor.class);

    private Image image;

    private GoogleAutoMLProperties googleAutoMLProperties;

    public VisionProcessor(GoogleAutoMLProperties googleAutoMLProperties, Image image) {
        this.googleAutoMLProperties = googleAutoMLProperties;
        this.image = image;
    }


    @Override
    public String call() throws Exception {

        StringBuilder responseBuilder = new StringBuilder();

        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create(googleAutoMLProperties.getImageAnnotatorSettings())) {
            List<AnnotateImageRequest> requests = new ArrayList<>();
            Feature feat = Feature.newBuilder().setType(Type.LABEL_DETECTION).build();
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
                    return null;
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

        return responseBuilder.toString();
    }

    public static void main(String... args) throws Exception {
        // Instantiates a client
        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {

            // The path to the image file to annotate
            String fileName = "./resources/wakeupcat.jpg";

            // Reads the image file into memory
            Path path = Paths.get(fileName);
            byte[] data = Files.readAllBytes(path);
            ByteString imgBytes = ByteString.copyFrom(data);

            // Builds the image annotation request
            List<AnnotateImageRequest> requests = new ArrayList<>();
            Image img = Image.newBuilder().setContent(imgBytes).build();
            Feature feat = Feature.newBuilder().setType(Type.LABEL_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feat)
                    .setImage(img)
                    .build();
            requests.add(request);

            // Performs label detection on the image file
            BatchAnnotateImagesResponse response = vision.batchAnnotateImages(requests);
            List<AnnotateImageResponse> responses = response.getResponsesList();

            for (AnnotateImageResponse res : responses) {
                if (res.hasError()) {
                    System.out.printf("Error: %s\n", res.getError().getMessage());
                    return;
                }

                for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
                    annotation.getAllFields().forEach((k, v) ->
                            System.out.printf("%s : %s\n", k, v.toString()));
                }
            }
        }
    }
}
