package org.brandon.articlekraftbackend.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.brandon.articlekraftbackend.user.UserDTO;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

@Builder
@JsonInclude(Include.NON_NULL)
public record AuthResponseDTO(
        UserDTO user,
        @JsonProperty("refresh_token")
        String refreshToken,
        @JsonProperty("access_token")
        String accessToken
) {
}
