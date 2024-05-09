package org.brandon.articlekraftbackend.user;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.brandon.articlekraftbackend.auth.RegistrationRequest;
import org.brandon.articlekraftbackend.util.PasswordUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final PasswordUtils passwordUtils;

    public UserDto toDto(User user) {
        return UserDto.builder()
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
    }

    public User toUser(RegistrationRequest registrationRequest) {
        return User.builder()
                .userId(UUID.randomUUID().toString())
                .firstName(registrationRequest.firstName())
                .lastName(registrationRequest.lastName())
                .email(registrationRequest.email())
                .password(passwordUtils.encodePassword(registrationRequest::password))
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .isEnabled(true)
                .build();
    }

    public User toUser(UserDto userDto) {
        return User.builder()
                .userId(UUID.randomUUID().toString())
                .firstName(userDto.firstName())
                .lastName(userDto.lastName())
                .email(userDto.email())
                .password(userDto.password())
                .isAccountNonExpired(true)
                .isAccountNonLocked(true)
                .isCredentialsNonExpired(true)
                .isEnabled(true)
                .build();
    }
}
