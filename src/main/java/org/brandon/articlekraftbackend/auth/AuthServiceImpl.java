package org.brandon.articlekraftbackend.auth;

import lombok.RequiredArgsConstructor;
import org.brandon.articlekraftbackend.email.EmailService;
import org.brandon.articlekraftbackend.email.ResetPasswordEmailRequest;
import org.brandon.articlekraftbackend.forgotpassword.*;
import org.brandon.articlekraftbackend.token.TokenService;
import org.brandon.articlekraftbackend.user.UserDTO;
import org.brandon.articlekraftbackend.user.UserService;
import org.brandon.articlekraftbackend.user.CustomUserDetails;
import org.brandon.articlekraftbackend.user.User;
import org.brandon.articlekraftbackend.exceptions.EntityAlreadyExistsException;
import org.brandon.articlekraftbackend.payload.*;
import org.brandon.articlekraftbackend.user.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.brandon.articlekraftbackend.utils.UUIDUtils.generateUUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserMapper userMapper;
  private final TokenService tokenService;
  private final UserService userService;
  private final AuthenticationManager authenticationManager;
  private final EmailService emailService;
  private final ForgotPasswordRequestService forgotPasswordRequestService;

  private final static int OTP_EXPIRY_DURATION = 5;
  private final static Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);

  @Override
  public APIResponse<AuthResponseDTO> registerUser(RegistrationRequest request) {
    LOGGER.info("Attempting to register user: {}", request);
    String email = request.email();
    if (userService.existsByEmail(email)) {
      throw new EntityAlreadyExistsException("A user with the email you provided already exists.",
          email);
    }
    User user = userMapper.toUser(request);
    User registeredUser = userService.saveUser(user);
    return generateResponse(registeredUser, HttpStatus.CREATED);
  }

  @Override
  public APIResponse<AuthResponseDTO> loginUser(LoginRequest request) {
    LOGGER.info("Attempting to login user: {}", request);
    authenticateUser(request);
    String email = request.email();
    User user = userService.findUserByEmail(email);
    return generateResponse(user, HttpStatus.OK);
  }

  @Override
  public APIResponse<?> forgotPassword(ForgotPasswordRequestDTO requestDTO) {
    final String email = requestDTO.email();
    User user = userService.findUserByEmail(email);
    forgotPasswordRequestService.deleteForgotPasswordRequestByUser(user);
    var forgotPasswordRequest = generateForgotPasswordRequest(user);
    forgotPasswordRequestService.saveForgotPasswordRequest(forgotPasswordRequest);
    var resetPasswordEmailRequest = generateResetPasswordEmail(user,
        forgotPasswordRequest.getCode());
    emailService.sendEmail(resetPasswordEmailRequest);
    return APIResponse.success(null, HttpStatus.OK);
  }

  @Override
  public APIResponse<?> resetPassword(ResetPasswordRequest request) {
    verifyPasswordAndConfirmPasswordMatch(request);
    ForgotPasswordRequest forgotPasswordRequest = forgotPasswordRequestService.findForgotPasswordRequestByCode(
        request.code());
    String code = forgotPasswordRequest.getCode();
    User user = userService.findUserByResetPasswordCode(code);
    if (forgotPasswordRequestService.isCodeExpired(forgotPasswordRequest)) {
      forgotPasswordRequestService.deleteForgotPasswordRequestByCode(code);
      throw new ResetPasswordCodeExpiredException();
    }
    userService.updatePassword(user, request);
    forgotPasswordRequestService.deleteForgotPasswordRequestByCode(code);
    return APIResponse.success(null, HttpStatus.OK);
  }

  // Throws a Bad Credentials Exception if authentication manager fails to authenticate authToken
  private void authenticateUser(LoginRequest request) {
    LOGGER.info("Attempting to authenticate user: {}", request.email());
    var authToken = UsernamePasswordAuthenticationToken.unauthenticated(request.email(),
        request.password());
    authenticationManager.authenticate(authToken);
  }

  private APIResponse<AuthResponseDTO> generateResponse(User user, HttpStatus status) {
    LOGGER.info("Generating response for user: {}", user.getUserId());
    String token = revokeAndGenerateAccessToken(user);
    UserDTO userDTO = userMapper.toDto(user);
    var authResponseDTO = AuthResponseDTO.builder()
        .user(userDTO)
        .accessToken(token)
        .build();
    return APIResponse.success(authResponseDTO, status);
  }

  private ForgotPasswordRequest generateForgotPasswordRequest(User user) {
    return ForgotPasswordRequest.builder()
        .code(generateUUID())
        .expirationDate(Instant.now().plus(OTP_EXPIRY_DURATION, ChronoUnit.MINUTES))
        .user(user)
        .build();
  }

  private String revokeAndGenerateAccessToken(User user) {
    LOGGER.info("Revoking access token for user: {}", user.getUserId());
    tokenService.revokeAllUserTokens(user);
    String token = tokenService.createToken(new CustomUserDetails(user));
    tokenService.storeToken(token, user);
    return token;
  }

  private static ResetPasswordEmailRequest generateResetPasswordEmail(User user, String code) {
    final String emailSubject = "Article Kraft | Password reset request";
    return ResetPasswordEmailRequest.builder()
        .to(user.getEmail())
        .subject(emailSubject)
        .recipientFirstname(user.getFirstName())
        .resetPasswordCode(code)
        .build();
  }

  private static void verifyPasswordAndConfirmPasswordMatch(ResetPasswordRequest request) {
    var passwordsMatch = request.password().equals(request.confirmPassword());
    if (!passwordsMatch) {
      LOGGER.error("Password and confirm password do not match");
      throw new PasswordsMismatchException();
    }
  }
}
