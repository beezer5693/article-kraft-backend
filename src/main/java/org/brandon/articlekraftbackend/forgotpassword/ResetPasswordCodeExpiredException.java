package org.brandon.articlekraftbackend.forgotpassword;

public class ResetPasswordCodeExpiredException extends RuntimeException {

    public ResetPasswordCodeExpiredException() {
        super("The code provided has expired.");
    }
}
