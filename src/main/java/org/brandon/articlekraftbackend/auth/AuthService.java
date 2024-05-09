package org.brandon.articlekraftbackend.auth;

import lombok.RequiredArgsConstructor;
import org.brandon.articlekraftbackend.user.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final static Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto registerUser(RegistrationRequest registrationRequest) {
        LOGGER.info("Attempting to register user: {}", registrationRequest);

        String userEmail = registrationRequest.email();
        if (isUserAlreadyRegistered(userEmail)) {
            throwUserExistsException(userEmail);
        }

        try {
            User user = userMapper.toUser(registrationRequest);
            User savedUser = userRepository.save(user);
            return userMapper.toDto(savedUser);
        } catch (DataAccessException e) {
            LOGGER.error("An error occured trying to access the database", e);
            throw e;
        }
    }

    private boolean isUserAlreadyRegistered(String email) {
        return userRepository.existsByEmail(email);
    }

    private static void throwUserExistsException(String userEmail) {
        String errorMessage = "A user with the email you provided already exists.";
        UserAlreadyExistsException ex = new UserAlreadyExistsException(errorMessage, userEmail);
        LOGGER.warn(errorMessage, ex);
        throw ex;
    }
}
