package org.brandon.articlekraftbackend.services;

import org.brandon.articlekraftbackend.entities.User;
import org.brandon.articlekraftbackend.payload.ResetPasswordRequest;

import java.util.Optional;

public interface UserService {
    User saveUser(User user);

    User findUserByEmail(String email);

    boolean existsByEmail(String email);

    void updatePassword(User user, ResetPasswordRequest resetPasswordRequest);
}
