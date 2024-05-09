package org.brandon.articlekraftbackend.user;

import static org.brandon.articlekraftbackend.user.Permission.CLIENT_CREATE;
import static org.brandon.articlekraftbackend.user.Permission.CLIENT_DELETE;
import static org.brandon.articlekraftbackend.user.Permission.CLIENT_READ;
import static org.brandon.articlekraftbackend.user.Permission.CLIENT_UPDATE;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
@RequiredArgsConstructor
public enum Role {

  CLIENT(Set.of(
      CLIENT_READ,
      CLIENT_CREATE,
      CLIENT_UPDATE,
      CLIENT_DELETE
  ));

  private final Set<Permission> permissions;

  public List<SimpleGrantedAuthority> getAuthorities() {
    List<SimpleGrantedAuthority> authorities = permissions.stream()
        .map(Permission::getPermission)
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());

    authorities.add(new SimpleGrantedAuthority("SCOPE_" + this.name()));
    
    return authorities;
  }
}
