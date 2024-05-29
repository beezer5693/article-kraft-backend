package org.brandon.articlekraftbackend.services;

import org.brandon.articlekraftbackend.entities.OTP;
import org.brandon.articlekraftbackend.entities.User;
import org.brandon.articlekraftbackend.payload.OTPRequest;

public interface OTPService {
    void saveOTP(OTP otp);

    OTP findByOTPAndUser(OTPRequest otpRequest, User user);

    void deleteOTP(OTP otp);

    boolean isOTPValid(OTP otp);

    int generateOTP();
}
