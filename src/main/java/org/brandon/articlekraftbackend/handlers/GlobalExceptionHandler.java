package org.brandon.articlekraftbackend.handlers;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

import org.brandon.articlekraftbackend.user.UserAlreadyExistsException;
import org.brandon.articlekraftbackend.user.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Response<Object> handleUserAlreadyExistsException(HttpServletRequest req,
                                                             UserAlreadyExistsException e) {
        LOGGER.error(e.getMessage(), e);
        return Response.error(HttpStatus.CONFLICT, e.getMessage(), null, req);
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Response<Object> handleUserNotFoundException(HttpServletRequest req,
                                                        UserNotFoundException e) {
        LOGGER.error(e.getMessage(), e);
        return Response.error(HttpStatus.NOT_FOUND, e.getMessage(), Map.of("id", e.getId()), req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response<Object> handleMethodArgumentNotValidException(HttpServletRequest req,
                                                                  MethodArgumentNotValidException e) {
        LOGGER.error(e.getMessage(), e);
        return Response.error(HttpStatus.BAD_REQUEST, "Validation failed", getFieldErrors(e), req);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<Response<Object>> handleApplicationException(HttpServletRequest req,
                                                                       ApplicationException e) {
        LOGGER.error(e.getMessage(), e);
        return new ResponseEntity<>(Response.error(e.getHttpStatus(), e.getMessage(), null, req),
                e.getHttpStatus());
    }

    @ExceptionHandler(JwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Response<Object> handleJWTException(HttpServletRequest req, JwtException e) {
        LOGGER.error(e.getMessage(), e);
        return Response.error(HttpStatus.UNAUTHORIZED, "Not authorized", null, req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Object>> handleException(HttpServletRequest req, Exception e) {
        switch (e) {
            case AccountStatusException _ -> {
                LOGGER.error(e.getMessage(), e);
                return new ResponseEntity<>(Response
                        .error(HttpStatus.UNAUTHORIZED, "Not authorized", null, req),
                        HttpStatus.UNAUTHORIZED);
            }
            case BadCredentialsException _ -> {
                LOGGER.error(e.getMessage(), e);
                return new ResponseEntity<>(Response
                        .error(HttpStatus.UNAUTHORIZED, "Incorrect email or password", null, req),
                        HttpStatus.UNAUTHORIZED);
            }
            case AccessDeniedException _ -> {
                LOGGER.error(e.getMessage(), e);
                return new ResponseEntity<>(Response
                        .error(HttpStatus.FORBIDDEN, e.getMessage(), null, req),
                        HttpStatus.FORBIDDEN);
            }
            default -> {
                LOGGER.error(e.getMessage(), e);
                return new ResponseEntity<>(Response
                        .error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null, req),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    private static Map<String, Map<String, String>> getFieldErrors(
            MethodArgumentNotValidException e) {
        Map<String, Map<String, String>> fieldErrors = new HashMap<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            Map<String, String> errorDetails = new HashMap<>();
            errorDetails.put("reason", fieldError.getDefaultMessage());
            errorDetails.put("rejected_value", String.valueOf(fieldError.getRejectedValue()));
            fieldErrors.put(fieldError.getField(), errorDetails);
        }
        return fieldErrors;
    }
}
