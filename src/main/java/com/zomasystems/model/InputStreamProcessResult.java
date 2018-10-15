/**
 * @author rahul
 * @created 28/09/2018
 */
package com.zomasystems.model;

import com.amazonaws.services.rekognition.model.BoundingBox;
import com.amazonaws.services.rekognition.model.Image;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;

@JsonIgnoreProperties({"image"})
public class InputStreamProcessResult {

    private String predictions;
    private String imgLabels;
    private String awsImageLabels;
    HashMap<String, BoundingBox> resultingMetaData;

    Image image;
    private String error;

    public HashMap<String, BoundingBox> getResultingMetaData() {
        return resultingMetaData;
    }

    public void setResultingMetaData(HashMap<String, BoundingBox> resultingMetaData) {
        this.resultingMetaData = resultingMetaData;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getPredictions() {
        return predictions;
    }

    public void setPredictions(String predictions) {
        this.predictions = predictions;
    }

    public String getImgLabels() {
        return imgLabels;
    }

    public void setImgLabels(String imgLabels) {
        this.imgLabels = imgLabels;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getAwsImageLabels() {
        return awsImageLabels;
    }

    public void setAwsImageLabels(String awsImageLabels) {
        this.awsImageLabels = awsImageLabels;
    }
}
