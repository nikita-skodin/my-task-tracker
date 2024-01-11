package com.skodin.util.mail;

import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Log4j2
@Component
@RequiredArgsConstructor
public class MailSandler {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;


    @Async
    public void sendActivationCodeMessage(String to, String subject, String link, String userName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            Context context = new Context();

            context.setVariables(Map.of("name", userName, "url", link));
            String text = templateEngine.process("emailTemplate", context);

            MimeMultipart mimeMultipart = new MimeMultipart("html");
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(text, "text/html");
            mimeMultipart.addBodyPart(messageBodyPart);

            message.setContent(mimeMultipart);
            message.setSubject(subject);
            message.setRecipients(Message.RecipientType.TO, to);

            mailSender.send(message);
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }

    }

}

