package org.brandon.articlekraftbackend.user;

import org.brandon.articlekraftbackend.handlers.Response;

import java.util.Map;
import java.util.Optional;

public interface UserService {
    Response<Map<String, Object>> getUserByEmail(String email);

    Optional<User> findUserByEmail(String email);
}
