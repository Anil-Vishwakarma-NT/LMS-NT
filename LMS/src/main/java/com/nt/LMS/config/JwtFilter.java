package com.nt.LMS.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.nt.LMS.constants.JwtConstant.BEARER_PREFIX_LENGTH;

/**
 * JwtFilter is a filter that intercepts HTTP requests to extract and validate JWT tokens.
 * If the token is valid, it authenticates the user and sets up the Spring Security context.
 * This class is designed to be used as part of the Spring Security filter chain.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    /**
     * Utility for working with JWT tokens.
     */
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Service for loading user details based on the username.
     */
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Filters incoming HTTP requests to extract JWT tokens and authenticate the user.
     * If the JWT token is valid, it sets the authentication context for the user.
     *
     * @param request the HTTP request, marked as final to prevent reassignment
     * @param response the HTTP response, marked as final to prevent reassignment
     * @param filterChain the filter chain, marked as final to prevent reassignment
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs during filtering
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String token = null;
        String username = null;

        // Extract token if present
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(BEARER_PREFIX_LENGTH); // Bearer prefix length is 7
            username = jwtUtil.extractUsername(token);
        }

        // If username is present and the security context is not already authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Load user details and validate the token
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(token, userDetails)) {
                // Create authentication token and set it in the security context
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null,
                                userDetails.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);  // Continue with the filter chain
    }
}
