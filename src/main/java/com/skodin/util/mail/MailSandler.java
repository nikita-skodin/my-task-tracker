package com.skodin.util.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailSandler {

    private final JavaMailSender emailSender;
    private final MailProperties mailProperties;

    @Async
    public void sendSimpleMessage(
            String to, String subject, String text) {

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailProperties.getUsername());
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            emailSender.send(message);

    }
}
