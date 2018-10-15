/**
 * @author rahul
 * @created 24/09/2018
 */
package com.zomasystems.config;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.zomasystems.processors.aws.RekognitionDetectFacesProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties( "aws.rekognition" )
public class AwsRekognitionProperties {

    private static final Logger log = LoggerFactory.getLogger(AwsRekognitionProperties.class);

    @Bean
    public AmazonRekognition getClient(){
        if(log.isDebugEnabled()){
            return AmazonRekognitionClientBuilder.standard().withCredentials(new ProfileCredentialsProvider("rahul-personal")).build();
        }
        return AmazonRekognitionClientBuilder.standard().withCredentials(new ClasspathPropertiesFileCredentialsProvider()).build();
    }
}
