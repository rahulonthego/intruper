/**
 * @author rahul
 * @created 29/09/2018
 */
package com.zomasystems.processors.google;

import com.google.cloud.automl.v1beta1.*;
import com.zomasystems.config.GoogleAutoMLProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class PredictProcessor implements Callable<String> {

    private static final Logger logger = LoggerFactory.getLogger(PredictProcessor.class);

    private Image image;

    public PredictProcessor(GoogleAutoMLProperties googleAutoMLProperties, Image image) {
        this.image = image;
        this.googleAutoMLProperties = googleAutoMLProperties;
    }

    private GoogleAutoMLProperties googleAutoMLProperties;


    @Override
    public String call() throws IOException {
        try (PredictionServiceClient predictionServiceClient = PredictionServiceClient.create(googleAutoMLProperties.getPredictionServiceSettings())) {
            ModelName name = ModelName.of(googleAutoMLProperties.getProject(), googleAutoMLProperties.getLocation(), googleAutoMLProperties.getModel());

            ExamplePayload payload = ExamplePayload.newBuilder().setImage(image).build();
            Map<String, String> params = new HashMap<>();
            PredictResponse response = predictionServiceClient.predict(name, payload, params);

            if (response != null) {
                StringBuilder stringBuilder = new StringBuilder();

                for (AnnotationPayload annotationPayload : response.getPayloadList()) {
                    stringBuilder.append(" " + annotationPayload.getDisplayName() + " - " + annotationPayload.getClassification().getScore() + " ");
                    stringBuilder.append('\n');
                }

                return stringBuilder.toString();
            }
        }
        return null;
    }
}
