package org.brandon.articlekraftbackend.token;

import org.brandon.articlekraftbackend.user.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.function.Function;

public interface TokenService {
    String createToken(UserDetails user, TokenType type, Function<Token, String> tokenFunction);

    String extractTokenPropertyValue(Jwt token, Function<Jwt, String> tokenFunction);

    boolean isTokenValid(Jwt token, UserDetails user);

    void saveRefreshToken(String token, User user);

    void revokeAllUserRefreshTokens(User user);
}
