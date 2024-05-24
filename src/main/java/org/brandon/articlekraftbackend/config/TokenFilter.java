package org.brandon.articlekraftbackend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.brandon.articlekraftbackend.handlers.ApplicationException;
import org.brandon.articlekraftbackend.token.Token;
import org.brandon.articlekraftbackend.token.TokenRepository;
import org.brandon.articlekraftbackend.token.TokenService;
import org.brandon.articlekraftbackend.user.User;
import org.brandon.articlekraftbackend.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class TokenFilter extends OncePerRequestFilter {
    private final TokenService tokenService;
    private final RSAKeys rsaKeys;
    private final HandlerExceptionResolver handlerExceptionResolver;
    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;

    private final static Logger LOGGER = LoggerFactory.getLogger(TokenFilter.class);
    private final TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        LOGGER.info("Started jwt request filtering...");
        try {
            final String authHeader = getAuthorizationHeader(request);
            if (!isAuthHeaderValid(authHeader)) {
                continueFilterChain(request, response, filterChain);
                return;
            }

            final String tokenString = extractTokenFromAuthorizationHeader(authHeader);
            final Jwt token = decodeJwt(tokenString);
            final String userEmail = extractUserEmailFromToken(token);

            Optional<User> user = getUserByEmail(userEmail);
            if (user.isPresent() && isSecurityContextHolderNull()) {
                authenticateRequest(request, user.get(), token);
            }

            continueFilterChain(request, response, filterChain);
        } catch (Exception ex) {
            LOGGER.error("Exception while authenticating request: {}", ex.getMessage());
            handlerExceptionResolver.resolveException(request, response, null, ex);
        }
    }

    private UserDetails getUserDetails(Supplier<String> username) {
        return userDetailsService.loadUserByUsername(username.get());
    }

    private Optional<User> getUserByEmail(String username) {
        return userRepository.findByEmail(username);
    }

    private String extractUserEmailFromToken(Jwt jwtToken) {
        return tokenService.extractTokenPropertyValue(jwtToken, Jwt::getSubject);
    }

    private Jwt decodeJwt(String tokenString) {
        JwtDecoder jwtDecoder = getJwtDecoder();
        return jwtDecoder.decode(tokenString);
    }

    private NimbusJwtDecoder getJwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(rsaKeys.rsaPublicKey()).build();
    }

    private boolean isTokenValid(Jwt token, UserDetails userDetails) {
        return tokenService.isTokenValid(token, userDetails);
    }

    private void authenticateRequest(HttpServletRequest request, User user, Jwt token) {
        UserDetails userDetails = getUserDetails(user::getEmail);
        validateToken(token, userDetails);
        setSecurityContextHolder(userDetails, request);
    }

    private void validateToken(Jwt token, UserDetails userDetails) {
        if (!isTokenValid(token, userDetails) || isTokenRevoked(token::getTokenValue)) {
            LOGGER.warn("Failed to authenticate request");
            throw new ApplicationException(HttpStatus.UNAUTHORIZED, "Not authorized");
        }
    }

    private boolean isTokenRevoked(Supplier<String> token) {
        try {
            Optional<Token> accessToken = tokenRepository.findByAccessToken(token.get());
            return accessToken.map(Token::isRevoked).orElse(false);
        } catch (DataAccessException e) {
            LOGGER.error("Failed to revoke token: {}", token);
            throw e;
        }
    }

    private void setSecurityContextHolder(UserDetails userDetails, HttpServletRequest request) {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        var authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        securityContext.setAuthentication(authToken);
        SecurityContextHolder.setContext(securityContext);
    }

    private static boolean isSecurityContextHolderNull() {
        return SecurityContextHolder.getContext().getAuthentication() == null;
    }

    private static boolean isAuthHeaderValid(String authHeader) {
        return authHeader != null && authHeader.startsWith("Bearer ");
    }

    private static String extractTokenFromAuthorizationHeader(String authHeader) {
        return authHeader.substring(7);
    }

    private static String getAuthorizationHeader(HttpServletRequest request) {
        return request.getHeader(HttpHeaders.AUTHORIZATION);
    }

    private static void continueFilterChain(HttpServletRequest req, HttpServletResponse res, FilterChain chain) {
        try {
            chain.doFilter(req, res);
        } catch (IOException | ServletException e) {
            LOGGER.error("Error proceeding with filter chain: {}", e.getMessage());
            throw new ApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
        }
    }
}
