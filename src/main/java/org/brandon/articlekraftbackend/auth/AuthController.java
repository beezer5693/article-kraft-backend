package org.brandon.articlekraftbackend.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.brandon.articlekraftbackend.forgotpassword.ForgotPasswordRequestDTO;
import org.brandon.articlekraftbackend.forgotpassword.ResetPasswordRequest;
import org.brandon.articlekraftbackend.payload.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public APIResponse<AuthResponseDTO> register(
      @RequestBody @Valid RegistrationRequest registrationRequest) {
    return authService.registerUser(registrationRequest);
  }

  @PostMapping("/login")
  public APIResponse<AuthResponseDTO> login(@RequestBody @Valid LoginRequest loginRequest) {
    return authService.loginUser(loginRequest);
  }

  @PostMapping("/forgot-password")
  public APIResponse<?> forgotPassword(
      @RequestBody @Valid ForgotPasswordRequestDTO forgotPasswordRequestDTO) {
    return authService.forgotPassword(forgotPasswordRequestDTO);
  }

  @PostMapping("/reset-password")
  public APIResponse<?> resetPassword(
      @RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
    return authService.resetPassword(resetPasswordRequest);
  }
}
