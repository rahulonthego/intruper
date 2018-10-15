/**
 * @author rahul
 * @created 28/09/2018
 */
package com.zomasystems.processors;

import com.zomasystems.processors.aws.S3Processor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.mock;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MessageProcessorTest {

    @Autowired
    InputStreamProcessor messageProcessor;
    @Autowired
    private S3Processor s3Processor;
    @Before
    public void setup(){
        s3Processor = mock(S3Processor.class);
        // doNothing().when(s3Processor).saveToS3(any(),eq(inputStream), eq("image/jpeg"));

    }

    /**
     *
     */
    @Test
    public void testMessageInputStream(){

    }
}
