/**
 * @author rahul
 * @created 24/09/2018
 */
package com.zomasystems.processors;

import com.amazonaws.services.rekognition.model.BoundingBox;
import com.amazonaws.services.rekognition.model.Image;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.zomasystems.model.InputStreamProcessResult;
import com.zomasystems.processors.aws.S3Processor;
import com.zomasystems.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

@Component
public class ImageProcessor {
    public static final String PUBLIC_BUCKET = "";
    public static final String PUBLIC_BUCKET_BASE_URI = "";

    private static Logger logger = LoggerFactory.getLogger(ImageProcessor.class);
    @Autowired
    private S3Processor s3Processor;

    public void drawCenteredString(String s, int w, int h, Graphics g) {
        FontMetrics fm = g.getFontMetrics();
        int x = (w - fm.stringWidth(s)) / 2;
        int y = (fm.getAscent() + (h - (fm.getAscent() + fm.getDescent())) / 2);
        g.drawString(s, x, y);
    }

    /**
     * Sets
     * @param inputStream
     * @param textToWrite<String>
     * @param boxes
     * @return BufferedImage
     * @throws Exception
     */
    public BufferedImage generateImageWithText(InputStream inputStream,
                                               Set<String> textToWrite,
                                               Collection<BoundingBox> boxes) throws Exception{

        BufferedImage bufferedImage = ImageIO.read(inputStream);
        int i = 0;
        for(BoundingBox box : boxes) {

            int height = box.getHeight().intValue();
            int width = box.getWidth().intValue();
            int fontSize = 10;


            Graphics graphics = bufferedImage.getGraphics();

            graphics.setColor(Color.YELLOW);

            //for each of the bounding box draw and write the text

            //calculate the font based on the height of the bounding box
            if (height < 10 && height > 5) {
                fontSize = height;
            }

            graphics.setFont(new Font("Calibri", Font.BOLD, 10));

            drawCenteredString(textToWrite.iterator().next(), box.getWidth().intValue(), box.getHeight().intValue(), graphics);
            graphics.drawRect(box.getTop().intValue(), box.getLeft().intValue(), box.getWidth().intValue() - 1, box.getHeight().intValue() - 1);

            i++;
        }

        return bufferedImage;
    }

    public String processToS3(InputStreamProcessResult result) throws Exception {
        String keyName = null;
        if (result != null){
            Image image = result.getImage();


            if(logger.isDebugEnabled()){
                ByteArrayInputStream inputStream2 = new ByteArrayInputStream(image.getBytes().array());
                byte[] buffer = new byte[inputStream2.available()];
                inputStream2.read(buffer);

                File targetFile = new File("targetFile.jpeg");
                OutputStream outStream = new FileOutputStream(targetFile);
                outStream.write(buffer);
            }
            if(image != null) {

                InputStream inputStream = new ByteArrayInputStream(image.getBytes().array());
                inputStream.reset();
                HashMap<String, BoundingBox> boundingBoxHashMap = result.getResultingMetaData();
                if (image != null & boundingBoxHashMap != null) {
                    Date date = new Date();
                    keyName = DateUtils.dateFormatter.format(date) + DateUtils.timeFormatter.format(date) + ".jpg";

                    s3Processor.saveToS3(PUBLIC_BUCKET, keyName, generateImageWithText(inputStream, boundingBoxHashMap.keySet(), boundingBoxHashMap.values()), "image/jpeg", CannedAccessControlList.PublicRead);
                }

                return PUBLIC_BUCKET_BASE_URI + keyName;
            }
        }
        return null;
    }

}
