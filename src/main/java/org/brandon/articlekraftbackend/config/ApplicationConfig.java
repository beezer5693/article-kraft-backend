package org.brandon.articlekraftbackend.config;

import org.brandon.articlekraftbackend.token.TokenConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
@EnableConfigurationProperties({RSAKeys.class, TokenConfig.class})
public class ApplicationConfig {
}
