/**
 * @author rmalhotra
 * @created 02/09/2018
 */
package com.zomasystems.processors.aws;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.zomasystems.config.AwsS3Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.*;


@Component("s3Processor")
public class S3Processor {

    private static final Logger logger = LoggerFactory.getLogger(S3Processor.class);

    @Autowired
    private AwsS3Properties awsS3Properties;

    public AmazonS3 getS3Client() {
        return s3Client;
    }

    public void setS3Client(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    private AmazonS3 s3Client;

    @PostConstruct
    private void setupClient(){
        if(logger.isDebugEnabled()) {
            s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(awsS3Properties.getAwsRegion())
                    .withCredentials(new ProfileCredentialsProvider("rahul-personal"))
                    .build();
        }else{
            s3Client = AmazonS3ClientBuilder.standard().withCredentials(new ClasspathPropertiesFileCredentialsProvider() ).build();
        }
    }

    public InputStream getObject(String bucketName, String key){

        S3Object fullObject = null, objectPortion = null, headerOverrideObject = null;
        // Get a range of bytes from an object and print the bytes.
        GetObjectRequest rangeObjectRequest = new GetObjectRequest(bucketName, key)
                .withRange(0,9);
        objectPortion = s3Client.getObject(rangeObjectRequest);
        return objectPortion.getObjectContent();
    }

    /**
     *
     * Using the profile defined in the config file, this will connect to AWS and persist
     * the file object with the name provided
     *
     * @param fileObjKeyName - file name to save as
     * @param contentType - content type of the object ex. image/jpeg
     */
    public void saveToS3(String bucket, String fileObjKeyName, BufferedImage image, String contentType, CannedAccessControlList accessControlList) throws IOException {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", os);

            InputStream byteArrayStream = new ByteArrayInputStream(os.toByteArray());
            int read = byteArrayStream.available();
            logger.info(String.format("Read length: %s", read));
            // Upload a file as a new object with ContentType and title specified.
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            metadata.setContentLength(read);

            PutObjectRequest request = new PutObjectRequest(bucket, fileObjKeyName, byteArrayStream, metadata);
            request.setCannedAcl(accessControlList);
            PutObjectResult result = s3Client.putObject(request);
            if (result != null){
                logger.info(String.format("Object %s successfully persisted on S3 %s",fileObjKeyName, awsS3Properties.getBucket()));
            }else{
                logger.info(String.format("Something went wrong... %s not persisted on S3 %s",fileObjKeyName, awsS3Properties.getBucket()));
            }
        }
        catch(SdkClientException e) {
            logger.error(String.format("An error occured while AWS S3 processing of %s and bucket %s", fileObjKeyName, awsS3Properties.getBucket()), e);
        }

    }

    public void copyS3ObjectAsPublic(String fromBucket, String sourceKey, String toBucket, String destinationKey){
        try {

            // Copy the object into a new object in the same bucket.
            CopyObjectRequest copyObjRequest = new CopyObjectRequest(fromBucket, sourceKey, toBucket, destinationKey);

            copyObjRequest.setCannedAccessControlList(CannedAccessControlList.PublicRead);
            s3Client.copyObject(copyObjRequest);
        }
        catch(AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        }
        catch(SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }
    }
}
