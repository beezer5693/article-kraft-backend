package org.brandon.articlekraftbackend.forgotpassword;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.brandon.articlekraftbackend.user.User;
import org.brandon.articlekraftbackend.exceptions.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ForgotPasswordRequestServiceImpl implements ForgotPasswordRequestService {
    private final ForgotPasswordRequestRepository forgotPasswordRequestRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(ForgotPasswordRequestServiceImpl.class);

    @Override
    public void saveForgotPasswordRequest(ForgotPasswordRequest forgotPasswordRequest) {
        LOGGER.info("Saving token: {}", forgotPasswordRequest);
        try {
            forgotPasswordRequestRepository.save(forgotPasswordRequest);
        } catch (DataAccessException e) {
            LOGGER.error("An error occured trying to access the database", e);
            throw e;
        }
    }

    @Transactional
    @Override
    public void deleteForgotPasswordRequestByUser(User user) {
        LOGGER.info("Deleting all codes for user: {}", user.getUserId());
        List<ForgotPasswordRequest> forgotPasswordRequestCodes = forgotPasswordRequestRepository.findAllByUser(user);
        if (!forgotPasswordRequestCodes.isEmpty()) {
            forgotPasswordRequestRepository.deleteAll(forgotPasswordRequestCodes);
        }
    }

    @Override
    public ForgotPasswordRequest findForgotPasswordRequestByCode(String code) {
        LOGGER.info("Finding password reset code by code: {}", code);
        try {
            return forgotPasswordRequestRepository.findByCode(code)
                    .orElseThrow(() -> handleForgotPasswordRequestCodeNotFound(code));
        } catch (DataAccessException e) {
            LOGGER.error("An error occured trying to access the database", e);
            throw e;
        }
    }

    @Transactional
    @Override
    public void deleteForgotPasswordRequestByCode(String code) {
        LOGGER.info("Deleting code: {}", code);
        try {
            forgotPasswordRequestRepository.deleteByCode(code);
            LOGGER.info("Successfully deleted code: {}", code);
        } catch (DataAccessException e) {
            LOGGER.error("An error occured trying to access the database", e);
            throw e;
        }
    }

    @Override
    public boolean isCodeExpired(ForgotPasswordRequest code) {
        return code.getExpirationDate().isBefore(Instant.now());
    }

    private static EntityNotFoundException handleForgotPasswordRequestCodeNotFound(String code) {
        String errorMessage = "The code provided is not valid.";
        EntityNotFoundException ex = new EntityNotFoundException(errorMessage, code);
        LOGGER.warn(errorMessage, ex);
        return ex;
    }
}
