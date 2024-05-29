package org.brandon.articlekraftbackend.services;

import org.brandon.articlekraftbackend.enums.TokenType;
import org.brandon.articlekraftbackend.entities.Token;
import org.brandon.articlekraftbackend.entities.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.function.Function;

public interface TokenService {
    String createToken(UserDetails user, TokenType type, Function<Token, String> tokenFunction);

    String extractUsername(String token);

    boolean isTokenValid(String token, UserDetails user);

    void storeToken(String token, User user);

    void revokeAllUserTokens(User user);
}
