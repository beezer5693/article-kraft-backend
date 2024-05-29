package org.brandon.articlekraftbackend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "_otp")
public class OTP extends BaseEntity {
    @Column(name = "otp_id", nullable = false)
    private String otpId;

    @Column(nullable = false)
    private int otp;

    @Column(nullable = false)
    private Date expirationDate;

    @OneToOne
    private User user;
}
