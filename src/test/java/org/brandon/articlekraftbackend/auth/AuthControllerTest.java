package org.brandon.articlekraftbackend.auth;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.UUID;
import org.brandon.articlekraftbackend.base.BaseControllerTestConfig;
import org.brandon.articlekraftbackend.exceptions.EntityAlreadyExistsException;
import org.brandon.articlekraftbackend.payload.APIResponse;
import org.brandon.articlekraftbackend.user.Role;
import org.brandon.articlekraftbackend.user.User;
import org.brandon.articlekraftbackend.user.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DirtiesContext
class AuthControllerTest extends BaseControllerTestConfig {

  @MockBean
  private AuthService authService;

  private UserDTO userDTO;

  private User user;

  private static final String BASE_URL = "/api/v1/auth";

  @BeforeEach
  void setUp() {
    user = User.builder()
        .userId(UUID.randomUUID().toString())
        .firstName("John")
        .lastName("Doe")
        .email("john.doe@example.com")
        .password("password")
        .role(Role.CLIENT)
        .isEnabled(true)
        .isAccountNonLocked(true)
        .isAccountNonExpired(true)
        .isCredentialsNonExpired(true)
        .createdDate(LocalDateTime.now())
        .build();

    userDTO = UserDTO.builder()
        .userId(user.getUserId())
        .firstName("John")
        .lastName("Doe")
        .email("john.doe@example.com")
        .password("password")
        .role(Role.CLIENT)
        .build();
  }

  @Test
  void should_RegisterUser_When_GivenValidRegistrationDetails() throws Exception {
    // Arrange
    RegistrationRequest request = RegistrationRequest.builder()
        .firstName("John")
        .lastName("Doe")
        .email("john.doe@example.com")
        .password("password")
        .build();

    var authResponseDTO = AuthResponseDTO.builder()
        .user(userDTO)
        .accessToken(mockAccessToken)
        .build();

    APIResponse<AuthResponseDTO> expectedResponse = APIResponse.success(authResponseDTO,
        HttpStatus.CREATED);

    String jsonResponse = convertToJson(expectedResponse);

    when(authService.registerUser(request)).thenReturn(expectedResponse);

    // Act
    ResultActions result = mockMvc.perform(post(BASE_URL.concat("/register"))
        .contentType(MediaType.APPLICATION_JSON)
        .content(convertToJson(request)));

    // Assert
    result.andDo(print())
        .andExpect(status().isCreated())
        .andExpect(content().json(jsonResponse));
  }

  @Test
  void should_ReturnStatusCode409_When_GivenRegistrationRequestWithAlreadyRegisteredEmail()
      throws Exception {
    // Arrange
    RegistrationRequest request = RegistrationRequest.builder()
        .firstName("John")
        .lastName("Doe")
        .email("john.doe@example.com")
        .password("password")
        .build();

    when(authService.registerUser(request)).thenThrow(
        new EntityAlreadyExistsException("User already exists", request.email()));

    // Act
    ResultActions result = mockMvc.perform(post(BASE_URL.concat("/register"))
        .contentType(MediaType.APPLICATION_JSON)
        .content(convertToJson(request)));

    // Assert
    result.andDo(print()).andExpect(status().isConflict());
  }

  @Test
  void should_ReturnStatusCode400_When_GivenInvalidRegistrationDetails() throws Exception {
    // Arrange
    RegistrationRequest invalidRequest = RegistrationRequest.builder()
        .firstName("")
        .lastName("Doe")
        .email("john.doe@example.com")
        .password("password")
        .build();

    // Act
    ResultActions result = mockMvc.perform(post(BASE_URL.concat("/register"))
        .contentType(MediaType.APPLICATION_JSON)
        .content(convertToJson(invalidRequest)));

    // Assert
    result.andDo(print()).andExpect(status().isBadRequest());
  }

  @Test
  void should_LoginUser_When_GivenValidLoginDetails() throws Exception {
    // Arrange
    LoginRequest request = LoginRequest.builder()
        .email("john.doe@example.com")
        .password("password")
        .build();

    var authResponseDTO = AuthResponseDTO.builder()
        .user(userDTO)
        .accessToken(mockAccessToken)
        .build();

    APIResponse<AuthResponseDTO> expectedResponse = APIResponse.success(authResponseDTO,
        HttpStatus.OK);

    String jsonResponse = convertToJson(expectedResponse);

    when(authService.loginUser(request)).thenReturn(expectedResponse);

    // Act
    ResultActions result = mockMvc.perform(post(BASE_URL.concat("/login"))
        .contentType(MediaType.APPLICATION_JSON)
        .content(convertToJson(request)));

    // Assert
    result.andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().json(jsonResponse));
  }

  @Test
  void should_ReturnStatusCode401_When_GivenInvalidLoginDetails() throws Exception {
    // Arrange
    LoginRequest request = LoginRequest.builder()
        .email("john.doe@example.com")
        .password("password")
        .build();

    when(authService.loginUser(request)).thenThrow(new BadCredentialsException("Bad credentials"));

    // Act
    ResultActions result = mockMvc.perform(post(BASE_URL.concat("/login"))
        .contentType(MediaType.APPLICATION_JSON)
        .content(convertToJson(request)));

    // Assert
    result.andDo(print()).andExpect(status().isUnauthorized());
  }
}