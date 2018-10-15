/**
 * @author rahul
 * @created 29/09/2018
 */
package com.zomasystems.processors;

import com.zomasystems.config.GmailServerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.mail.*;
import java.util.Properties;

@Component
public class EmailSession {

    private static final Logger log = LoggerFactory.getLogger(EmailSession.class);

    protected Store storeObj;
    protected Session emailSessionObj;

    @Autowired
    private GmailServerProperties gmailServerProperties;

    protected Folder emailFolderObj;


    private static final String MAIL_HOST = "mail.pop3.host";
    private static final String MAIL_PORT = "mail.pop3.port";
    private static final String MAIL_TLS_ENABLE = "mail.pop3.starttls.enable";
    public static final String MAIL_POP3_SOCKET_FACTORY = "mail.pop3.socketFactory";
    public static final String MAIL_POP3_SOCKET_FACTORY_CLASS = "mail.pop3.socketFactory.class";
    public static final String MAIL_POP3_PORT = "mail.pop3.port";

    @PostConstruct
    private void setup(){
        try {
            setPropertiesForEmailSession();
        }catch(Exception err){
            log.error("An error occured while creating an email session", err);
        }
    }
    /**
     * Creates a sesssion for the email and then returns new messages
     *
     * @return
     * @throws MessagingException
     */
    public Message[] getMessages() throws MessagingException {
        Message[] messages = null;
        if (emailSessionObj != null && storeObj != null){

            //Create POP3 store object and connect with the server
            storeObj = emailSessionObj
                    .getStore(gmailServerProperties.getProtocol());


            storeObj.connect(gmailServerProperties.getHost(), gmailServerProperties.getPort(),
                    gmailServerProperties.getEmail(), gmailServerProperties.getPassword());
            //Create folder object and open it in read-only mode
            emailFolderObj = storeObj.getFolder(gmailServerProperties.getFolder());
            emailFolderObj.open(Folder.READ_WRITE);
            //Fetch messages from the folder and print in a loop
            messages = emailFolderObj.getMessages();

        } else {
            log.error("Null storeObj or null emailSessionObj found");
        }

        return messages;
    }

    private void setPropertiesForEmailSession() throws NoSuchProviderException {
        Properties propvals = new Properties();
        propvals.put(MAIL_HOST, gmailServerProperties.getHost());
        propvals.put(MAIL_PORT, gmailServerProperties.getPort());
        propvals.put(MAIL_TLS_ENABLE, gmailServerProperties.isEnableTLS());
        propvals.put(MAIL_POP3_SOCKET_FACTORY, gmailServerProperties.getPort());
        propvals.put(MAIL_POP3_SOCKET_FACTORY_CLASS, gmailServerProperties.getFactoryClass());
        propvals.put(MAIL_POP3_PORT, gmailServerProperties.getPort());
        emailSessionObj = Session.getDefaultInstance(propvals);

        log.info("Was setting up all the properties");
        if (emailSessionObj != null) {
            log.info("setting up store object");
            storeObj = emailSessionObj.getStore("pop3");
            if (storeObj == null) {
                log.info("store object is null");
            }
        } else {
            log.info("Email session object was null");
        }
    }

    public void closeFolder(){
        try {
            if(emailFolderObj != null) {
                emailFolderObj.close(false);
            }
        }catch(MessagingException err){
            log.error(err.getMessage(), err);
        }
    }

}
