package org.brandon.articlekraftbackend.payload;

import lombok.Builder;

@Builder
public record MailBody(String to, String subject, String body) {
}
