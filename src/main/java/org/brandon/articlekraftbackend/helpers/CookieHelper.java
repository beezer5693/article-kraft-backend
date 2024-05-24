package org.brandon.articlekraftbackend.helpers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;

import java.util.Arrays;
import java.util.Optional;

import static java.util.Objects.isNull;
import static org.springframework.boot.web.server.Cookie.SameSite.*;

public class CookieHelper {
    private static final String COOKIE_DOMAIN = "localhost";
    private static final boolean HTTP_ONLY = Boolean.TRUE;
    private static final boolean SECURE = Boolean.FALSE;

    private CookieHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static Optional<String> retrieve(Cookie[] cookies, @NonNull String name) {
        if (isNull(cookies)) {
            return Optional.empty();
        }
        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equalsIgnoreCase(name))
                .map(Cookie::getValue)
                .findFirst();
    }

    public static Cookie generateCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setDomain(COOKIE_DOMAIN);
        cookie.setHttpOnly(HTTP_ONLY);
        cookie.setSecure(SECURE);
        cookie.setAttribute("Same-Site", NONE.name());
        return cookie;
    }

    public static void attachCookiesToResponseHeader(HttpServletResponse response, Cookie... cookies) {
        Arrays.stream(cookies).forEach(response::addCookie);
    }
}