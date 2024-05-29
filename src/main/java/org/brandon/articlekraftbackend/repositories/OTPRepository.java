package org.brandon.articlekraftbackend.repositories;

import org.brandon.articlekraftbackend.entities.OTP;
import org.brandon.articlekraftbackend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OTPRepository extends JpaRepository<OTP, Integer> {
    @Query("select otp from OTP otp where otp.otp = ?1 and otp.user = ?2")
    Optional<OTP> findByOtpAndUser(int otp, User user);
}
