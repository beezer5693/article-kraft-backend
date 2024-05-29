package org.brandon.articlekraftbackend.exceptions;

public class OTPExpiredException extends RuntimeException {

    public OTPExpiredException(String message) {
        super(message);
    }
}
