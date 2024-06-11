package org.brandon.articlekraftbackend.forgotpassword;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank(message = "Code cannot be blank")
        String code,
        @NotBlank(message = "Password cannot be blank")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,
        @JsonProperty("confirm_password")
        @NotBlank(message = "Confirm password cannot be blank")
        String confirmPassword
) {
}
