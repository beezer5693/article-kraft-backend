package org.brandon.articlekraftbackend.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.brandon.articlekraftbackend.enums.Permission.*;

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

        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));

        return authorities;
    }
}
