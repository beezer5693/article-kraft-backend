package org.brandon.articlekraftbackend.user;

import lombok.Getter;

@Getter
public class UserAlreadyExistsException extends RuntimeException {

  private final String email;

  public UserAlreadyExistsException(String message, String email) {
    super(message);
    this.email = email;
  }
}
