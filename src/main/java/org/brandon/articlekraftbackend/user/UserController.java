package org.brandon.articlekraftbackend.user;

import lombok.RequiredArgsConstructor;
import org.brandon.articlekraftbackend.handlers.Response;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public Response<Map<String, Object>> findByUserId(Authentication authentication) {
        return userService.getUserByEmail(authentication.getName());
    }

    @GetMapping("/hello")
    public Response<String> hello() {
        return Response.success("Hello there", HttpStatus.OK);
    }

}
