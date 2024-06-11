package org.brandon.articlekraftbackend.exceptions;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.brandon.articlekraftbackend.forgotpassword.PasswordsMismatchException;
import org.brandon.articlekraftbackend.forgotpassword.ResetPasswordCodeExpiredException;
import org.brandon.articlekraftbackend.payload.APIResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntityAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public APIResponse<Object> handleUserAlreadyExistsException(HttpServletRequest request, EntityAlreadyExistsException e) {
        LOGGER.error(e.getMessage(), e);
        return APIResponse.error(HttpStatus.CONFLICT, e.getMessage(), null, request);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public APIResponse<Object> handleUserNotFoundException(HttpServletRequest request, EntityNotFoundException e) {
        LOGGER.error(e.getMessage(), e);
        return APIResponse.error(HttpStatus.NOT_FOUND, e.getMessage(), Map.of("identifier", e.getIdentifier()), request);
    }

    @ExceptionHandler(PasswordsMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public APIResponse<Object> handlePasswordsMismatchException(HttpServletRequest request, PasswordsMismatchException e) {
        LOGGER.error(e.getMessage(), e);
        return APIResponse.error(HttpStatus.BAD_REQUEST, e.getMessage(), null, request);
    }

    @ExceptionHandler(ResetPasswordCodeExpiredException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public APIResponse<Object> handleOTPExpiredException(HttpServletRequest request, ResetPasswordCodeExpiredException e) {
        LOGGER.error(e.getMessage(), e);
        return APIResponse.error(HttpStatus.BAD_REQUEST, e.getMessage(), null, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public APIResponse<Object> handleMethodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException e) {
        LOGGER.error(e.getMessage(), e);
        return APIResponse.error(HttpStatus.BAD_REQUEST, "Validation failed", getFieldErrors(e), request);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<APIResponse<Object>> handleApplicationException(HttpServletRequest request, ApplicationException e) {
        LOGGER.error(e.getMessage(), e);
        return new ResponseEntity<>(APIResponse.error(e.getHttpStatus(), e.getMessage(), null, request),
                e.getHttpStatus());
    }

    @ExceptionHandler(JwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public APIResponse<Object> handleJWTException(HttpServletRequest request, JwtException e) {
        LOGGER.error(e.getMessage(), e);
        return APIResponse.error(HttpStatus.UNAUTHORIZED, "Not authorized", null, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse<Object>> handleException(HttpServletRequest request, Exception e) {
        switch (e) {
            case AccountStatusException _ -> {
                LOGGER.error(e.getMessage(), e);
                return new ResponseEntity<>(APIResponse
                        .error(HttpStatus.UNAUTHORIZED, "Not authorized", null, request),
                        HttpStatus.UNAUTHORIZED);
            }
            case BadCredentialsException _ -> {
                LOGGER.error(e.getMessage(), e);
                return new ResponseEntity<>(APIResponse
                        .error(HttpStatus.UNAUTHORIZED, "Incorrect email or password.", null, request),
                        HttpStatus.UNAUTHORIZED);
            }
            case AccessDeniedException _ -> {
                LOGGER.error(e.getMessage(), e);
                return new ResponseEntity<>(APIResponse
                        .error(HttpStatus.FORBIDDEN, e.getMessage(), null, request),
                        HttpStatus.FORBIDDEN);
            }
            default -> {
                LOGGER.error(e.getMessage(), e);
                return new ResponseEntity<>(APIResponse
                        .error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null, request),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    private static Map<String, Map<String, String>> getFieldErrors(MethodArgumentNotValidException e) {
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
