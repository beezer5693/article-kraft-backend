package org.brandon.articlekraftbackend.services.impl;

import lombok.RequiredArgsConstructor;
import org.brandon.articlekraftbackend.payload.MailBody;
import org.brandon.articlekraftbackend.services.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;

    private static final String SENDER = "noreply@articleKraft.com";

    @Override
    public void sendEmail(MailBody mailBody) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailBody.to());
        message.setFrom(SENDER);
        message.setSubject(mailBody.subject());
        message.setText(mailBody.body());
        mailSender.send(message);
    }
}
