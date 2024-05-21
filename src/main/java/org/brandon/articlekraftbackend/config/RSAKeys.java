package org.brandon.articlekraftbackend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@ConfigurationProperties(prefix = "jwt")
public record RSAKeys(RSAPublicKey rsaPublicKey, RSAPrivateKey rsaPrivateKey) {
}
