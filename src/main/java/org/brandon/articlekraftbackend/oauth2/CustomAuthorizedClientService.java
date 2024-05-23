package org.brandon.articlekraftbackend.oauth2;

import lombok.RequiredArgsConstructor;
import org.brandon.articlekraftbackend.user.Role;
import org.brandon.articlekraftbackend.user.User;
import org.brandon.articlekraftbackend.user.UserRepository;
import org.brandon.articlekraftbackend.user.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomAuthorizedClientService implements OAuth2AuthorizedClientService {
    private final UserRepository userRepository;

    @Override
    public <T extends OAuth2AuthorizedClient> T loadAuthorizedClient(String clientRegistrationId, String principalName) {
        return null;
    }

    @Override
    public void saveAuthorizedClient(OAuth2AuthorizedClient authorizedClient, Authentication principal) {
        Optional<User> user = userRepository.findByEmail(principal.getName());
        if (user.isEmpty()) {
            User newUser = User.builder()
                    .userId(UUID.randomUUID().toString())
                    .email(principal.getName())
                    .role(Role.CLIENT)
                    .build();
            userRepository.save(newUser);
        }
    }

    @Override
    public void removeAuthorizedClient(String clientRegistrationId, String principalName) {

    }
}
