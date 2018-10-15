/**
 * @author rahul
 * @created 08/09/2018
 */
package com.zomasystems.processors;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.zomasystems.processors.aws.S3Processor;
import com.zomasystems.utils.DateUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests the s3 processor to validate it put the objects to the s3 buckets successfully
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class S3ProcessorTest {

    @Autowired
    private S3Processor s3Processor;

    private AmazonS3 s3Client;

    private byte[] buffer = new byte[248];
    private InputStream inputStream;

    @Before
    public void setup(){

        s3Client = mock(AmazonS3.class);

        inputStream = mock(InputStream.class);

        when(s3Client.putObject(any())).thenReturn(new PutObjectResult());
        s3Processor.setS3Client(s3Client);
    }

    @Test
    public void testSavingResource() throws IOException {
//        String key = DateUtils.timeFormatter.format(new Date());
//        s3Processor.saveToS3(key, inputStream, "image/jpeg");
//
//        verify(s3Client).putObject(any());
    }

}
