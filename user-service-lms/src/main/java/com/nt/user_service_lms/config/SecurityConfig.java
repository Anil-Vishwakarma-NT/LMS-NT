package com.nt.user_service_lms.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration class for defining HTTP security, authorization rules,
 * and custom authentication/authorization filters.
 *
 * <p>This configuration:
 * <ul>
 *     <li>Disables CSRF protection for stateless sessions</li>
 *     <li>Defines endpoint access rules based on roles and authorities</li>
 *     <li>Sets session policy to stateless</li>
 *     <li>Registers a custom authentication filter</li>
 *     <li>Handles authentication and access denied exceptions with custom responses</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    /**
     * Custom authentication filter for verifying service-based tokens or authentication.
     */
    @Autowired
    private ServiceAuthenticationFilter serviceAuthenticationFilter;

    /**
     * Configures the HTTP security, including endpoint access rules, session management,
     * filters, and exception handling.
     *
     * @param http the {@link HttpSecurity} to modify
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs while configuring the security filter chain
     */
    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/service-api/admin/**",
                                        "/api/service-api/custom-report/**",
                                        "/api/service-api/enrollment/**",
                                        "/api/service-api/custom-user-report/**").hasRole("ADMIN")

                        .requestMatchers("api/service-api/manager/**").hasAnyRole("MANAGER","ADMIN")

                        .requestMatchers("/api/service-api/users/**",
                                        "/api/service-api/enrollments/**",
                                        "/api/service-api/group/**").hasAnyRole("EMPLOYEE", "ADMIN","MANAGER")

                        .anyRequest().authenticated()

                )
                .addFilterBefore(serviceAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptions -> exceptions
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
     * Bean definition for {@link PasswordEncoder} using BCrypt hashing algorithm.
     *
     * @return a BCrypt-based password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
