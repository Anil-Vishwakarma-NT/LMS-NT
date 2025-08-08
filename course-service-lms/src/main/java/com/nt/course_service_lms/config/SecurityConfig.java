package com.nt.course_service_lms.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration class for the LMS Course Service.
 * Configures security filters, authentication, authorization, and password encoding.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile("!test")
public class SecurityConfig {

    /**
     * Custom authentication filter for service-to-service authentication.
     * This is now optional and will only be injected if the active profile is NOT 'test'.
     */
    @Autowired(required = false) // <-- STEP 1: MAKE IT OPTIONAL
    private ServiceAuthenticationFilter serviceAuthenticationFilter;

    /**
     * Configures the security filter chain with custom authentication and authorization rules.
     * Sets up stateless session management, CSRF protection, and custom exception handling.
     *
     * @param http the HttpSecurity object to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // user endpoints - require authentication
                        .requestMatchers("/api/service-api/streaming/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/**").authenticated()

                        .anyRequest().authenticated()
                )
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)

                );

        // <-- STEP 2: ADD A NULL CHECK
        // Only add the filter if it has been instantiated (i.e., not in a 'test' profile)
        if (serviceAuthenticationFilter != null) {
            http.addFilterBefore(serviceAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        }

        http.exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/json");
                    response.getWriter().write(
                            "{\"error\": \"Unauthorized\", \"message\": \"Authentication required\"}"
                    );
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setContentType("application/json");
                    response.getWriter().write(
                            "{\"error\": \"Forbidden\", \"message\": \"Access denied\"}"
                    );
                })
        );

        return http.build();
    }

    /**
     * Creates a BCrypt password encoder bean for secure password hashing.
     * Uses the default strength of 10 rounds.
     *
     * @return a BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
