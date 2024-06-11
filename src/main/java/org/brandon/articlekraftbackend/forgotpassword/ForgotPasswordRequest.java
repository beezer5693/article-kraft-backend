package org.brandon.articlekraftbackend.forgotpassword;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.brandon.articlekraftbackend.user.User;
import org.brandon.articlekraftbackend.common.BaseEntity;

import java.time.Instant;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@ToString
@Table(name = "_forgot_password_request")
public class ForgotPasswordRequest extends BaseEntity {

  @Column(nullable = false)
  private String code;

  @Column(nullable = false)
  private Instant expirationDate;

  @JsonBackReference
  @OneToOne
  @JoinColumn(name = "user_id")
  private User user;
}
