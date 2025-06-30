package com.nt.LMS.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private ServiceAuthenticationFilter serviceAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authz -> authz
                        // user endpoints - require authentication
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/enrollment/**").permitAll()
                        .requestMatchers("/api/users/enrollments/**").permitAll()
                        .requestMatchers("api/service-api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/employee/**").hasAnyAuthority("employee", "admin")
                        .requestMatchers("/manager/**").hasAnyAuthority("manager", "admin")
                        .requestMatchers("/group/all-groups").permitAll()
                        .requestMatchers("/group/**").permitAll()
                        // Any other request requires authentication
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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

//    @Bean
//    public SecurityFilterChain securityFilterChain(final HttpSecurity http) throws Exception {
//        http
//                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//                .csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/auth/**").permitAll()
//                        .requestMatchers("/api/enrollment/**").permitAll()
//                        .requestMatchers("/api/users/enrollments/**").permitAll()
//                        .requestMatchers("/admin/**").permitAll()
//                        .requestMatchers("/employee/**").hasAnyAuthority("employee", "admin")
//                        .requestMatchers("/manager/**").hasAnyAuthority("manager", "admin")
//                        .requestMatchers("/group/all-groups").permitAll()
//                        .requestMatchers("/group/**").permitAll()
//                        .anyRequest().authenticated()
//                )
//                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authenticationProvider(authenticationProvider())
//                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager() {
//        return new ProviderManager(authenticationProvider());
//    }
//
//    @Bean
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(userDetailsService);
//        authProvider.setPasswordEncoder(passwordEncoder());
//        return authProvider;
//    }
//    //by default strength for generation of paaword is 10 in bcrypt password encoder.
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowedOrigins(List.of("http://localhost:3000")); // or your frontend URL
//        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS" ,"PATCH"));
//        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
//        config.setAllowCredentials(true);
//        config.setExposedHeaders(List.of("Authorization"));
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//        return source;
//    }

