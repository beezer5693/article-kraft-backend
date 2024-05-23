package org.brandon.articlekraftbackend.user;

import lombok.RequiredArgsConstructor;
import org.brandon.articlekraftbackend.handlers.Response;
import org.brandon.articlekraftbackend.token.Token;
import org.brandon.articlekraftbackend.token.TokenService;
import org.brandon.articlekraftbackend.token.TokenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final TokenService tokenService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public void saveUser(User user) {
        try {
            userRepository.save(user);
        } catch (DataAccessException e) {
            LOGGER.error("An error occurred while saving the user to the database", e);
            throw e;
        }
    }

    @Override
    public Response<Map<String, Object>> getUserByEmail(String email) {
        Optional<User> user = findUserByEmail(email);
        if (user.isEmpty()) {
            throw handleUserNotFoundException(email);
        }
        User foundUser = user.get();
        String accessToken = tokenService.createToken(new CustomUserDetails(foundUser), TokenType.ACCESS, Token::getAccessToken);
        UserDTO userDTO = mapUserToDTO(user.get());
        return Response.success(Map.of("user", userDTO, "access_token", accessToken), HttpStatus.OK);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        try {
            return userRepository.findByEmail(email);
        } catch (DataAccessException e) {
            LOGGER.error("An error occured trying to access the database", e);
            throw e;
        }
    }

    private UserDTO mapUserToDTO(User user) {
        return userMapper.toDto(user);
    }

    private static UserNotFoundException handleUserNotFoundException(String email) {
        String errorMessage = String.format("Could not find user: %s", email);
        UserNotFoundException ex = new UserNotFoundException(errorMessage, email);
        LOGGER.warn(errorMessage, ex);
        return ex;
    }
}
