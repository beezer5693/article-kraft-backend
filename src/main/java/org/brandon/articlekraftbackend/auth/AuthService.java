package org.brandon.articlekraftbackend.auth;

import org.brandon.articlekraftbackend.handlers.Response;
import org.springframework.security.core.Authentication;

public interface AuthService {
    Response<AuthResponseDTO> registerUser(RegistrationRequest registrationRequest);

    Response<AuthResponseDTO> loginUser(LoginRequest loginRequest);

    Response<AuthResponseDTO> refreshAccessToken(Authentication authentication);
}
