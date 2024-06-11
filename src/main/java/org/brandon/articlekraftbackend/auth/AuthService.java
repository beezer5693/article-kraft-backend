package org.brandon.articlekraftbackend.auth;

import org.brandon.articlekraftbackend.forgotpassword.ForgotPasswordRequestDTO;
import org.brandon.articlekraftbackend.forgotpassword.ResetPasswordRequest;
import org.brandon.articlekraftbackend.payload.*;

public interface AuthService {

  APIResponse<AuthResponseDTO> registerUser(RegistrationRequest registrationRequest);

  APIResponse<AuthResponseDTO> loginUser(LoginRequest loginRequest);

  APIResponse<?> forgotPassword(ForgotPasswordRequestDTO forgotPasswordRequestDTO);

  APIResponse<?> resetPassword(ResetPasswordRequest resetPasswordRequest);
}
