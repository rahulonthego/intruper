/**
 * @author rahul
 * @created 12/10/2018
 */
package com.zomasystems.processors.aws;

import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.model.DetectLabelsRequest;
import com.amazonaws.services.rekognition.model.DetectLabelsResult;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.rekognition.model.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Callable;

public class RekognitionDetectLabelsProcessor implements Callable<String> {

    private static final Logger log = LoggerFactory.getLogger(RekognitionDetectLabelsProcessor.class);

    private AmazonRekognition client;

    private Image image;

    public RekognitionDetectLabelsProcessor(AmazonRekognition client, Image image) {
        this.client = client;
        this.image = image;
    }

    @Override
    public String call() throws Exception {

        DetectLabelsRequest request = new DetectLabelsRequest().withImage(image)
                .withMaxLabels(123)
                .withMinConfidence(70f);
        DetectLabelsResult response = client.detectLabels(request);

        StringBuilder stringBuilder = new StringBuilder();

        if (response != null && response.getLabels() != null) {
            List<Label> awsImageLabels = response.getLabels();

            stringBuilder.append("Number of labels found: " + awsImageLabels.size());

            for (Label label : awsImageLabels) {

                stringBuilder.append(label.getName() + " Confidence: " + label.getConfidence().shortValue() + " ");

                if (log.isDebugEnabled()) {
                    log.debug(label.toString());
                }
            }
        }

        return stringBuilder.toString();
    }

    public AmazonRekognition getClient() {
        return client;
    }

    public void setClient(AmazonRekognition client) {
        this.client = client;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}
