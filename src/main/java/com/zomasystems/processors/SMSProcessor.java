/**
 * @author rahul
 * @created 22/09/2018
 */
package com.zomasystems.processors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.twilio.Twilio;
import com.twilio.converter.Promoter;
import com.twilio.rest.api.v2010.account.Message;
import com.zomasystems.model.InputStreamProcessResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.util.concurrent.Callable;

public class SMSProcessor implements Callable<Boolean> {

    private static final Logger log = LoggerFactory.getLogger(SMSProcessor.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    public static final String ACCOUNT_SID = "";
    public static final String AUTH_TOKEN = "";
    public static final String CUSTOMER_NUMBER = "";
    public static final String FROM = "";
    public static final String PUBLIC_BUCKET_BASE_URI = "";

    private InputStreamProcessResult result;
    private ImageProcessor imageProcessor;

    public SMSProcessor(ImageProcessor imageProcessor, InputStreamProcessResult result){
        this.result = result;
        this.imageProcessor = imageProcessor;
    }

    public void sendTextMessage(String body) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message message = Message.creator(
                new com.twilio.type.PhoneNumber(CUSTOMER_NUMBER),
                new com.twilio.type.PhoneNumber(FROM),
                body)
                .create();
        log.info("Message sent with details with sid: " + message.getSid());

    }

    public void sendMediaMessage(String body, URI uri) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message message = com.twilio.rest.api.v2010.account.Message.creator(
                new com.twilio.type.PhoneNumber(CUSTOMER_NUMBER),
                new com.twilio.type.PhoneNumber(FROM),
                body)
                .setMediaUrl(Promoter.listOfOne(uri))
                .create();

        log.info("Media message sent with details with sid: " + message.getSid());
    }

    public Boolean call() throws Exception {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String resultString = mapper.writeValueAsString(result);
        System.out.println("***************************************************************************************************");
        System.out.println(resultString);
        System.out.println("***************************************************************************************************");
        System.out.println("***************************************************************************************************");
        System.out.println("***************************************************************************************************");
        System.out.println("***************************************************************************************************");
        sendMediaMessage(resultString, new URI(imageProcessor.processToS3(result)));

        return true;
    }

}
