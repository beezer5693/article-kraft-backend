package org.brandon.articlekraftbackend.user;

import org.brandon.articlekraftbackend.forgotpassword.ResetPasswordRequest;

public interface UserService {
    User saveUser(User user);

    User findUserByEmail(String email);

    User findUserByResetPasswordCode(String code);

    boolean existsByEmail(String email);

    void updatePassword(User user, ResetPasswordRequest resetPasswordRequest);
}
