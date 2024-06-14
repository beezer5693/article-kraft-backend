package org.brandon.articlekraftbackend.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.UUID;
import org.brandon.articlekraftbackend.token.TokenServiceImpl;
import org.brandon.articlekraftbackend.user.CustomUserDetails;
import org.brandon.articlekraftbackend.user.CustomUserDetailsService;
import org.brandon.articlekraftbackend.user.Role;
import org.brandon.articlekraftbackend.user.User;
import org.brandon.articlekraftbackend.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class BaseControllerTestConfig extends TestContainerConfig {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected TokenServiceImpl tokenService;

  @Autowired
  protected UserRepository userRepository;

  @MockBean
  protected CustomUserDetailsService customUserDetailsService;

  protected User mockUser;

  protected String mockAccessToken;

  protected ObjectMapper mapper = new ObjectMapper();

  private static final String BEARER = "Bearer";

  @BeforeEach
  void beforeEach() {
    mockUser = User.builder()
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

    userRepository.save(mockUser);

    mockAccessToken = generateMockAccessToken(new CustomUserDetails(mockUser));

    Mockito.when(customUserDetailsService.loadUserByUsername(mockUser.getEmail()))
        .thenReturn(new CustomUserDetails(mockUser));
  }

  @AfterEach
  void afterEach() {
    userRepository.deleteAll();
  }

  public String convertToJson(Object obj) {
    try {
      return mapper.writeValueAsString(obj);
    } catch (JsonProcessingException ex) {
      throw new RuntimeException(ex);
    }
  }

  private String generateMockAccessToken(UserDetails userDetails) {
    return BEARER.concat(tokenService.createToken(userDetails));
  }
}
