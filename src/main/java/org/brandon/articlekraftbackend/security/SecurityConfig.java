package org.brandon.articlekraftbackend.security;

import lombok.RequiredArgsConstructor;
import org.brandon.articlekraftbackend.oauth2.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.brandon.articlekraftbackend.oauth2.OAuthController.*;
import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthenticationProvider authenticationProvider;
    private final RefreshTokenFilter refreshTokenFilter;
    private final AccessTokenFilter accessTokenFilter;
    private final CustomAuthorizationRequestRepository authorizationRequestRepository;
    private final CustomAuthorizedClientService authorizedClientService;
    private final OAuthController oauthController;
    private final CustomAuthorizationRedirectFilter authorizationRedirectFilter;
    private final CustomAuthorizationRequestResolver authorizationRequestResolver;

    @Value("${frontend-url}")
    private String FRONTEND_URL;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .securityMatcher(new AntPathRequestMatcher("/api/v1/auth/**"))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/refresh").authenticated()
                        .anyRequest().permitAll()
                )
                .authenticationProvider(authenticationProvider)
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()))
                .addFilterBefore(refreshTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    SecurityFilterChain userSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .securityMatcher(new AntPathRequestMatcher("/api/v1/users/**"))
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()))
                .addFilterBefore(accessTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(authenticationProvider)
                .build();
    }

    @Bean
    SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .securityMatcher(new AntPathRequestMatcher("/oauth2/authorization/**"))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(subconfig -> subconfig
                                .baseUri(AUTHORIZATION_BASE_URL)
                                .authorizationRequestResolver(authorizationRequestResolver)
                                .authorizationRequestRepository(authorizationRequestRepository)
                        )
                        .redirectionEndpoint(subconfig -> subconfig
                                .baseUri(CALLBACK_BASE_URL + "/*")
                        )
                        .authorizedClientService(authorizedClientService)
                        .successHandler(oauthController::oauthSuccessResponse)
                        .failureHandler(oauthController::oauthFailureResponse)
                )
                .addFilterBefore(authorizationRedirectFilter, OAuth2AuthorizationRequestRedirectFilter.class)
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(FRONTEND_URL));
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
