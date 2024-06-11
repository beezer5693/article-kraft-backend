package org.brandon.articlekraftbackend.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

  private final JavaMailSender mailSender;
  private final TemplateEngine templateEngine;

  @Value("${spring.mail.username}")
  private String SENDER_EMAIL;

  @Value("${frontend-url}")
  private String FRONTEND_URL;

  private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);
  private static final String SENDER_NAME = "noreply@articlekraftsupport.com";
  public static final String UTF8_ENCODING = "UTF-8";
  public static final int PRIORITY_LEVEL = 1;

  @Override
  public void sendEmail(ResetPasswordEmailRequest resetPasswordEmailRequest) {
    try {
      MimeMessage message = getMimeMessage();
      MimeMessageHelper helper = getMimeMessageHelper(message);
      helper.setTo(resetPasswordEmailRequest.to());
      helper.setSubject(resetPasswordEmailRequest.subject());
      String htmlBody = getHtmlContent(resetPasswordEmailRequest);
      helper.setText(htmlBody, true);
      mailSender.send(message);
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }

  private String generateResetPasswordLink(String code) {
    String path = "/reset-password?code=";
    return FRONTEND_URL + path + code;
  }

  private MimeMessage getMimeMessage() {
    return mailSender.createMimeMessage();
  }

  private String getHtmlContent(ResetPasswordEmailRequest resetPasswordEmailRequest) {
    Context context = new Context();
    context.setVariable("name", resetPasswordEmailRequest.recipientFirstname());
    context.setVariable("resetPasswordLink",
        generateResetPasswordLink(resetPasswordEmailRequest.resetPasswordCode()));
    return templateEngine.process("reset-password", context);
  }

  private MimeMessageHelper getMimeMessageHelper(MimeMessage message)
      throws MessagingException, UnsupportedEncodingException {
    MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF8_ENCODING);
    helper.setPriority(PRIORITY_LEVEL);
    helper.setFrom(SENDER_EMAIL, SENDER_NAME);
    return helper;
  }
}
