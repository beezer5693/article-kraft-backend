package org.brandon.articlekraftbackend.email;

import lombok.Builder;

@Builder
public record ResetPasswordEmailRequest(
        String to,
        String subject,
        String recipientFirstname,
        String resetPasswordCode
) {
}
