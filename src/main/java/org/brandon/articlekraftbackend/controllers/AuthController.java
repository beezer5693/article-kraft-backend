package org.brandon.articlekraftbackend.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.brandon.articlekraftbackend.payload.*;
import org.brandon.articlekraftbackend.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public APIResponse<AuthResponse> register(@RequestBody @Valid RegistrationRequest registrationRequest) {
        return authService.registerUser(registrationRequest);
    }

    @PostMapping("/login")
    public APIResponse<AuthResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        return authService.loginUser(loginRequest);
    }

    @PostMapping("/forgot-password")
    public APIResponse<?> forgotPassword(@RequestBody @Valid ForgotPasswordRequest forgotPasswordRequest) {
        return authService.forgotPassword(forgotPasswordRequest);
    }

    @PostMapping("/verify-otp")
    public APIResponse<AuthResponse> verifyOTP(@RequestBody @Valid OTPRequest otpRequest) {
        return authService.verifyOTP(otpRequest);
    }

    @PostMapping("/reset-password/{email}")
    public APIResponse<?> resetPassword(@PathVariable String email, @RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
        return authService.resetPassword(email, resetPasswordRequest);
    }
}
