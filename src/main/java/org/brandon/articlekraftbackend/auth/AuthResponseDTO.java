package org.brandon.articlekraftbackend.auth;

import lombok.Builder;
import org.brandon.articlekraftbackend.user.UserDTO;

@Builder
public record AuthResponseDTO(
        UserDTO user,
        String accessToken
) {
}
