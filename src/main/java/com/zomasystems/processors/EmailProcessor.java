/**
 * @author rahul
 * @created 01/09/2018
 */
package com.zomasystems.processors;

import com.google.common.base.Stopwatch;
import com.zomasystems.model.InputStreamProcessResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;

/**
 * The mail system has two major parts : MTAs and UAs. A message transfer agent (MTA) routes a mail message towards its
 * final destination by sending the message to another MTA. A user agent (UA) interacts with an end-user and allows the
 * user to send and receive mail messages.
 * <p>
 * On the Internet, MTAs communicate with each other using the Simple Mail Transfer Protocol (SMTP). SMTP follows
 * the client/ server model of computing. The SMTP client initiates the dialogue with an SMTP server by sending a
 * message to the remote server. This message includes the following information : the sender address, the recipient
 * address (maybe more than one), and the message itself.
 * <p>
 * Another useful mail protocol is Post Office Protocol (POP). With POP one can set up a machine as a mail drop point.
 * POP lets a userís mailbox reside on a remote host, but allows the user to retrieve the messages from the remote
 * mailbox on demand. As such, POP is user driven, since mail is not transferred until the user requests that be done.
 * After the transfer, the user can read mail on the local system whenever itís convenient. Replies are relayed to the
 * POP server using SMTP. There are two versions of POP -- POP2 and POP3.
 * <p>
 * The SMTP protocol suffers from one significant defect : it understands only 7-bit ASCII characters. Consequently,
 * you canít use SMTP to send binary data such as graphics or programs. Multipurpose Internet Mail Extensions (MIME)
 * provides a standard by specifying a set of encoding rules and header extensions to the Internet standard message
 * specification. These rules let a UA encode messages with text, graphics, and even sound into 7-bit ASCII.
 * <p>
 * source: http://www.perflensburg.se/Privatsida/cp-web/AZXXEP.HTM
 */
@Component
public class EmailProcessor {
    private static final Logger log = LoggerFactory.getLogger(EmailProcessor.class);

    @Autowired
    private InputStreamProcessor inputStreamProcessor;

    @Autowired
    private EmailSession emailSession;

    /**
     * Checks the email based on the configuration properties
     * & calls the input stream processor to process each stream
     *
     * @throws MessagingException
     * @throws IOException
     */
    public void processEmail() throws Exception {
        Message[] messageobjs = emailSession.getMessages();
        InputStreamProcessResult processResult = null;
        /**
         * For each email message received
         * if message has image attachment - process the stream
         */

        try {
            if (messageobjs != null) {
                Stopwatch stopwatch = Stopwatch.createStarted();
                log.debug("//////////////////////////////////////////////");
                log.debug("// Starting stop watch");
                log.debug("//////////////////////////////////////////////");
                for (int i = 0, n = messageobjs.length; i < n; i++) {
                    log.info("Number of email messages to get: " + messageobjs.length);
                    //do some logging for debug
                    log.info("Printing individual messages");
                    log.info("No# " + (i + 1));

                    if (messageobjs[i] != null) {
                        log.info("Email Subject: " + messageobjs[i].getSubject());
                        if (!(messageobjs[i].getContent() instanceof Multipart)) {
                            processResult = inputStreamProcessor.processInputStream(messageobjs[i].getInputStream());
                        } else {
                            try {
                                MimeMultipart multipart = (MimeMultipart) messageobjs[i].getContent();
                                for (int k = 0; k < multipart.getCount(); k++) {
                                    BodyPart bodyPart = multipart.getBodyPart(k);
                                    String[] contentType = bodyPart.getHeader("Content-Type");
                                    log.info("Processing multi-part email with content type: " + contentType[0].toString());
                                    if (contentType[0].contains("text/plain")) {
                                        log.info("Text message, ignoring it");
                                    } else if (contentType[0].contains("application/octet-stream") || contentType[0].contains(" image/jpeg")) {
                                        processResult = inputStreamProcessor.processInputStream(bodyPart.getInputStream());
                                    } else {
                                        log.error("unknown content type: " + contentType[0]);
                                    }

                                }
                            } catch (ClassCastException cce) {
                                log.error("Class Cast Exception ", cce);
                            }
                        }
                    }
                }
                stopwatch.stop();
                log.debug("//////////////////////////////////////////////");
                log.debug("/// Stopped");
                log.debug("//////////////////////////////////////////////");
                log.debug("time: " + stopwatch);

                emailSession.closeFolder();
            } else {
                log.error("MessageObj is null!!");
            }
        } catch (MessagingException err) {
            log.error("Error during processing messages for this session", err);
        }

    }

    public void setEmailSession(EmailSession emailSession) {
        this.emailSession = emailSession;
    }


    public void setInputStreamProcessor(InputStreamProcessor inputStreamProcessor) {
        this.inputStreamProcessor = inputStreamProcessor;
    }
}
