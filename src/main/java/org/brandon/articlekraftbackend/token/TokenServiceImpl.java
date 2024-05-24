package org.brandon.articlekraftbackend.token;

import lombok.RequiredArgsConstructor;
import org.brandon.articlekraftbackend.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.security.oauth2.jwt.JwtClaimsSet.Builder;
import static org.springframework.security.oauth2.jwt.JwtClaimsSet.builder;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final JwtEncoder jwtEncoder;
    private final TokenConfig tokenConfig;
    private final TokenRepository tokenRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenServiceImpl.class);

    private final Function<Integer, Builder> tokenBuilder = tokenExpiration -> builder()
            .issuer("articlekraft")
            .issuedAt(Instant.now())
            .expiresAt(Instant.now().plus(tokenExpiration, ChronoUnit.MINUTES));

    private final BiFunction<UserDetails, Integer, JwtClaimsSet> buildToken = (user, tokenExpiration) ->
            tokenBuilder.apply(tokenExpiration)
                    .subject(user.getUsername())
                    .claim("scope", getPermissions(user))
                    .build();

    @Override
    public String createToken(UserDetails user, TokenType type, Function<Token, String> tokenFunction) {
        LOGGER.info("Generating JWT {} for user {}", type.getValue(), user.getUsername());
        final Jwt encodedToken = encodeJwt(user, getTokenExpiration());
        Token token = Token
                .builder()
                .accessToken(extractTokenPropertyValue(encodedToken, Jwt::getTokenValue))
                .build();
        return tokenFunction.apply(token);
    }

    @Override
    public String extractTokenPropertyValue(Jwt token, Function<Jwt, String> tokenFunction) {
        return tokenFunction.apply(token);
    }

    @Override
    public boolean isTokenValid(Jwt token, UserDetails user) {
        final String username = extractTokenPropertyValue(token, Jwt::getSubject);
        return username.equals(user.getUsername()) && !isTokenExpired(token);
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

    private int getTokenExpiration() {
        return tokenConfig.accessTokenExpiry();
    }

    private boolean isTokenExpired(Jwt token) {
        return Objects.requireNonNull(token.getExpiresAt()).isBefore(Instant.now());
    }

    private Jwt encodeJwt(UserDetails user, Integer expirationMinutes) {
        return jwtEncoder.encode(JwtEncoderParameters.from(buildToken.apply(user, expirationMinutes)));
    }

    private static Token generateToken(String token, User user) {
        return Token.builder()
                .accessToken(token)
                .isRevoked(false)
                .user(user)
                .build();
    }

    private static String getPermissions(UserDetails user) {
        return user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
    }
}
