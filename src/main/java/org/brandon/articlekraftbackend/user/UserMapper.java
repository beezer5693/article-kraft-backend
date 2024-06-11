package org.brandon.articlekraftbackend.user;

import lombok.RequiredArgsConstructor;
import org.brandon.articlekraftbackend.auth.RegistrationRequest;
import org.brandon.articlekraftbackend.utils.PasswordUtils;
import org.springframework.stereotype.Component;

import static org.brandon.articlekraftbackend.utils.UUIDUtils.generateUUID;

@Component
@RequiredArgsConstructor
public class UserMapper {

  private final PasswordUtils passwordUtils;

  public UserDTO toDto(User user) {
    return UserDTO.builder()
        .userId(user.getUserId())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .email(user.getEmail())
        .password(user.getPassword())
        .role(user.getRole())
        .build();
  }

  public User toUser(RegistrationRequest registrationRequest) {
    return User.builder()
        .userId(generateUUID())
        .firstName(registrationRequest.firstName())
        .lastName(registrationRequest.lastName())
        .email(registrationRequest.email())
        .password(passwordUtils.encodePassword(registrationRequest.password()))
        .role(Role.CLIENT)
        .isAccountNonExpired(true)
        .isAccountNonLocked(true)
        .isCredentialsNonExpired(true)
        .isEnabled(true)
        .build();
  }

  public User toUser(UserDTO userDto) {
    return User.builder()
        .userId(generateUUID())
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
