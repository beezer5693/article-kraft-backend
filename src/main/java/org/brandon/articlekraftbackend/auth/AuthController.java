package org.brandon.articlekraftbackend.auth;

import jakarta.validation.Valid;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.brandon.articlekraftbackend.handlers.Response;
import org.brandon.articlekraftbackend.user.UserDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Response<Map<String, UserDto>> register(@RequestBody @Valid RegistrationRequest registrationRequest) {
        UserDto createdUser = authService.registerUser(registrationRequest);
        return Response.success(Map.of("user", createdUser), HttpStatus.CREATED);
    }
}
