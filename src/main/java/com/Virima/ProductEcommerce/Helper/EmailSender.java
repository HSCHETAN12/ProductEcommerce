package com.Virima.ProductEcommerce.Helper;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailSender {

    @Autowired
    JavaMailSender mailSender;


    public void sendOtp(String to, int otp, String name) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            // Set the sender's email and name
            helper.setFrom("chetanyash5@gmail.com", "ecommerce Application");

            // Set the recipient email
            helper.setTo(to);

            // Set the email subject
            helper.setSubject("Verify your mail through OTP");

            // Create the plain text or HTML content for the email
            String emailContent = "<html><body>"
                    + "<h3>Hello, " + name + "!</h3>"
                    + "<p>Your OTP for verifying your email is: <b>" + otp + "</b></p>"
                    + "<p>If you didn't request this, please ignore this email.</p>"
                    + "</body></html>";

            // Set the HTML content (true indicates HTML content)
            helper.setText(emailContent,true);

            // Send the email
            mailSender.send(message);

        } catch (Exception e) {
            // Log the error (you can use a logger in real projects)
            e.printStackTrace();
        }
    }
}
