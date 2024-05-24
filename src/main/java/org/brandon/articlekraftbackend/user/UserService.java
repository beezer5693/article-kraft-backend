package org.brandon.articlekraftbackend.user;

import org.brandon.articlekraftbackend.handlers.Response;

import java.util.Map;
import java.util.Optional;

public interface UserService {
    void saveUser(User user);

    Response<Map<String, Object>> getUserByEmail(String email);

    Optional<User> findUserByEmail(String email);
}
