package org.brandon.articlekraftbackend.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.brandon.articlekraftbackend.handlers.Response;
import org.brandon.articlekraftbackend.token.Token;
import org.brandon.articlekraftbackend.token.TokenService;
import org.brandon.articlekraftbackend.token.TokenType;
import org.brandon.articlekraftbackend.user.*;
import org.brandon.articlekraftbackend.util.CookieUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class OAuthController {
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public static final String AUTHORIZATION_BASE_URL = "/oauth2/authorization";
    public static final String CALLBACK_BASE_URL = "/oauth2/callback/google";
    private static final String ACCESS_TOKEN = "access-token";
    private static final String REFRESH_TOKEN = "refresh-token";

    @SneakyThrows
    public void oauthRedirectResponse(HttpServletRequest request, HttpServletResponse response, String url) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{ \"redirectUrl\": \"%s\" }".formatted(url));
    }

    @SneakyThrows
    public void oauthSuccessCallback(OAuth2AuthorizedClient client, Authentication authentication) {
        String email = authentication.getName();
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            User newUser = User.builder()
                    .userId(UUID.randomUUID().toString())
                    .email(email)
                    .role(Role.CLIENT)
                    .build();
            userRepository.save(newUser);
        }
    }

    @SneakyThrows
    public void oauthSuccessResponse(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<User> user = userRepository.findByEmail(authentication.getName());
        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found", authentication.getName());
        }
        User foundUser = user.get();
        createTokensAndAddToResponseHeader(new CustomUserDetails(foundUser), response);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.OK.value());
        new ObjectMapper().writeValue(response.getOutputStream(), Response.success(foundUser, HttpStatus.OK));
        response.getOutputStream().flush();
    }

    @SneakyThrows
    public void oauthFailureResponse(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        new ObjectMapper().writeValue(response.getOutputStream(), Response.error(HttpStatus.UNAUTHORIZED, "Not authorized", null, request));
        response.getOutputStream().flush();
    }

    private void createTokensAndAddToResponseHeader(UserDetails user, HttpServletResponse response) {
        Token authTokens = generateAuthTokens(user);
        Cookie accessTokenCookie = CookieUtil.generateCookie(ACCESS_TOKEN, authTokens.getAccessToken());
        Cookie refreshTokenCookie = CookieUtil.generateCookie(REFRESH_TOKEN, authTokens.getRefreshToken());
        addAuthTokensToResponseHeader(response, accessTokenCookie, refreshTokenCookie);
    }

    private Token generateAuthTokens(UserDetails user) {
        String accessToken = tokenService.createToken(user, TokenType.ACCESS, Token::getAccessToken);
        String refreshToken = tokenService.createToken(user, TokenType.REFRESH, Token::getRefreshToken);
        return Token.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }

    private void addAuthTokensToResponseHeader(HttpServletResponse response, Cookie... cookies) {
        Arrays.stream(cookies).forEach(response::addCookie);
    }
}
