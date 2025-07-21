package com.nt.lms.api_gateway_lms.service.serviceImp;

import com.nt.lms.api_gateway_lms.config.JwtUtil;
import com.nt.lms.api_gateway_lms.config.RsaDecryptUtil;
import com.nt.lms.api_gateway_lms.constant.CommonConstants;
import com.nt.lms.api_gateway_lms.constant.UserConstants;
import com.nt.lms.api_gateway_lms.dto.AuthRequest;
import com.nt.lms.api_gateway_lms.dto.AuthResponse;
import com.nt.lms.api_gateway_lms.dto.CustomUserDetails;
import com.nt.lms.api_gateway_lms.dto.RefreshTokenRequest;
import com.nt.lms.api_gateway_lms.exception.ResourceNotFoundException;
import com.nt.lms.api_gateway_lms.service.CustomUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Service implementation responsible for handling authentication-related operations
 * such as login, token refresh, and logout.
 */
@Slf4j
@Service
public class AuthServiceImp {

    /**
     * Reactive authentication manager for performing non-blocking authentication.
     */
    @Autowired
    private ReactiveAuthenticationManager authenticationManager;

    /**
     * Utility for generating and validating JWT access and refresh tokens.
     */
    @Autowired
    private JwtUtil jwtTokenManager;

    /**
     * Utility for decrypting RSA encrypted data (e.g., encrypted passwords).
     */
    @Autowired
    private RsaDecryptUtil rsaDecryptUtil;

    /**
     * Service to retrieve user details for authentication.
     */
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    /**
     * Authenticates a user based on email and password, and generates JWT tokens upon successful login.
     *
     * @param request AuthRequest containing email and password.
     * @return Mono emitting ResponseEntity containing AuthResponse with tokens or appropriate error status.
     */
    public Mono<ResponseEntity<AuthResponse>> login(final AuthRequest request) {
        log.info("Attempting login for email: {}", request.getEmail());

        if (request.getEmail() == null || request.getPassword() == null) {
            log.warn("Login failed - email or password is null");
            return Mono.just(ResponseEntity.badRequest().body(null));
        }

        return customUserDetailsService.findByUsername(request.getEmail())
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(UserConstants.USER_NOT_FOUND)))
                .flatMap(user -> {
                    CustomUserDetails userDetails = (CustomUserDetails) user;

                    if (!userDetails.isActive()) {
                        log.warn("Login attempt failed - user inactive: {}", request.getEmail());
                        return Mono.error(new ResourceNotFoundException(UserConstants.USER_DELETED));
                    }

                    Authentication auth = new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword()
                    );

                    return authenticationManager.authenticate(auth)
                            .map(authResult -> {
                                CustomUserDetails authenticatedUser = (CustomUserDetails) authResult.getPrincipal();

                                String accessToken = jwtTokenManager.generateAccessToken(
                                        authenticatedUser,
                                        authenticatedUser.getUserId(),
                                        authenticatedUser.getEmail(),
                                        authenticatedUser.getFullName()
                                );

                                String refreshToken = jwtTokenManager.generateRefreshToken(
                                        authenticatedUser,
                                        authenticatedUser.getUserId(),
                                        authenticatedUser.getEmail(),
                                        authenticatedUser.getFullName()
                                );

                                AuthResponse response = AuthResponse.builder()
                                        .accessToken(accessToken)
                                        .refreshToken(refreshToken)
                                        .tokenType("Bearer")
                                        .expiresIn(
                                                jwtTokenManager
                                                        .getAccessTokenExpiration()
                                                        / CommonConstants.INTEGER_ONE_THOUSAND
                                        )
                                        .build();

                                return ResponseEntity.ok(response);
                            });
                })
                .onErrorResume(e -> {
                    if (e instanceof ResourceNotFoundException) {
                        return Mono.just(ResponseEntity.status(CommonConstants.INTEGER_FOUR_HUNDRED_FOUR).body(null));
                    }
                    log.error("Authentication failed: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(CommonConstants.INTEGER_FOUR_HUNDRED).body(null));
                });
    }

    /**
     * Generates a new access token using a valid refresh token.
     *
     * @param request RefreshTokenRequest containing the refresh token.
     * @return Mono emitting ResponseEntity with new access token and existing refresh token.
     */
    public Mono<ResponseEntity<AuthResponse>> refreshToken(final RefreshTokenRequest request) {
        return Mono.fromCallable(() -> jwtTokenManager.extractEmail(request.getRefreshToken()))
                .flatMap(email ->
                        customUserDetailsService.findByUsername(email)
                                .cast(CustomUserDetails.class)
                                .flatMap(userDetails -> {
                                    String newAccessToken = jwtTokenManager.refreshAccessToken(
                                            request.getRefreshToken(),
                                            userDetails,
                                            userDetails.getUserId(),
                                            userDetails.getEmail(),
                                            userDetails.getFullName()
                                    );

                                    AuthResponse response = AuthResponse.builder()
                                            .accessToken(newAccessToken)
                                            .refreshToken(request.getRefreshToken())
                                            .tokenType("Bearer")
                                            .expiresIn(
                                                    jwtTokenManager
                                                            .getAccessTokenExpiration()
                                                            / CommonConstants.INTEGER_ONE_THOUSAND
                                            )
                                            .build();

                                    return Mono.just(ResponseEntity.ok(response));
                                })
                )
                .onErrorResume(e -> {
                    e.printStackTrace();
                    return Mono.just(ResponseEntity.badRequest().<AuthResponse>build());
                });
    }

    /**
     * Handles user logout. Currently a placeholder without actual token invalidation.
     *
     * @param authHeader Authorization header containing bearer token.
     * @return Mono emitting ResponseEntity with logout confirmation message.
     */
    public Mono<ResponseEntity<Map<String, String>>> logout(final String authHeader) {
        // Token invalidation can be implemented here
        return Mono.just(ResponseEntity.ok(Map.of("message", "Logged out successfully")));
    }
}
