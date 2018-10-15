/**
 * @author rahul
 * @created 30/09/2018
 */
package com.zomasystems.config;


import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.automl.v1beta1.PredictionServiceSettings;
import com.google.cloud.vision.v1.ImageAnnotatorSettings;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.IOException;

@Configuration
@ConfigurationProperties( "google" )
public class GoogleAutoMLProperties {

    private String project;
    private String location;
    private String model;
    private String credentialsFile;
    private ClassPathResource credentialsFileResource;
    private Credentials googleCredentials;
    private PredictionServiceSettings predictionServiceSettings;
    private ImageAnnotatorSettings imageAnnotatorSettings;

    @PostConstruct
    public void setupCredentialsFile(){
        credentialsFileResource  = new ClassPathResource(credentialsFile);
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCredentialsFile() {
        return credentialsFile;
    }

    public void setCredentialsFile(String credentialsFile) {
        this.credentialsFile = credentialsFile;
    }

    public ClassPathResource getCredentialsFileResource() {
        return credentialsFileResource;
    }

    public Credentials getCredentials() throws FileNotFoundException, IOException {

        if (googleCredentials == null) {
            googleCredentials = ServiceAccountCredentials.fromStream(getCredentialsFileResource().getInputStream());
        }

        return googleCredentials;
    }

    public PredictionServiceSettings getPredictionServiceSettings() throws IOException {
        if (predictionServiceSettings == null) {
            predictionServiceSettings =
                    PredictionServiceSettings.newBuilder()
                            .setCredentialsProvider(FixedCredentialsProvider.create(getCredentials()))
                            .build();

        }
        return predictionServiceSettings;
    }

    public ImageAnnotatorSettings getImageAnnotatorSettings() throws IOException {
        if (imageAnnotatorSettings == null) {
            imageAnnotatorSettings =
                    ImageAnnotatorSettings.newBuilder()
                            .setCredentialsProvider(FixedCredentialsProvider.create(getCredentials()))
                            .build();

        }
        return imageAnnotatorSettings;
    }
}
