/**
 * @author rahul
 * @created 08/09/2018
 */
package com.zomasystems.processors;

import com.zomasystems.config.TestApplicationConfiguration;
import com.zomasystems.model.InputStreamProcessResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class EmailProcessorTest {
    private InputStream inputStream;

    private Message[] messages;
    private InputStreamProcessResult processResult;

    private InputStreamProcessor inputStreamProcessor;
    private SMSProcessor smsProcessor;

    private EmailProcessor emailProcessor = new EmailProcessor();
    @Autowired
    private TestApplicationConfiguration testApplicationConfiguration;


    private EmailSession emailSession;


    /**
     * Setup the email session by setting up the email storeObject and
     * folder
     *
     * @throws IOException
     * @throws MessagingException
     */
    @Before
    public void setup() throws Exception {

        //mock objects used by the processor
        emailSession = mock(EmailSession.class);
        smsProcessor = mock(SMSProcessor.class);
        inputStreamProcessor = mock(InputStreamProcessor.class);


        emailProcessor.setEmailSession(emailSession);
        //    emailProcessor.setSmsProcessor(smsProcessor);
        emailProcessor.setInputStreamProcessor(inputStreamProcessor);
    }

    /**
     * Sets-up messages for testing
     *
     * @param numberOfMessagesToSetup -- number of messages to setup
     * @param inputStream             - the inputstream containing the contents of the message
     * @return messagesCreated
     * @throws IOException
     * @throws MessagingException
     */
    private void setUpMessages(int numberOfMessagesToSetup, InputStream inputStream) throws IOException, MessagingException {
        messages = new MimeMessage[numberOfMessagesToSetup];

        for (int i = 0; i < numberOfMessagesToSetup; i++) {
            //Multipurpose Internet Mail Extensions (MIME)
            messages[i] = mock(MimeMessage.class);
            when(messages[i].getSubject()).thenReturn("test");
            //  when(messages[i].getFrom()).thenReturn(new InternetAddress("test@testsystems.com"));
            when(messages[i].getInputStream()).thenReturn(inputStream);
        }
    }


    /**
     * Tests a message with photo attachment
     *
     * @throws IOException
     * @throws MessagingException
     */
    @Test
    public void testProcessMessage() throws Exception {
        //generate a byte stream from test image
        InputStream inputStream = testApplicationConfiguration.getSourceResource().getInputStream();
        setUpMessages(1, testApplicationConfiguration.getSourceResource().getInputStream());

        String googlePrediction = "NoOne - 90";
        processResult = new InputStreamProcessResult();
        when(emailSession.getMessages()).thenReturn(messages);
//        when(googleProcessor.predictGoogle(anyObject(), anyString(), anyString(), anyString())).thenReturn(googlePrediction);
//        when(visionProcessor.getImageLabels(anyObject())).thenReturn("");
        when(inputStreamProcessor.processInputStream(any(ByteArrayInputStream.class))).thenReturn(processResult);
        // doNothing().when(smsProcessor).sendMessage(any(InputStreamProcessResult.class));
        emailProcessor.processEmail();
        verify(inputStreamProcessor, atLeast(1)).processInputStream(anyObject());
//        verify(smsProcessor, atLeast(1)).sendMessage(anyObject());
//        verify(googleProcessor, atLeast(1)).predictGoogle(anyObject(), anyString(), anyString(), anyString());
//        verify(visionProcessor).getImageLabels(any(Image.class));
    }

    @Test
    public void testNoImageMessage() {

    }

    @Test
    public void testMultipleImagesMessage() {

    }

    @Test
    public void testEmptyContentMessage() {

    }

    /**
     * An email folder may contain multiple messages, once  folder is open
     * and a session is created for that folder, EmailProcessor should read
     * a message one time and mark it as read and not read the messages over
     * and over again.
     */
    @Test
    public void testMessagesAreReadOnlyOnce() {

    }

    /**
     * If an image is bigger than 5MB which is the limit for rekognition service
     */
    @Test
    public void testMaximumMessageSize() {

    }
}
