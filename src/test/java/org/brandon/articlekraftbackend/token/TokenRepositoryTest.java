package org.brandon.articlekraftbackend.token;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.brandon.articlekraftbackend.user.Role;
import org.brandon.articlekraftbackend.user.User;
import org.brandon.articlekraftbackend.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = Replace.NONE)
class TokenRepositoryTest {

  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.0");

  @Autowired
  private TokenRepository tokenRepository;

  @Autowired
  private UserRepository userRepository;

  private User user;
  private User user2;
  private Token token;

  @BeforeEach
  void beforeEach() {
    tokenRepository.deleteAll();
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
    user2 = User.builder()
        .userId(UUID.randomUUID().toString())
        .firstName("Arantxa")
        .lastName("Leon")
        .email("arantxa@email.com")
        .password("password")
        .role(Role.CLIENT)
        .createdDate(LocalDateTime.now())
        .isAccountNonExpired(true)
        .isAccountNonLocked(true)
        .isCredentialsNonExpired(true)
        .isEnabled(true)
        .build();
    token = Token.builder()
        .accessToken("123456789")
        .isRevoked(false)
        .createdDate(LocalDateTime.now())
        .build();
  }

  @Test
  void should_EstablishConnectionToPostgresContainer() {
    assertTrue(postgreSQLContainer.isCreated());
    assertTrue(postgreSQLContainer.isRunning());
  }

  @Test
  void should_FindAllTokensAssociatedWithUser_When_ValidUserIdGiven() {
    // Arrange & Act
    User savedUser = userRepository.save(user);
    token.setUser(savedUser);
    tokenRepository.save(token);
    List<Token> tokens = tokenRepository.findByUserId(savedUser.getId());

    // Assert
    assertFalse(tokens.isEmpty());
    assertEquals(1, tokens.size());
    assertEquals(token, tokens.getFirst());
  }

  @Test
  void should_ReturnEmptyList_When_NoTokensAssociatedWithGivenUserId() {
    // Arrange & Act
    List<User> users = userRepository.saveAll(List.of(user, user2));
    token.setUser(users.getFirst());
    tokenRepository.save(token);
    List<Token> tokens = tokenRepository.findByUserId(user2.getId());

    // Assert
    assertTrue(tokenRepository.count() > 0);
    assertTrue(tokens.isEmpty());
  }

  @Test
  void should_FindTokenByAccessToken_When_ValidAccessTokenGiven() {
    // Arrange & Act
    User savedUser = userRepository.save(user);
    token.setUser(savedUser);
    tokenRepository.save(token);
    Optional<Token> foundToken = tokenRepository.findByAccessToken(token.getAccessToken());

    // Assert
    assertTrue(foundToken.isPresent());
    assertEquals(token, foundToken.get());
  }

  @Test
  void should_ReturnEmptyOptional_When_TokenNotFound() {
    // Arrange & Act
    Optional<Token> foundToken = tokenRepository.findByAccessToken("invalidToken");

    // Assert
    assertTrue(foundToken.isEmpty());
  }
}