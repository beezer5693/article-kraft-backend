package org.brandon.articlekraftbackend.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record AuthResponse(
        UserDTO user,
        @JsonProperty("access_token")
        String accessToken
) {
}
