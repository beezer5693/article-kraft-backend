package org.brandon.articlekraftbackend.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserDTO(
        @JsonProperty("user_id")
        String userId,
        @JsonProperty("first_name")
        @NotBlank(message = "First name cannot be blank")
        String firstName,
        @JsonProperty("last_name")
        @NotBlank(message = "Last name cannot be blank")
        String lastName,
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Invalid email address")
        String email,
        @NotBlank(message = "Password cannot be blank")
        @Size(min = 8, message = "Password must be at least 8 characters")
        @JsonProperty(access = Access.WRITE_ONLY)
        String password,
        Role role
) {

}
