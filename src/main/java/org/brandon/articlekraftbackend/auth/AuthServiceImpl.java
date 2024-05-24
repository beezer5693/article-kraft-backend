package org.brandon.articlekraftbackend.auth;

import lombok.RequiredArgsConstructor;
import org.brandon.articlekraftbackend.token.Token;
import org.brandon.articlekraftbackend.token.TokenService;
import org.brandon.articlekraftbackend.token.TokenType;
import org.brandon.articlekraftbackend.user.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;

    private final static Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Override
    public AuthResponseDTO registerUser(RegistrationRequest registrationRequest) {
        LOGGER.info("Attempting to register user: {}", registrationRequest);
        String userEmail = registrationRequest.email();
        if (isUserAlreadyRegistered(userEmail)) {
            handleUserExistsException(userEmail);
        }
        User user = mapRegistrationRequestToUser(registrationRequest);
        User registeredUser = saveUser(user);
        return handleTokenGenerationAndResponse(registeredUser);
    }

    @Override
    public AuthResponseDTO loginUser(LoginRequest loginRequest) {
        authenticateUser(loginRequest);
        Optional<User> user = getUserByEmail(loginRequest.email());
        if (user.isEmpty()) {
            throw new BadCredentialsException("Incorrect email or password");
        }
        User foundUser = user.get();
        return handleTokenGenerationAndResponse(foundUser);
    }

    @Override
    public AuthResponseDTO refreshAccessToken(Authentication authentication) {
        String email = authentication.getName();
        Optional<User> user = getUserByEmail(email);
        if (user.isEmpty()) {
            throw handleUserNotFoundException(email);
        }
        User foundUser = user.get();
        return handleTokenGenerationAndResponse(foundUser);
    }

    private AuthResponseDTO handleTokenGenerationAndResponse(User user) {
        tokenService.revokeAllUserTokens(user);
        String token = generateAccessToken(getUserDetails(user));
        tokenService.storeToken(token, user);
        return generateAuthResponse(user, token);
    }

    private AuthResponseDTO generateAuthResponse(User foundUser, String token) {
        return AuthResponseDTO.builder()
                .user(mapToDto(foundUser))
                .accessToken(token)
                .build();
    }

    private User saveUser(User user) {
        try {
            return userRepository.save(user);
        } catch (DataAccessException e) {
            LOGGER.error("An error occured trying to access the database", e);
            throw e;
        }
    }

    private Optional<User> getUserByEmail(String email) {
        try {
            return userRepository.findByEmail(email);
        } catch (DataAccessException e) {
            LOGGER.error("An error occured trying to access the database", e);
            throw e;
        }
    }

    private boolean isUserAlreadyRegistered(String email) {
        try {
            return userRepository.existsByEmail(email);
        } catch (DataAccessException e) {
            LOGGER.error("An error occured trying to access the database", e);
            throw e;
        }
    }

    // Throws a Bad Credentials Exception if authentication fails
    private void authenticateUser(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken token = UsernamePasswordAuthenticationToken.unauthenticated(
                loginRequest.email(), loginRequest.password());
        authenticationManager.authenticate(token);
    }

    private String generateAccessToken(UserDetails userDetails) {
        return tokenService.createToken(userDetails, TokenType.ACCESS, Token::getAccessToken);
    }

    private User mapRegistrationRequestToUser(RegistrationRequest registrationRequest) {
        return userMapper.toUser(registrationRequest);
    }

    private UserDTO mapToDto(User newUser) {
        return userMapper.toDto(newUser);
    }

    private UserDetails getUserDetails(User user) {
        return new CustomUserDetails(user);
    }

    private static UserNotFoundException handleUserNotFoundException(String email) {
        String errorMessage = String.format("Could not find user: %s", email);
        UserNotFoundException ex = new UserNotFoundException(errorMessage, email);
        LOGGER.warn(errorMessage, ex);
        return ex;
    }

    private static void handleUserExistsException(String email) {
        String errorMessage = "A user with the email you provided already exists.";
        UserAlreadyExistsException ex = new UserAlreadyExistsException(errorMessage, email);
        LOGGER.warn(errorMessage, ex);
        throw ex;
    }
}
