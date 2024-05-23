package org.brandon.articlekraftbackend.oauth2;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.SerializationUtils;
import org.brandon.articlekraftbackend.util.CookieUtil;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class CustomAuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    public static final String OAUTH_COOKIE_NAME = "OAUTH";
    private static final Base64.Encoder B64E = Base64.getEncoder();
    private static final Base64.Decoder B64D = Base64.getDecoder();

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return getAuthorizationRequestCookie(request);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        if (authorizationRequest == null) {
            return;
        }
        this.attachOAuth2CookieToResponseHeader(response, authorizationRequest);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        return null;
    }

    private void attachOAuth2CookieToResponseHeader(HttpServletResponse response, OAuth2AuthorizationRequest authorizationRequest) {
        Cookie oauth2Cookie = generateOAuth2Cookie(authorizationRequest);
        response.addCookie(oauth2Cookie);
    }

    private OAuth2AuthorizationRequest getAuthorizationRequestCookie(HttpServletRequest request) {
        return CookieUtil.retrieve(request.getCookies(), OAUTH_COOKIE_NAME)
                .map(this::decrypt)
                .orElse(null);
    }

    private String encrypt(OAuth2AuthorizationRequest authorizationRequest) {
        byte[] serializedAuthorizationRequest = SerializationUtils.serialize(authorizationRequest);
        return B64E.encodeToString(serializedAuthorizationRequest);
    }

    private OAuth2AuthorizationRequest decrypt(String encrypted) {
        byte[] encryptedBytes = B64D.decode(encrypted);
        return SerializationUtils.deserialize(encryptedBytes);
    }

    private Cookie generateOAuth2Cookie(OAuth2AuthorizationRequest authorizationRequest) {
        return CookieUtil.generateCookie(OAUTH_COOKIE_NAME, encrypt(authorizationRequest));
    }
}
