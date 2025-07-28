//package com.nt.course_service_lms.config;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.context.annotation.Profile;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.List;
//
//@Component
//@Profile("test")
//public class TestAuthenticationFilter extends OncePerRequestFilter {
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//
//        // Create mock authentication for all test requests
//        List<SimpleGrantedAuthority> authorities = List.of(
//                new SimpleGrantedAuthority("ROLE_ADMIN"),
//                new SimpleGrantedAuthority("ROLE_USER")
//        );
//
//        ServicePrincipal principal = new ServicePrincipal.Builder()
//                .serviceId("test-service")
//                .userId("test-user-id")
//                .userEmail("test@example.com")
//                .userFullName("Test User")
//                .originalTokenType("SERVICE")
//                .build();
//
//        UsernamePasswordAuthenticationToken authentication =
//                new UsernamePasswordAuthenticationToken(principal, null, authorities);
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        filterChain.doFilter(request, response);
//    }
//}
package com.nt.course_service_lms.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Profile("test")
public class TestAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Check for role-specific headers to control authentication
        String testRole = request.getHeader("X-Test-Role");
        String testUser = request.getHeader("X-Test-User");

        List<SimpleGrantedAuthority> authorities;
        String userId;
        String userEmail;

        // Default to ADMIN if no specific role is requested
        if ("EMPLOYEE".equals(testRole)) {
            authorities = List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE"));
            userId = "test-employee-id";
            userEmail = "employee@example.com";
        } else if ("USER".equals(testRole)) {
            authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
            userId = "test-user-id";
            userEmail = "user@example.com";
        } else {
            // Default ADMIN role
            authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
            userId = "test-admin-id";
            userEmail = "admin@example.com";
        }

        ServicePrincipal principal = new ServicePrincipal.Builder()
                .serviceId("test-service")
                .userId(userId)
                .userEmail(userEmail)
                .userFullName(testUser != null ? testUser : "Test User")
                .originalTokenType("SERVICE")
                .build();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}