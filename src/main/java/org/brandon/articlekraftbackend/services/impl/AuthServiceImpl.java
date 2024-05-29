package org.brandon.articlekraftbackend.services.impl;

import lombok.RequiredArgsConstructor;
import org.brandon.articlekraftbackend.config.CustomUserDetails;
import org.brandon.articlekraftbackend.entities.OTP;
import org.brandon.articlekraftbackend.entities.Token;
import org.brandon.articlekraftbackend.entities.User;
import org.brandon.articlekraftbackend.exceptions.EntityAlreadyExistsException;
import org.brandon.articlekraftbackend.exceptions.OTPExpiredException;
import org.brandon.articlekraftbackend.exceptions.PasswordsMismatchException;
import org.brandon.articlekraftbackend.payload.*;
import org.brandon.articlekraftbackend.services.*;
import org.brandon.articlekraftbackend.utils.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Date;

import static org.brandon.articlekraftbackend.enums.TokenType.ACCESS_TOKEN;
import static org.brandon.articlekraftbackend.utils.UUIDUtil.generateUUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserMapper userMapper;
    private final TokenService tokenService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final OTPService otpService;

    private final static int OTP_EXPIRY_DURATION = 70 * 1000;
    private final static Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Override
    public APIResponse<AuthResponse> registerUser(RegistrationRequest registrationRequest) {
        LOGGER.info("Attempting to register user: {}", registrationRequest);
        String email = registrationRequest.email();
        if (userService.existsByEmail(email)) {
            final String errorMessage = "A user with the email you provided already exists.";
            throw new EntityAlreadyExistsException(errorMessage, email);
        }
        User user = userMapper.toUser(registrationRequest);
        User registeredUser = userService.saveUser(user);
        return generateResponse(registeredUser, HttpStatus.CREATED);
    }

    @Override
    public APIResponse<AuthResponse> loginUser(LoginRequest loginRequest) {
        LOGGER.info("Attempting to login user: {}", loginRequest);
        authenticateUser(loginRequest);
        String email = loginRequest.email();
        User user = userService.findUserByEmail(email);
        return generateResponse(user, HttpStatus.OK);
    }

    @Override
    public APIResponse<?> forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        User user = userService.findUserByEmail(forgotPasswordRequest.email());
        OTP otp = generateOTP(user);
        MailBody mailBody = generateEmail(user, otp.getOtp());
        emailService.sendEmail(mailBody);
        otpService.saveOTP(otp);
        return APIResponse.success(null, HttpStatus.OK);
    }

    @Override
    public APIResponse<AuthResponse> verifyOTP(OTPRequest otpRequest) {
        String email = otpRequest.email();
        User user = userService.findUserByEmail(email);
        OTP otp = otpService.findByOTPAndUser(otpRequest, user);
        if (!otpService.isOTPValid(otp)) {
            otpService.deleteOTP(otp);
            throw new OTPExpiredException("OTP had expired.");
        }
        return APIResponse.success(null, HttpStatus.OK);
    }

    @Override
    public APIResponse<?> resetPassword(String email, ResetPasswordRequest resetPasswordRequest) {
        validatePasswordMatch(resetPasswordRequest);
        User user = userService.findUserByEmail(email);
        userService.updatePassword(user, resetPasswordRequest);
        return APIResponse.success(null, HttpStatus.OK);
    }

    // Throws a Bad Credentials Exception if authentication manager fails to authenticate authToken
    private void authenticateUser(LoginRequest loginRequest) {
        LOGGER.info("Attempting to authenticate user: {}", loginRequest.email());
        var authToken = UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.email(), loginRequest.password());
        authenticationManager.authenticate(authToken);
    }

    private APIResponse<AuthResponse> generateResponse(User user, HttpStatus status) {
        LOGGER.info("Generating response for user: {}", user.getUserId());
        String token = revokeAndGenerateAccessToken(user);
        UserDTO userDTO = userMapper.toDto(user);
        AuthResponse authResponse = generateAuthResponse(userDTO, token);
        return APIResponse.success(authResponse, status);
    }

    private AuthResponse generateAuthResponse(UserDTO userDTO, String token) {
        return AuthResponse.builder()
                .user(userDTO)
                .accessToken(token)
                .build();
    }

    private OTP generateOTP(User user) {
        return OTP.builder()
                .otpId(generateUUID())
                .otp(otpService.generateOTP())
                .expirationDate(new Date(System.currentTimeMillis() + OTP_EXPIRY_DURATION))
                .user(user)
                .build();
    }

    private static MailBody generateEmail(User user, int otp) {
        final String emailSubject = "Reset Password: OTP";
        final String passwordOTPResetMessage = "Here is your OTP to reset your password: ";
        return MailBody.builder()
                .to(user.getEmail())
                .subject(emailSubject)
                .body(passwordOTPResetMessage + otp)
                .build();
    }

    private String revokeAndGenerateAccessToken(User user) {
        LOGGER.info("Revoking access token for user: {}", user.getUserId());
        tokenService.revokeAllUserTokens(user);
        String token = tokenService.createToken(new CustomUserDetails(user),
                ACCESS_TOKEN, Token::getAccessToken);
        tokenService.storeToken(token, user);
        return token;
    }

    private static void validatePasswordMatch(ResetPasswordRequest resetPasswordRequest) {
        boolean passwordsMatch = resetPasswordRequest.password().equals(resetPasswordRequest.confirmPassword());
        if (!passwordsMatch) {
            LOGGER.error("Password and confirm password do not match");
            throw new PasswordsMismatchException();
        }
    }
}
