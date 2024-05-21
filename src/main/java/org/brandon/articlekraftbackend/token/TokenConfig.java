package org.brandon.articlekraftbackend.token;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "token")
public record TokenConfig(int accessTokenExpiry, int refreshTokenExpiry) {
}
