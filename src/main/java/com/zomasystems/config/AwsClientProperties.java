/**
 * @author rmalhotra
 * @created 02/09/2018
 */
package com.zomasystems.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties( "aws.client" )
public class AwsClientProperties {

    private Integer awsClientMaxConnections;
    private String awsRegion;

    public Integer getAwsClientMaxConnections() {
        return awsClientMaxConnections;
    }

    public void setAwsClientMaxConnections(Integer awsClientMaxConnections) {
        this.awsClientMaxConnections = awsClientMaxConnections;
    }

    public String getAwsRegion() {
        return awsRegion;
    }

    public void setAwsRegion(String awsRegion) {
        this.awsRegion = awsRegion;
    }

}
