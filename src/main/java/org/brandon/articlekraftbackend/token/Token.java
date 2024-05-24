package org.brandon.articlekraftbackend.token;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.brandon.articlekraftbackend.common.BaseEntity;
import org.brandon.articlekraftbackend.user.User;

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
    @Column(nullable = false)
    public boolean isRevoked;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User user;
}
