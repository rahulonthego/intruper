/**
 * @author rmalhotra
 * @created 02/09/2018
 */
package com.zomasystems.config;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties( "aws.s3" )
public class AwsS3Properties {

    private String awsRegion;

    private String bucket;

    private String profile;

    public String getAwsRegion() {
        return awsRegion;
    }

    public void setAwsRegion(String awsRegion) {
        this.awsRegion = awsRegion;
    }

    public Regions getAwsRegions(){
        return Regions.fromName(awsRegion);
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
