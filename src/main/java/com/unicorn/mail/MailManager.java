package com.unicorn.mail;


import com.unicorn.base.logger.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class MailManager {
    /**
     * Utility method to send simple HTML email
     * @param session -Session
     * @param toEmail -MailId of Receiver
     * @param fromEmail -MailId of Sender
     * @param fromName -Name to be displayed for Sender
     * @param subject -Subject of mail
     * @param body -MailBody
     */
    private static void sendEmail(Session session, String fromEmail, String fromName, String toEmail, String subject, String body){
        try
        {
            MimeMessage msg = new MimeMessage(session);
            //set message headers
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");
            msg.setFrom(new InternetAddress(fromEmail, fromName));
            msg.setReplyTo(InternetAddress.parse(fromEmail, false));
            msg.setSubject(subject, "UTF-8");
            msg.setText(body, "UTF-8");
            msg.setSentDate(new Date());
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            Logger.info("Message is ready");
            Transport.send(msg);
            Logger.info("EMail Sent Successfully!!");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        String content = "";
        StringBuilder contentBuilder = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new FileReader(System.getProperty("user.dir")+ File.separator+"Target"+File.separator+"Emailable.html"));
            String str;
            while ((str = in.readLine()) != null) {
                contentBuilder.append(str);
            }
            in.close();
        } catch (Exception e) {}

         content = contentBuilder.toString();
        Logger.info("Start");
        String smtpHostServer = "10.44.16.251";
        String fromName = "NoReply-WU";
        String fromMailID = "no_reply@westernunion.com";
        String toMailID = "mayur.patil2@westernunion.com";
        String mailBody= content;
        Properties props = System.getProperties();
        props.put("mail.smtp.host", smtpHostServer);
        Session session = Session.getDefaultInstance(props, null);
        sendEmail(session,fromMailID, fromName,toMailID,"Test Execution Summary", mailBody);
    }

}