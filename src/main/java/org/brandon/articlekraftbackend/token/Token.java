package org.brandon.articlekraftbackend.token;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.brandon.articlekraftbackend.user.User;
import org.brandon.articlekraftbackend.common.BaseEntity;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "_tokens")
public class Token extends BaseEntity {

  @Column(name = "access_token", length = 1000)
  @JsonProperty("access_token")
  private String accessToken;

  @Enumerated(EnumType.STRING)
  private TokenType type;

  @Column(nullable = false)
  public boolean isRevoked;

  @JsonBackReference
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  public User user;
}
