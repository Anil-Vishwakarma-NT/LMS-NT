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
package com.nt.user_service_lms.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(TestAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        log.debug("Processing test authentication for: {}", request.getRequestURI());

        // Check for role-specific headers to control authentication
        String testRole = request.getHeader("X-Test-Role");
        String testUser = request.getHeader("X-Test-User");

        List<SimpleGrantedAuthority> authorities;
        String userId;
        String userEmail;
        String fullName;

        // Set authentication based on test role (only ADMIN and EMPLOYEE supported)
        if ("EMPLOYEE".equals(testRole)) {
            authorities = List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE"));
            userId = "test-employee-id";
            userEmail = "employee@example.com";
            fullName = testUser != null ? testUser : "Test Employee";
        } else {
            // Default ADMIN role
            authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
            userId = "test-admin-id";
            userEmail = "admin@example.com";
            fullName = testUser != null ? testUser : "Test Admin";
        }

        // Create ServicePrincipal
        ServicePrincipal principal = new ServicePrincipal.Builder()
                .serviceId("test-service")
                .userId(userId)
                .userEmail(userEmail)
                .userFullName(fullName)
                .originalTokenType("SERVICE")
                .build();

        // Set authentication in SecurityContext
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.debug("Set test authentication - User: {}, Role: {}, Authorities: {}",
                userEmail, testRole, authorities);

        try {
            filterChain.doFilter(request, response);
        } finally {
            // Clear authentication after request
            SecurityContextHolder.clearContext();
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/h2-console") || path.startsWith("/favicon.ico");
    }
}