package org.brandon.articlekraftbackend.token;

import org.brandon.articlekraftbackend.user.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface TokenService {

  String extractUsername(String token);

  boolean isTokenValid(String token, UserDetails user);

  String createToken(UserDetails user);

  void storeToken(String token, User user);

  void revokeAllUserTokens(User user);
}
