package org.brandon.articlekraftbackend.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Permission {
  CLIENT_READ("client:read"),
  CLIENT_CREATE("client:create"),
  CLIENT_UPDATE("client:update"),
  CLIENT_DELETE("client:delete");

  private final String permission;
}
