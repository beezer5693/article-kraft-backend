package org.brandon.articlekraftbackend.exceptions;

public class PasswordsMismatchException extends RuntimeException {

    public PasswordsMismatchException() {
        super("Password and confirm password do not match");
    }
}
