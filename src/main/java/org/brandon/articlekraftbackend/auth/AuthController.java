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
  public APIResponse<AuthResponseDTO> register(@RequestBody @Valid RegistrationRequest request) {
    return authService.registerUser(request);
  }

  @PostMapping("/login")
  public APIResponse<AuthResponseDTO> login(@RequestBody @Valid LoginRequest request) {
    return authService.loginUser(request);
  }

  @PostMapping("/forgot-password")
  public APIResponse<?> forgotPassword(
      @RequestBody @Valid ForgotPasswordRequestDTO requestDTO) {
    return authService.forgotPassword(requestDTO);
  }

  @PostMapping("/reset-password")
  public APIResponse<?> resetPassword(@RequestBody @Valid ResetPasswordRequest request) {
    return authService.resetPassword(request);
  }
}
