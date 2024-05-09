package org.brandon.articlekraftbackend.user;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {

  private final String id;

  public UserNotFoundException(String message, String id) {
    super(message);
    this.id = id;
  }
}
