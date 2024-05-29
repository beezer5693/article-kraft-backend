package org.brandon.articlekraftbackend.services.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.brandon.articlekraftbackend.config.TokenConfig;
import org.brandon.articlekraftbackend.enums.TokenType;
import org.brandon.articlekraftbackend.repositories.TokenRepository;
import org.brandon.articlekraftbackend.entities.Token;
import org.brandon.articlekraftbackend.entities.User;
import org.brandon.articlekraftbackend.services.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl extends TokenConfig implements TokenService {
    private final TokenRepository tokenRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenServiceImpl.class);

    private final Supplier<SecretKey> secretKey = () -> Keys.hmacShaKeyFor(Decoders.BASE64.decode(getSecretKey()));

    private final Supplier<JwtBuilder> tokenBuilder = () ->
            Jwts.builder()
                    .id(UUID.randomUUID().toString())
                    .issuedAt(Date.from(Instant.now()))
                    .expiration(Date.from(Instant.now().plus(getAccessTokenExpiry(), ChronoUnit.DAYS)))
                    .signWith(secretKey.get(), Jwts.SIG.HS256);

    private final Function<UserDetails, String> buildToken = user ->
            tokenBuilder.get()
                    .subject(user.getUsername())
                    .claim("role", user.getAuthorities())
                    .compact();

    private final Function<String, Claims> extractAllClaims = token ->
            Jwts.parser()
                    .verifyWith(secretKey.get())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();


    @Override
    public String createToken(UserDetails user, TokenType type, Function<Token, String> tokenFunction) {
        LOGGER.info("Generating JWT {} for user {}", type.getValue(), user.getUsername());
        final String accessToken = buildToken.apply(user);
        Token token = Token
                .builder()
                .accessToken(accessToken)
                .build();
        return tokenFunction.apply(token);
    }

    @Override
    public void storeToken(String token, User user) {
        Token accessToken = generateToken(token, user);
        try {
            tokenRepository.save(accessToken);
        } catch (DataAccessException e) {
            LOGGER.error("An error occurred trying to access the database", e);
            throw e;
        }
    }

    @Override
    public void revokeAllUserTokens(User user) {
        try {
            var allUserTokens = tokenRepository.findByUserId(user.getId());
            if (allUserTokens.isEmpty()) return;
            allUserTokens.stream()
                    .peek(token -> token.setRevoked(true))
                    .forEach(tokenRepository::save);
        } catch (DataAccessException e) {
            LOGGER.error("An error occurred trying to access the database", e);
            throw e;
        }
    }

    @Override
    public boolean isTokenValid(String token, UserDetails user) {
        final String username = extractUsername(token);
        return username.equals(user.getUsername()) && !isTokenExpired(token);
    }

    @Override
    public String extractUsername(String token) {
        return getClaimsValue(token, Claims::getSubject);
    }

    public Claims extractClaim(String token) {
        return extractAllClaims.apply(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(Date.from(Instant.now()));
    }

    private Date extractExpiration(String token) {
        return extractClaim(token).getExpiration();
    }

    private <T> T getClaimsValue(String token, Function<Claims, T> claimsExtractor) {
        return extractAllClaims.andThen(claimsExtractor).apply(token);
    }

    private static Token generateToken(String token, User user) {
        return Token.builder()
                .accessToken(token)
                .isRevoked(false)
                .user(user)
                .build();
    }
}



