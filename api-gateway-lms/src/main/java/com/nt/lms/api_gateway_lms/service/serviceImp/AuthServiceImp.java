package com.nt.lms.api_gateway_lms.service.serviceImp;

import com.nt.lms.api_gateway_lms.config.JwtUtil;
import com.nt.lms.api_gateway_lms.config.RsaDecryptUtil;
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

@Slf4j
@Service
public class AuthServiceImp {
    @Autowired
    private ReactiveAuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtTokenManager;

    @Autowired
    RsaDecryptUtil rsaDecryptUtil;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    public Mono<ResponseEntity<AuthResponse>> login(AuthRequest request) {
        log.info("Attempting login for email: {}", request.getEmail());

        if (request.getEmail() == null || request.getPassword() == null) {
            log.warn("Login failed - email or password is null ***************");
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

//                    String decodedPassword = rsaDecryptUtil.decrypt(request.getPassword());
//                    System.out.println("Passowrd" + decodedPassword);
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
                                        .expiresIn(jwtTokenManager.getAccessTokenExpiration() / 1000)
                                        .build();

                                return ResponseEntity.ok(response);
                            });
                })
                .onErrorResume(e -> {
                    if (e instanceof ResourceNotFoundException) {
                        return Mono.just(ResponseEntity.status(404).body(null));
                    }
                    log.error("Authentication failed: {}", e.getMessage());
                    return Mono.just(ResponseEntity.status(400).body(null));
                });
    }

    public Mono<ResponseEntity<AuthResponse>> refreshToken(RefreshTokenRequest request) {
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
                                            .expiresIn(jwtTokenManager.getAccessTokenExpiration() / 1000)
                                            .build();

                                    return Mono.just(ResponseEntity.ok(response));
                                })
                )
                .onErrorResume(e -> {
                    e.printStackTrace();
                    return Mono.just(ResponseEntity.badRequest().<AuthResponse>build());
                });
    }

    public Mono<ResponseEntity<Map<String, String>>> logout(String authHeader) {
        // Token invalidation can be implemented here
        return Mono.just(ResponseEntity.ok(Map.of("message", "Logged out successfully")));

//        log.info("Logout request for user: {}", email);
//
//        User user = userRepository.findByEmailIgnoreCase(email)
//                .orElseThrow(() -> {
//                    log.error("Logout failed - user not found: {}", email);
//                    return new ResourceNotFoundException(UserConstants.USER_NOT_FOUND);
//                });
//
//        refreshTokenRepository.deleteByUserId(user.getUserId());
//        log.info("Refresh token deleted for user: {}", email);
    }
}
