package com.example.testingLogIn.PasswordResetPackage;

import com.example.testingLogIn.ModelDTO.UserDTO;
import com.example.testingLogIn.WebsiteConfiguration.WebsiteConfigurationServices;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class EmailService {

    private final Properties prop;
    private final Authenticator authenticator;
    private final Session session;
    private final String username;
    private final String websiteAddress = "www.alfurqan-islamic-institute.site";
    @Autowired
    private WebsiteConfigurationServices webService;
    public EmailService(){
        username = "customerservicealfurqanislamic@gmail.com";
        String password = "xlfp ycim vsoz zizu";
        prop = new Properties();
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");

        authenticator = new Authenticator(){
            protected PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication(username,password);
            }
        };

        session = Session.getInstance(prop,authenticator);
    }

    public void sendEmail(String receiver){
        Message msg = new MimeMessage(session);
        try {
            msg.setFrom(new InternetAddress("customerservicealfurqanislamic@gmail.com"));
            msg.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(receiver));
            msg.setSubject("Testing email");
            msg.setText("Testing");
            Transport.send(msg);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendPasswordResetEmail(String recipientEmail, String token) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(recipientEmail));
            message.setSubject("Password Reset Request");
            String resetLink = "http://"+websiteAddress+"/reset-password/" + token;
            // HTML email content
            String htmlContent = 
                    "<h3>Password Reset</h3>"
                    + "<p>We received a request to reset your password. Click the link below:</p>"
                    + "<p><a href=\""+resetLink+"\">"+" Reset Password </a></p>"
                    + "<p>This link will expire in 24 hours.</p>"
                    + "<p>If you didn't request this, please ignore this email.</p>";

            message.setContent(htmlContent, "text/html; charset=utf-8");
            Transport.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public void sendAccountConfirmation(String recipientEmail, String token) {
        String websiteName = webService.getName();
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(recipientEmail));
            message.setSubject("Your account confirmation for "+websiteName);
            String htmlContent = getString(recipientEmail, token);
            message.setContent(htmlContent, "text/html; charset=utf-8");
            Transport.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public void registrationComplete(String recipientEmail){
        try {
            String websiteName = webService.getName();
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(recipientEmail));
            message.setSubject("Account Registration Complete");
            String htmlContent =
                    "<p>Welcome to"+websiteName+"!</p>" +
                    "<p>We're excited to have you. Click below to log in and get started:</p>" +
                    "<p><a href='http://websiteaddress'>Log in now</a></p>"
                    .replace("websiteaddress",websiteAddress);

            message.setContent(htmlContent, "text/html; charset=utf-8");
            Transport.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String getString(String recipientEmail, String token) {
        String schoolEmail = webService.getEmail();
        String confirmLink = "http://"+websiteAddress+"/account-confirmation/" + token;
        String websiteName = webService.getName();

       return """
                <h3>Hello receiver,</h3>
                <p>Our staff has created an account for you on websiteName using this email address as your login credential.</p>
                </br>
                <p>To activate your account and set a password, please click below:</p>
                <p><a href="confirmLink">Activate Account</a></p>
                <p>This link will expire in 24 hours.</p>
                </br>
                <p>If you did not register on websiteName, please ignore this email or contact our support team at</p>
                <p>schoolEmail</p>
                </br>
                <p>Thanks,</p>
                <p>websiteName Team</p>
                """
                .replace("receiver", recipientEmail)
                .replace("confirmLink",confirmLink)
                .replace("schoolEmail",schoolEmail)
                .replace("websiteName",websiteName);
    }
}
