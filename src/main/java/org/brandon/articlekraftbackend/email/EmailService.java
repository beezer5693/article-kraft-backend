package org.brandon.articlekraftbackend.email;

public interface EmailService {
    void sendEmail(ResetPasswordEmailRequest resetPasswordEmailRequest);
}
