package com.example.loylogic.hackathon;

/**
 * Created by loylogic on 05/01/18.
 */
//https://stackoverflow.com/questions/4345032/how-to-send-a-simple-email-programatically-exists-a-simple-way-to-do-it
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

public class MailService {
    // public static final String MAIL_SERVER = "localhost";

    private String toList;
    private String ccList;
    private String bccList;
    private String subject;
    final private static String SMTP_SERVER = "smtp.gmail.com";//DataService.getSetting(DataService.SETTING_SMTP_SERVER);
    private static  String username = "404eureka@gmail.com";
    private static  String pass = "Eureka@14";
    private String from;
    private String txtBody;
    private String htmlBody;
    private String replyToList;
//    private ArrayList<Attachment> attachments;
    private boolean authenticationRequired = false;

    public MailService(String from, String toList, String subject, String txtBody, String htmlBody) {
        this.txtBody = txtBody;
        this.htmlBody = htmlBody;
        this.subject = subject;
        this.from = from;
        this.toList = toList;
        this.ccList = null;
        this.bccList = null;
        this.replyToList = null;
        this.authenticationRequired = true;

//        this.attachments = new ArrayList<Attachment>();
//        if (attachment != null) {
//            this.attachments.add(attachment);
//        }
    }

//    public MailService(String from, String toList, String subject, String txtBody, String htmlBody
//                      ) {
//        this.txtBody = txtBody;
//        this.htmlBody = htmlBody;
//        this.subject = subject;
//        this.from = from;
//        this.toList = toList;
//        this.ccList = null;
//        this.bccList = null;
//        this.replyToList = null;
//        this.authenticationRequired = true;
////        this.attachments = attachments == null ? new ArrayList<Attachment>()
////                : attachments;
//    }

    public void sendAuthenticated() throws AddressException, MessagingException {
        authenticationRequired = true;
        send();
    }

    /**
     * Send an e-mail
     *
     * @throws MessagingException
     * @throws AddressException
     */
    public void send() throws AddressException, MessagingException {
        Properties props = new Properties();

        // set the host smtp address
        props.put("mail.smtp.host", SMTP_SERVER);
        props.put("mail.user", from);

        props.put("mail.smtp.starttls.enable", "true");  // needed for gmail
        props.put("mail.smtp.auth", "true"); // needed for gmail
        props.put("mail.smtp.port", "587");  // gmail smtp port

        props.put("mail.username", username);
        props.put("mail.password", pass);
        props.put(" mail.smtp.auth.mechanisms", "login");

//        mail.smtp.auth=true
//        mail.smtp.starttls.enable=true
//        mail.smtp.auth.mechanisms=login
//        mail.smtp.quitwait=false
//        mail.debug = false
//        mail.username=test@gmail.com
//                mail.password=test
//        mail.smtp.host=smtp.gmail.com
//        mail.smtp.port=587
//        mail.smtp.protocol=smtps
//
        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, pass);

            }
        };


        Session session;

        if (authenticationRequired) {
//            SMTPAuthenticator smtpAuthenticator = new SMTPAuthenticator();
//            smtpAuthenticator.getPasswordAuthentication();
//            Authenticator auth = smtpAuthenticator;

            props.put("mail.smtp.auth", "true");
            session = Session.getDefaultInstance(props, auth);
        } else {
            session = Session.getDefaultInstance(props, null);
        }

        // get the default session
        session.setDebug(true);

        // create message
        Message msg = new javax.mail.internet.MimeMessage(session);

        // set from and to address
        try {
            msg.setFrom(new InternetAddress(from, from));
            msg.setReplyTo(new InternetAddress[]{new InternetAddress(from,from)});
        } catch (Exception e) {
            msg.setFrom(new InternetAddress(from));
            msg.setReplyTo(new InternetAddress[]{new InternetAddress(from)});
        }

        // set send date
        msg.setSentDate(Calendar.getInstance().getTime());

        // parse the recipients TO address
        java.util.StringTokenizer st = new java.util.StringTokenizer(toList, ",");
        int numberOfRecipients = st.countTokens();

        javax.mail.internet.InternetAddress[] addressTo = new javax.mail.internet.InternetAddress[numberOfRecipients];

        int i = 0;
        while (st.hasMoreTokens()) {
            addressTo[i++] = new javax.mail.internet.InternetAddress(st
                    .nextToken());
        }
        msg.setRecipients(javax.mail.Message.RecipientType.TO, addressTo);

        // parse the replyTo addresses
        if (replyToList != null && !"".equals(replyToList)) {
            st = new java.util.StringTokenizer(replyToList, ",");
            int numberOfReplyTos = st.countTokens();
            javax.mail.internet.InternetAddress[] addressReplyTo = new javax.mail.internet.InternetAddress[numberOfReplyTos];
            i = 0;
            while (st.hasMoreTokens()) {
                addressReplyTo[i++] = new javax.mail.internet.InternetAddress(
                        st.nextToken());
            }
            msg.setReplyTo(addressReplyTo);
        }

        // parse the recipients CC address
        if (ccList != null && !"".equals(ccList)) {
            st = new java.util.StringTokenizer(ccList, ",");
            int numberOfCCRecipients = st.countTokens();

            javax.mail.internet.InternetAddress[] addressCC = new javax.mail.internet.InternetAddress[numberOfCCRecipients];

            i = 0;
            while (st.hasMoreTokens()) {
                addressCC[i++] = new javax.mail.internet.InternetAddress(st
                        .nextToken());
            }

            msg.setRecipients(javax.mail.Message.RecipientType.CC, addressCC);
        }

        // parse the recipients BCC address
        if (bccList != null && !"".equals(bccList)) {
            st = new java.util.StringTokenizer(bccList, ",");
            int numberOfBCCRecipients = st.countTokens();

            javax.mail.internet.InternetAddress[] addressBCC = new javax.mail.internet.InternetAddress[numberOfBCCRecipients];

            i = 0;
            while (st.hasMoreTokens()) {
                addressBCC[i++] = new javax.mail.internet.InternetAddress(st
                        .nextToken());
            }

            msg.setRecipients(javax.mail.Message.RecipientType.BCC, addressBCC);
        }

        // set header
        msg.addHeader("X-Mailer", "MyAppMailer");
        msg.addHeader("Precedence", "bulk");
        // setting the subject and content type
        msg.setSubject(subject);

        Multipart mp = new MimeMultipart("related");

        // set body message
        MimeBodyPart bodyMsg = new MimeBodyPart();
        bodyMsg.setText(txtBody, "iso-8859-1");
//        if (attachments.size()>0) htmlBody = htmlBody.replaceAll("#filename#",attachments.get(0).getFilename());
//        if (htmlBody.indexOf("#header#")>=0) htmlBody = htmlBody.replaceAll("#header#",attachments.get(1).getFilename());
//        if (htmlBody.indexOf("#footer#")>=0) htmlBody = htmlBody.replaceAll("#footer#",attachments.get(2).getFilename());

        bodyMsg.setContent(htmlBody, "text/html");
        mp.addBodyPart(bodyMsg);

        // set attachements if any
//        if (attachments != null && attachments.size() > 0) {
//            for (i = 0; i < attachments.size(); i++) {
//                Attachment a = attachments.get(i);
//                BodyPart att = new MimeBodyPart();
//                att.setDataHandler(new DataHandler(a.getDataSource()));
//                att.setFileName( a.getFilename() );
//                att.setHeader("Content-ID", "<" + a.getFilename() + ">");
//                mp.addBodyPart(att);
//            }
//        }
        msg.setContent(mp);

        try {
            // send it
            javax.mail.Transport.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    public String getToList() {
        return toList;
    }

    public void setToList(String toList) {
        this.toList = toList;
    }

    public String getCcList() {
        return ccList;
    }

    public void setCcList(String ccList) {
        this.ccList = ccList;
    }

    public String getBccList() {
        return bccList;
    }

    public void setBccList(String bccList) {
        this.bccList = bccList;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTxtBody(String body) {
        this.txtBody = body;
    }

    public void setHtmlBody(String body) {
        this.htmlBody = body;
    }

    public String getReplyToList() {
        return replyToList;
    }

    public void setReplyToList(String replyToList) {
        this.replyToList = replyToList;
    }

    public boolean isAuthenticationRequired() {
        return authenticationRequired;
    }

    public void setAuthenticationRequired(boolean authenticationRequired) {
        this.authenticationRequired = authenticationRequired;
    }

}
