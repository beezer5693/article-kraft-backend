package org.brandon.articlekraftbackend.forgotpassword;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequestDTO(
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Invalid email address")
        String email
) {
}
