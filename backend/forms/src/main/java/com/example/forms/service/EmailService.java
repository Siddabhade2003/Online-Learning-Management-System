package com.example.forms.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import javax.mail.MessagingException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.util.FileCopyUtils;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendVerificationEmail(String to, String verificationToken, String domainUrl) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

            String emailContent = getEmailContent(verificationToken, domainUrl);
            helper.setTo(to);
            helper.setSubject("Account Verification");
            helper.setText(emailContent, true);

            javaMailSender.send(mimeMessage);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception
        } catch (jakarta.mail.MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private String getEmailContent(String verificationToken, String domainUrl) throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/email_templates.html");
        byte[] fileBytes = FileCopyUtils.copyToByteArray(resource.getInputStream());
        String emailTemplate = new String(fileBytes, StandardCharsets.UTF_8);

        // Replace placeholders with actual values
        emailTemplate = emailTemplate.replace("$VERIFICATION_TOKEN$", verificationToken);
        emailTemplate = emailTemplate.replace("$DOMAIN_URL$", domainUrl);

        return emailTemplate;
    }
}
