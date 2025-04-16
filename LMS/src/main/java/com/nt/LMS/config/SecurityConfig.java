package com.nt.LMS.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security Configuration class to configure authentication and authorization.
 * This class sets up HTTP security, authentication manager, password encoding,
 * and JWT filter for the application.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * The user details service used for authentication.
     */
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * JWT filter to intercept and validate JWT tokens for every request.
     */
    @Autowired
    private JwtFilter jwtFilter;

    /**
     * Configures HTTP security for the application, defining the security filters
     * and access control for various endpoints.
     *
     * @param http the HttpSecurity object for configuring HTTP request security
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs during security configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/admin/**").hasAuthority("admin")  // Use "admin" instead of "ROLE_ADMIN"
                        .requestMatchers("/employee/**").hasAnyAuthority("employee", "admin")
                        .requestMatchers("/manager/**").hasAnyAuthority("manager", "admin")
                        .requestMatchers("/group/all-groups").hasAuthority("admin")
                        .requestMatchers("/group/**").hasAnyAuthority("manager", "admin") // Use "admin" instead of "ROLE_ADMIN"
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Provides an AuthenticationManager to handle authentication.
     *
     * @return the AuthenticationManager bean
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(authenticationProvider());
    }

    /**
     * Configures the authentication provider using the user details service and password encoder.
     *
     * @return the configured DaoAuthenticationProvider
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Configures the password encoder to use BCryptPasswordEncoder for hashing passwords.
     *
     * @return the BCryptPasswordEncoder bean
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
