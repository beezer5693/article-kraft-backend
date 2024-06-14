package org.brandon.articlekraftbackend.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = Replace.NONE)
class UserRepositoryTest {

  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.0");

  @Autowired
  private UserRepository userRepository;

  private User user;

  @BeforeEach
  void beforeEach() {
    userRepository.deleteAll();
    user = User.builder()
        .userId(UUID.randomUUID().toString())
        .firstName("Brandon")
        .lastName("Bryan")
        .email("brandon@email.com")
        .password("password")
        .role(Role.CLIENT)
        .createdDate(LocalDateTime.now())
        .isAccountNonExpired(true)
        .isAccountNonLocked(true)
        .isCredentialsNonExpired(true)
        .isEnabled(true)
        .build();
  }

  @Test
  void should_EstablishConnectionToPostgresContainer() {
    assertTrue(postgreSQLContainer.isCreated());
    assertTrue(postgreSQLContainer.isRunning());
  }

  @Test
  void should_ReturnTrue_When_UserExists() {
    // Arrange & Act
    userRepository.save(user);
    boolean result = userRepository.existsByEmail(user.getEmail());

    // Assert
    assertTrue(result);
  }

  @Test
  void should_ReturnFalse_When_UserDoesNotExist() {
    // Arrange & Act
    boolean result = userRepository.existsByEmail("you@email.com");

    // Assert
    assertFalse(result);
  }

  @Test
  void should_ReturnOptionalOfUser_When_ValidEmailGiven() {
    // Arrange & Act
    userRepository.save(user);
    Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());

    // Assert
    assertTrue(optionalUser.isPresent());
    assertEquals(user.getEmail(), optionalUser.get().getEmail());
  }

  @Test
  void should_ReturnEmptyOptional_When_UserDoesNotFound() {
    // Arrange & Act
    Optional<User> optionalUser = userRepository.findByEmail("you@email.com");

    // Assert
    assertTrue(optionalUser.isEmpty());
  }
}