package com.example.antifurtoappbasic;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInstaller;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/*Classe per la gestione dell'invio delle mail tramite librerie java Mail*/

public class MailService {

    private static String username;
    private static final String password = "android123";

    private static Context mailContext;

    public MailService(String name, Context context) {

        this.username = name;
        this.mailContext = context;
    }

    /*Metodo per l'invio di della mail*/

    public void sendMail(String email, String subject, String messageBody) {
        Session session = createSessionObject();

        try {

            Message message = createMessage(email, subject, messageBody, session);
            new SendMailTask().execute(message);
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /*Metodo per la creazione della mail contenente testo e allegati come foto e audio*/

    private Message createMessage(String email, String subject, String messageBody, Session session) throws MessagingException, UnsupportedEncodingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username + "@gmail.com", "ANTIFURTO APP"));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email, email));
        message.setSubject(subject);

        //message.setText(messageBody);

        String mainPath = Environment.getExternalStorageDirectory().toString() + "/AntiFurtoApp/";
        Multipart mp = new MimeMultipart();

        File filePhoto0 = new File(mainPath + "photo/photo0.jpg");
        if (filePhoto0.exists()) {

            MimeBodyPart photo0 = new MimeBodyPart();
            try {

                photo0.attachFile(filePhoto0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mp.addBodyPart(photo0);
        }


        File filePhoto1 = new File(mainPath + "photo/photo1.jpg");
        if (filePhoto1.exists()) {

            MimeBodyPart photo1 = new MimeBodyPart();
            try {

                photo1.attachFile(filePhoto1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mp.addBodyPart(photo1);
        }


        File fileAudio = new File(mainPath + "audio/registrazione.aac");
        if (fileAudio.exists()) {

            MimeBodyPart audio = new MimeBodyPart();
            try {

                audio.attachFile(fileAudio);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mp.addBodyPart(audio);
        }

        MimeBodyPart text = new MimeBodyPart();
        text.setText(messageBody);
        mp.addBodyPart(text);

        message.setContent(mp);

        return message;
    }

    /*Metodo per il settaggio delle proprietà relative alle mail*/

    private Session createSessionObject() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");

        return Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                Log.i("PROVA AUTENTICAZIONE", "autentico");
                return new PasswordAuthentication(username, password);
            }
        });
    }

    /*Metodo per l'invio tramite AsyncTask*/

    private class SendMailTask extends AsyncTask<Message, Void, Void> {

        @Override
        protected Void doInBackground(Message... messages) {
            try {
                Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
                Transport.send(messages[0]);
                Log.i("PROVA INVIO", "invio");
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
