package org.brandon.articlekraftbackend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.brandon.articlekraftbackend.exceptions.ApplicationException;
import org.brandon.articlekraftbackend.token.TokenRepository;
import org.brandon.articlekraftbackend.token.TokenService;
import org.brandon.articlekraftbackend.user.User;
import org.brandon.articlekraftbackend.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
@RequiredArgsConstructor
public class TokenFilter extends OncePerRequestFilter {

  private final TokenService tokenService;
  private final UserService userService;
  private final UserDetailsService userDetailsService;
  private final HandlerExceptionResolver handlerExceptionResolver;

  private final static Logger LOGGER = LoggerFactory.getLogger(TokenFilter.class);
  private final TokenRepository tokenRepository;

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) {
    LOGGER.info("Started jwt request filtering...");
    try {
      final String authHeader = getAuthorizationHeader(request);
      if (!isAuthHeaderValid(authHeader)) {
        continueFilterChain(request, response, filterChain);
        return;
      }

      final String token = extractTokenFromAuthorizationHeader(authHeader);
      final String userEmail = tokenService.extractUsername(token);

      User user = userService.findUserByEmail(userEmail);

      if (isSecurityContextHolderNull()) {
        authenticateRequest(request, user, token);
      }

      continueFilterChain(request, response, filterChain);
    } catch (Exception ex) {
      LOGGER.error("Exception while authenticating request: {}", ex.getMessage());
      handlerExceptionResolver.resolveException(request, response, null, ex);
    }
  }

  private void authenticateRequest(HttpServletRequest request, User user, String token) {
    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
    validateToken(token, userDetails);
    setSecurityContextHolder(userDetails, request);
  }

  private void validateToken(String token, UserDetails userDetails) {
    if (!tokenService.isTokenValid(token, userDetails)) {
      LOGGER.warn("Failed to authenticate request");
      throw new ApplicationException(HttpStatus.UNAUTHORIZED, "Not authorized");
    }
  }

  private void setSecurityContextHolder(UserDetails userDetails, HttpServletRequest request) {
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    var authToken = new UsernamePasswordAuthenticationToken(userDetails, null,
        userDetails.getAuthorities());
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

  private static void continueFilterChain(HttpServletRequest req, HttpServletResponse res,
      FilterChain chain) {
    try {
      chain.doFilter(req, res);
    } catch (IOException | ServletException e) {
      LOGGER.error("Error proceeding with filter chain: {}", e.getMessage());
      throw new ApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), e);
    }
  }
}
