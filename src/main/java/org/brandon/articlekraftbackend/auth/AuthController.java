package org.brandon.articlekraftbackend.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.brandon.articlekraftbackend.handlers.Response;
import org.brandon.articlekraftbackend.helpers.CookieHelper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private static final String ACCESS_TOKEN = "access-token";

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Response<AuthResponseDTO> register(@RequestBody @Valid RegistrationRequest registrationRequest, HttpServletResponse response) {
        AuthResponseDTO authResponseDTO = authService.registerUser(registrationRequest);
        createTokensAndAddToResponseHeader(authResponseDTO, response);
        return Response.success(authResponseDTO, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public Response<AuthResponseDTO> login(@RequestBody @Valid LoginRequest loginRequest, HttpServletResponse response) {
        AuthResponseDTO authResponseDTO = authService.loginUser(loginRequest);
        createTokensAndAddToResponseHeader(authResponseDTO, response);
        return Response.success(authResponseDTO, HttpStatus.OK);
    }

    @GetMapping("/refresh")
    public Response<AuthResponseDTO> refresh(Authentication authentication, HttpServletResponse response) {
        AuthResponseDTO authResponseDTO = authService.refreshAccessToken(authentication);
        createTokensAndAddToResponseHeader(authResponseDTO, response);
        return Response.success(authResponseDTO, HttpStatus.OK);
    }

    private void createTokensAndAddToResponseHeader(AuthResponseDTO authResponseDTO, HttpServletResponse response) {
        Cookie accessTokenCookie = CookieHelper.generateCookie(ACCESS_TOKEN, authResponseDTO.accessToken());
        CookieHelper.attachCookiesToResponseHeader(response, accessTokenCookie);
    }
}
