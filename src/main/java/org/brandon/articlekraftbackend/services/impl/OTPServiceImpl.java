package org.brandon.articlekraftbackend.services.impl;

import lombok.RequiredArgsConstructor;
import org.brandon.articlekraftbackend.entities.OTP;
import org.brandon.articlekraftbackend.entities.User;
import org.brandon.articlekraftbackend.exceptions.EntityNotFoundException;
import org.brandon.articlekraftbackend.payload.OTPRequest;
import org.brandon.articlekraftbackend.repositories.OTPRepository;
import org.brandon.articlekraftbackend.services.OTPService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OTPServiceImpl implements OTPService {
    private final OTPRepository otpRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(OTPServiceImpl.class);

    @Override
    public void saveOTP(OTP otp) {
        LOGGER.info("Saving OTP: {}", otp);
        try {
            otpRepository.save(otp);
            LOGGER.info("Successfully saved OTP: {}", otp);
        } catch (DataAccessException e) {
            LOGGER.error("An error occured trying to access the database", e);
            throw e;
        }
    }

    @Override
    public OTP findByOTPAndUser(OTPRequest otpRequest, User user) {
        LOGGER.info("Finding OTP by OTP: {}", otpRequest);
        int otp = otpRequest.otp();
        try {
            OTP foundOTP = otpRepository.findByOtpAndUser(otp, user)
                    .orElseThrow(() -> handleOTPNotFoundException(otp));
            LOGGER.info("Found OTP: {}", foundOTP);
            return foundOTP;
        } catch (DataAccessException e) {
            LOGGER.error("An error occured trying to access the database", e);
            throw e;
        }
    }

    @Override
    public void deleteOTP(OTP otp) {
        LOGGER.info("Deleting OTP: {}", otp);
        try {
            otpRepository.deleteById(otp.getId());
            LOGGER.info("Successfully deleted OTP: {}", otp);
        } catch (DataAccessException e) {
            LOGGER.error("An error occured trying to access the database", e);
            throw e;
        }
    }

    @Override
    public boolean isOTPValid(OTP otp) {
        return otp.getExpirationDate().before(Date.from(Instant.now()));
    }

    @Override
    public int generateOTP() {
        Random random = new Random();
        return random.nextInt(100_000, 999_999);
    }

    private static EntityNotFoundException handleOTPNotFoundException(int otp) {
        String errorMessage = String.format("Could not find OTP: %d", otp);
        EntityNotFoundException ex = new EntityNotFoundException(errorMessage, String.valueOf(otp));
        LOGGER.warn(errorMessage, ex);
        return ex;
    }
}
