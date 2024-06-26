package org.brandon.articlekraftbackend.user;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.brandon.articlekraftbackend.common.BaseEntity;
import org.brandon.articlekraftbackend.forgotpassword.ForgotPasswordRequest;
import org.brandon.articlekraftbackend.token.Token;

import java.util.List;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "_users")
public class User extends BaseEntity {

  @Column(nullable = false, unique = true, name = "user_id")
  private String userId;

  @Column(name = "first_name", nullable = false)
  private String firstName;

  @Column(name = "last_name", nullable = false)
  private String lastName;

  @Column(nullable = false, unique = true)
  private String email;

  private String password;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Role role;

  @JsonManagedReference
  @OneToMany(mappedBy = "user")
  private List<Token> tokens;

  @JsonManagedReference
  @OneToOne(mappedBy = "user")
  private ForgotPasswordRequest forgotPasswordRequest;

  @Column(name = "is_account_non_expired", nullable = false)
  private boolean isAccountNonExpired;

  @Column(name = "is_account_non_locked", nullable = false)
  private boolean isAccountNonLocked;

  @Column(name = "is_credentials_non_expired", nullable = false)
  private boolean isCredentialsNonExpired;

  @Column(name = "is_enabled", nullable = false)
  private boolean isEnabled;
}
