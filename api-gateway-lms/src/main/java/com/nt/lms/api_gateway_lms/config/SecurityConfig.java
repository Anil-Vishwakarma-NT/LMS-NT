package com.nt.lms.api_gateway_lms.config;

import com.nt.lms.api_gateway_lms.constant.CommonConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

/**
 * Security configuration class for the LMS API Gateway using Spring WebFlux.
 * This configuration class sets up reactive security for the Learning Management System (LMS)
 * API Gateway, including JWT authentication, CORS configuration, and path-based authorization.
 *
 * <p>Key features configured:</p>
 * <ul>
 *   <li>JWT-based authentication for API endpoints</li>
 *   <li>CORS support for cross-origin requests</li>
 *   <li>Path-based security rules with public and protected endpoints</li>
 *   <li>Custom exception handling for authentication and authorization failures</li>
 *   <li>BCrypt password encoding for user authentication</li>
 * </ul>
 *
 * @author NT LMS Team
 * @version 1.0
 * @since 1.0
 */
@Configuration
@EnableWebFluxSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * Configures the main security filter chain for the reactive web application.
     * This method sets up the security rules, authentication mechanisms, and exception handling
     * for the LMS API Gateway.
     *
     * <p>Security configuration includes:</p>
     * <ul>
     *   <li>Disabling CSRF, HTTP Basic, and Form Login authentication</li>
     *   <li>Enabling CORS with custom configuration</li>
     *   <li>Setting up JWT authentication filter</li>
     *   <li>Defining path-based authorization rules</li>
     *   <li>Custom exception handling for unauthorized and forbidden access</li>
     * </ul>
     *
     * @param http the {@link ServerHttpSecurity} instance to configure security settings
     * @param jwtAuthFilter the JWT authentication filter for processing JWT tokens
     * @return configured {@link SecurityWebFilterChain} for the application
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            final ServerHttpSecurity http,
            final JwtAuthFilter jwtAuthFilter) {
        return http
                .csrf(csrf -> csrf.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(formLogin -> formLogin.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(exchange -> exchange
                        // for static content pdf or video fetching from course-service-lms
                        .pathMatchers("/lms/course/static/**").permitAll()
                        // Auth endpoints - public access
                        .pathMatchers(
                                "/lms/api/client-api/auth/login",
                                "/lms/api/client-api/auth/refresh",
                                "/lms/api/client-api/password/**",
                                "/course/h2-console/**",
                                "/user/h2-console/**"
                        ).permitAll()

                        // Token introspection - requires authentication
                        .pathMatchers("/lms/api/client-api/auth/logout").authenticated()

                        // Service endpoints - require authentication
                        .pathMatchers("/api/token-api/**").authenticated()

                        // Any other request requires authentication
                        .anyExchange().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((exchange, ex) -> {
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            exchange.getResponse().getHeaders().add("Content-Type", "application/json");
                            String body = "{\"error\": \"Unauthorized\", \"message\": \"Authentication required\"}";
                            return exchange.getResponse().writeWith(
                                    Mono.just(exchange.getResponse().bufferFactory().wrap(body.getBytes()))
                            );
                        })
                        .accessDeniedHandler((exchange, denied) -> {
                            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                            exchange.getResponse().getHeaders().add("Content-Type", "application/json");
                            String body = "{\"error\": \"Forbidden\", \"message\": \"Access denied\"}";
                            return exchange.getResponse().writeWith(
                                    Mono.just(exchange.getResponse().bufferFactory().wrap(body.getBytes()))
                            );
                        })
                )
                .build();
    }

    /**
     * Creates a reactive authentication manager for handling user authentication.
     * This manager uses the provided user details service to authenticate users
     * with BCrypt password encoding.
     *
     * <p>The authentication manager is responsible for:</p>
     * <ul>
     *   <li>Validating user credentials against the user details service</li>
     *   <li>Using BCrypt password encoder for secure password verification</li>
     *   <li>Supporting reactive authentication flows</li>
     * </ul>
     *
     * @param userDetailsService the reactive user details service for loading user information
     * @return configured {@link ReactiveAuthenticationManager} instance
     */
    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(final ReactiveUserDetailsService userDetailsService) {
        UserDetailsRepositoryReactiveAuthenticationManager authManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authManager.setPasswordEncoder(passwordEncoder());
        return authManager;
    }

    /**
     * Provides a BCrypt password encoder for secure password hashing and verification.
     * BCrypt is a strong, adaptive hashing function designed to be slow and resistant
     * to brute-force attacks.
     *
     * <p>Features of BCrypt encoder:</p>
     * <ul>
     *   <li>Adaptive cost parameter for adjusting computational complexity</li>
     *   <li>Built-in salt generation for each password</li>
     *   <li>Resistance to rainbow table attacks</li>
     * </ul>
     *
     * @return a {@link BCryptPasswordEncoder} instance for password encoding
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures Cross-Origin Resource Sharing (CORS) settings for the application.
     * This configuration allows the frontend application to make cross-origin requests
     * to the API Gateway from approved origins.
     *
     * <p>CORS configuration includes:</p>
     * <ul>
     *   <li>Allowed origins: localhost:3000 (development frontend)</li>
     *   <li>Allowed methods: GET, POST, PUT, DELETE, OPTIONS, PATCH</li>
     *   <li>Allowed headers: All headers (*)</li>
     *   <li>Credentials support: Enabled for cookie-based authentication</li>
     *   <li>Max age: 3600 seconds for preflight request caching</li>
     * </ul>
     *
     * @return configured {@link CorsConfigurationSource} for handling CORS requests
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(CommonConstants.LONG_THREE_THOUSAND_SIX_HUNDRED);

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
