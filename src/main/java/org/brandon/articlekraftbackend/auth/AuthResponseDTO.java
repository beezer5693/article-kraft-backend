package org.brandon.articlekraftbackend.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import org.brandon.articlekraftbackend.user.UserDTO;

@Builder
public record AuthResponseDTO(
    UserDTO user,
    @JsonProperty("access_token")
    String accessToken
) {

}
