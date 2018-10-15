/**
 * @author rahul
 * @created 24/09/2018
 */
package com.zomasystems.processors;

import com.amazonaws.services.rekognition.model.BoundingBox;
import com.zomasystems.config.TestApplicationConfiguration;
import com.zomasystems.utils.DateUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ImageProcessorTest {


    @Autowired
    private ImageProcessor imageProcessor;

    @Autowired
    private DateUtils dateUtils;

    @Autowired
    private TestApplicationConfiguration testApplicationConfiguration;

    public static boolean compareImage(File fileA, File fileB) {
        try {
            // take buffer data from botm image files //
            BufferedImage biA = ImageIO.read(fileA);
            DataBuffer dbA = biA.getData().getDataBuffer();
            int sizeA = dbA.getSize();
            BufferedImage biB = ImageIO.read(fileB);
            DataBuffer dbB = biB.getData().getDataBuffer();
            int sizeB = dbB.getSize();
            // compare data-buffer objects //
            if (sizeA == sizeB) {
                for (int i = 0; i < sizeA; i++) {
                    if (dbA.getElem(i) != dbB.getElem(i)) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println("Failed to compare image files ...");
            return false;
        }

    }



    /**
     *
     */
    @Test
    public void testImage() throws Exception {

        Set<String> textToWrite = new LinkedHashSet<>();
        List<BoundingBox> boxes = new ArrayList<>();

        BoundingBox box1 = new BoundingBox();
        box1.setLeft(0f);
        box1.setTop(0f);
        box1.setHeight(10f);
        box1.setWidth(200f);
        textToWrite.add("This is box 1");

        BoundingBox box2 = new BoundingBox();
        box2.setLeft(10f);
        box2.setTop(20f);
        box2.setHeight(50f);
        box2.setWidth(20f);
        textToWrite.add("Box2");

        boxes.add(box1);
        boxes.add(box2);

        FileInputStream src = new FileInputStream(testApplicationConfiguration.getSourceResource().getFile());
        File targetFile = new File(dateUtils.generateDateTimeString() + ".jpg");
        BufferedImage bufferedImage =imageProcessor.generateImageWithText(src, textToWrite, boxes);

        ImageIO.write(bufferedImage, "jpg", targetFile);

//        target.flush();
//        target.close();
//        assertTrue(compareImage(targetResource.getFile(), targetFile));

    }

    @Test
    public void testNonJPEGImage() {

    }

    @Test
    public void testInvalidImageEncoding() {

    }

}
