package org.brandon.articlekraftbackend.user;

import org.brandon.articlekraftbackend.handlers.Response;

import java.util.Map;

public interface UserService {
    Response<Map<String, Object>> getUserByEmail(String email);
}
