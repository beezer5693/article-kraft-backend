package org.brandon.articlekraftbackend.forgotpassword;

import org.brandon.articlekraftbackend.user.User;

public interface ForgotPasswordRequestService {
    void saveForgotPasswordRequest(ForgotPasswordRequest forgotPasswordRequest);

    ForgotPasswordRequest findForgotPasswordRequestByCode(String code);

    void deleteForgotPasswordRequestByUser(User user);

    void deleteForgotPasswordRequestByCode(String code);

    boolean isCodeExpired(ForgotPasswordRequest forgotPasswordRequest);
}
