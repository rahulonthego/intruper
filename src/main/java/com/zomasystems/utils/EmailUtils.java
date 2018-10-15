/**
 * @author rmalhotra
 * @created 02/09/2018
 */
package com.zomasystems.utils;

import com.zomasystems.processors.aws.S3Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;


public class EmailUtils {

    private static final Logger log = LoggerFactory.getLogger(EmailUtils.class);

    private S3Processor s3Processor;

    //

    /**
     * Processes an image that is attached as an input stream to a mail message.
     * It then saves the image as:
     *  Email_Processor_Save_Directory>/TODAYs_DATE/CURRENT_TIME.jpg
     * @param indvidualmsg
     */
    public void processImage(Message indvidualmsg){

        try {
            InputStream inputStream = indvidualmsg.getInputStream();
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);


            //save the file locally to the directory provided
            Date date = new Date();
            String folderName = DateUtils.dateFormatter.format(date);
            String timeKeyName = DateUtils.timeFormatter.format(date) + ".jpg";

            String folderPath =  "";//emailProcessorProperties.getSaveDirectory() + '/' + folderName;
            String path = folderPath + "/" + timeKeyName;
            createFolderIfNeeded(folderPath);
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            fileOutputStream.write(buffer);
            fileOutputStream.close();

            //Now copy the file over to S3
            //s3Processor.saveToS3(timeKeyName, new File(path));
        }catch(MessagingException | IOException err){
            log.info("Cannot process Image, ", err);
        }
    }

    protected void createFolderIfNeeded(String folderName){
        File folder = new File(folderName);
        if (!folder.exists()){
            folder.mkdir();
        }
    }

    public void multiPartProcessor(Message indvidualmsg) {

        try {
            // store attachment file name, separated by comma
            String attachFiles = "";
            //save the file locally to the directory provided
            Date date = new Date();
            String folderName = DateUtils.dateFormatter.format(date);
            createFolderIfNeeded(folderName);

            // content may contain attachments
            Multipart multiPart = (Multipart) indvidualmsg.getContent();
            int numberOfParts = multiPart.getCount();
            for (int partCount = 0; partCount < numberOfParts; partCount++) {
                MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                    // this part is attachment
                    String fileName = part.getFileName();
                    attachFiles += fileName + ", ";
                    part.saveFile(folderName + File.separator + fileName);
                }
            }

            if (attachFiles.length() > 1) {
                attachFiles = attachFiles.substring(0, attachFiles.length() - 2);
            }
        }catch(MessagingException |IOException err){
            log.error("Unable to process multi-part message", err);
        }
    }

    public void processTextContent(Message indvidualmsg) {
        try {
            Object content = indvidualmsg.getContent();
            if (content != null) {

                String contentType = indvidualmsg.getContentType();
                String messageContent = "";
                messageContent = content.toString();
                System.out.println("Content: " + messageContent);
            }
        } catch (IOException | MessagingException err) {
            log.error("unable to process text content of the message ", err);
        }
    }

    public void setS3Processor(S3Processor s3Processor) {
            this.s3Processor = s3Processor;
    }

}
