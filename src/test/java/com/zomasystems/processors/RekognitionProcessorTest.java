/**
 * @author rahul
 * @created 29/09/2018
 */
package com.zomasystems.processors;

import com.amazonaws.services.rekognition.model.BoundingBox;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.util.IOUtils;
import com.zomasystems.config.TestApplicationConfiguration;
import com.zomasystems.processors.aws.RekognitionProcessor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RekognitionProcessorTest {
    private static final Logger log = LoggerFactory.getLogger(RekognitionProcessorTest.class);

    @Autowired
    private TestApplicationConfiguration applicationConfiguration;

    @Autowired
    private RekognitionProcessor rekognitionProcessor;

    /**
     * 5 Scenarios to detect faces and bounding boxes
     * 1. Man peeking inside the house
     * 2. Man stepping inside the door
     * 3. Man walking out the door
     * 4. Man walking to the door
     * 5. Man walking to the door with cane
     */
    @Test
    public void testLabelsScenarios() throws Exception{

        log.info("Starting test for labels");
        detectFacesForTest("Man peeking inside the house", applicationConfiguration.getManPeekingInsideResource().getInputStream());
        detectFacesForTest("Man stepping in door", applicationConfiguration.getManSteppingInDoorResource().getInputStream());
        detectFacesForTest("Man walking out", applicationConfiguration.getManWalkingOutResource().getInputStream());
        detectFacesForTest("Man walking to door", applicationConfiguration.getManWalkingToDoorResource().getInputStream());
        detectFacesForTest("Man walking to door with cane", applicationConfiguration.getManWalkingToDoorWithCaneResource().getInputStream());

    }

    private void detectFacesForTest(String scenario, InputStream inputStream) throws Exception {
        ByteBuffer imageBytes;
        imageBytes = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
        Image image = new Image().withBytes(imageBytes);
        log.info("-------------------------------------");
        log.info("Detecting faces for - " + scenario );
        log.info("-------------------------------------");
        HashMap<String, BoundingBox> resultingMap = rekognitionProcessor.detectFaces(image);
        for (String s : resultingMap.keySet()){
            log.info("Label: " + s);
            BoundingBox box = resultingMap.get(s);
            log.info("Bounding Box: " + box.toString());
        }
        log.info("-------------------------------------");
    }
}
