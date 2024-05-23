package org.brandon.articlekraftbackend.auth;

import org.springframework.security.core.Authentication;

public interface AuthService {
    AuthResponseDTO registerUser(RegistrationRequest registrationRequest);

    AuthResponseDTO loginUser(LoginRequest loginRequest);

    AuthResponseDTO refreshAccessToken(Authentication authentication);
}
