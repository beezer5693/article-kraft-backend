package org.brandon.articlekraftbackend.services;

import org.brandon.articlekraftbackend.payload.MailBody;

public interface EmailService {
    void sendEmail(MailBody mailBody);
}
