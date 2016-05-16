package lt.milkusteam.cloud.core.validation;

import lt.milkusteam.cloud.core.model.User;
import lt.milkusteam.cloud.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Locale;
import java.util.Properties;
import java.util.UUID;

//import org.springframework.mail.javamail.JavaMailSender;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {
    @Autowired
    private UserService service;
    @Autowired
    private MessageSource messages;
    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {

        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        service.createVerificationToken(user.getUsername(), token);
        String recipientAddress = user.getEmail();
        String subject = "Registration Confirmation";
        String confirmationUrl = event.getAppUrl() + "/successRegistration?token=" + token;
        Locale locale = new  Locale("en");
        String mailMessage = messages.getMessage("register.send", null, locale);
        SimpleMailMessage email = new SimpleMailMessage();

        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(mailMessage + " rn " + "http://localhost:8080" + confirmationUrl);
        String messa= new String (mailMessage + " rn " + "http://localhost:8080" + confirmationUrl);
        System.out.println(messa);
        new RegistrationListener().email(recipientAddress,messa,subject);

    }
     public void email(String to, String text, String subject){
        // Sender's email ID needs to be mentioned
        String from = "dddcloudmanager@yahoo.com";
        String pass ="jonera45ismestaskablys";
        // Recipient's email ID needs to be mentioned.
        //String to = "arnoldas8@gmail.com";
        String host = "smtp.mail.yahoo.com";

        // Get system properties
        Properties properties = System.getProperties();
        // Setup mail server
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.user", from);
        properties.put("mail.smtp.password", pass);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");

        // Get the default Session object.
        Session session = Session.getDefaultInstance(properties);

        try{
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(to));

            // Set Subject: header field
            message.setSubject(subject);

            // Now set the actual message
            message.setText(text);

            // Send message
            Transport transport = session.getTransport("smtp");
            transport.connect(host, from, pass);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
            System.out.println("Sent message successfully....");
        }catch (MessagingException mex) {
            mex.printStackTrace();
        }
    }







}
