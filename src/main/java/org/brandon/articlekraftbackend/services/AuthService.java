package org.brandon.articlekraftbackend.services;

import org.brandon.articlekraftbackend.payload.*;

public interface AuthService {
    APIResponse<AuthResponse> registerUser(RegistrationRequest registrationRequest);

    APIResponse<AuthResponse> loginUser(LoginRequest loginRequest);

    APIResponse<?> forgotPassword(ForgotPasswordRequest forgotPasswordRequest);

    APIResponse<AuthResponse> verifyOTP(OTPRequest otpRequest);

    APIResponse<?> resetPassword(String email, ResetPasswordRequest resetPasswordRequest);
}
