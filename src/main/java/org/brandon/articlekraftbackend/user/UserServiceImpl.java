package org.brandon.articlekraftbackend.user;

import lombok.RequiredArgsConstructor;
import org.brandon.articlekraftbackend.exceptions.EntityNotFoundException;
import org.brandon.articlekraftbackend.forgotpassword.ResetPasswordRequest;
import org.brandon.articlekraftbackend.forgotpassword.ForgotPasswordRequestRepository;
import org.brandon.articlekraftbackend.utils.PasswordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ForgotPasswordRequestRepository forgotPasswordRequestRepository;
    private final PasswordUtils passwordUtils;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public User saveUser(User user) {
        LOGGER.info("Saving user: {}", user);
        try {
            return userRepository.save(user);
        } catch (DataAccessException e) {
            LOGGER.error("An error occurred while saving the user to the database", e);
            throw e;
        }
    }

    @Override
    public User findUserByEmail(String email) {
        try {
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> handleUserNotFoundException(email));
        } catch (DataAccessException e) {
            LOGGER.error("An error occured trying to access the database", e);
            throw e;
        }
    }

    @Override
    public User findUserByResetPasswordCode(String code) {
        try {
            return forgotPasswordRequestRepository.findUserByCode(code);
        } catch (DataAccessException e) {
            LOGGER.error("An error occured trying to access the database", e);
            throw e;
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        LOGGER.info("Checking if the user exists with the email {}", email);
        try {
            return userRepository.existsByEmail(email);
        } catch (DataAccessException e) {
            LOGGER.error("An error occured trying to access the database", e);
            throw e;
        }
    }

    @Override
    public void updatePassword(User user, ResetPasswordRequest resetPasswordRequest) {
        LOGGER.info("Updating password for user {}", user.getEmail());
        String encryptedPassword = passwordUtils.encodePassword(resetPasswordRequest.password());
        try {
            userRepository.updatePassword(user.getEmail(), encryptedPassword);
        } catch (DataAccessException e) {
            LOGGER.error("An error occured trying to access the database", e);
            throw e;
        }
    }

    private static EntityNotFoundException handleUserNotFoundException(String email) {
        String errorMessage = String.format("Could not find user: %s", email);
        EntityNotFoundException ex = new EntityNotFoundException(errorMessage, email);
        LOGGER.warn(errorMessage, ex);
        return ex;
    }
}
