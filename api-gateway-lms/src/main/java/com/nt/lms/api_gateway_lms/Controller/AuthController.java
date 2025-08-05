package com.nt.lms.api_gateway_lms.Controller;

import com.nt.lms.api_gateway_lms.dto.AuthRequest;
import com.nt.lms.api_gateway_lms.dto.AuthResponse;
import com.nt.lms.api_gateway_lms.dto.RefreshTokenRequest;
import com.nt.lms.api_gateway_lms.service.serviceImp.AuthServiceImp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * REST controller for handling authentication-related operations in the LMS API Gateway.
 *
 * <p>This controller provides endpoints for user authentication, token refresh, and logout
 * operations. It uses reactive programming with WebFlux to handle asynchronous operations
 * and returns Mono responses for non-blocking I/O.</p>
 *
 * <p>Base path for all endpoints: {@code /lms/api/client-api/auth}</p>
 */
@Slf4j
@RestController
@RequestMapping("lms/api/client-api/auth")
public class AuthController {

    /**
     * Service implementation for handling authentication operations.
     *
     * <p>This service contains the business logic for user authentication,
     * token generation, token refresh, and logout operations.</p>
     */
    @Autowired
    private AuthServiceImp authService;

    /**
     * Authenticates a user and generates access and refresh tokens.
     *
     * <p>This endpoint accepts user credentials (email and password) and validates them
     * against the authentication service. Upon successful authentication, it returns
     * both access and refresh tokens that can be used for subsequent API calls.</p>
     *
     * <p><strong>HTTP Method:</strong> POST</p>
     * <p><strong>Endpoint:</strong> {@code /lms/api/client-api/auth/login}</p>
     *
     * @param request The authentication request containing user credentials.
     *                Must include valid email and password fields.
     * @return A {@code Mono<ResponseEntity<AuthResponse>>} containing:
     * <ul>
     *   <li>HTTP 200 OK with AuthResponse containing access and refresh tokens on success</li>
     *   <li>HTTP 401 Unauthorized if credentials are invalid</li>
     *   <li>HTTP 400 Bad Request if request format is invalid</li>
     * </ul>
     * @throws IllegalArgumentException if the request is null or contains invalid data
     * @see AuthRequest
     * @see AuthResponse
     */
    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@RequestBody final AuthRequest request) {
        return authService.login(request);
    }

    /**
     * Refreshes an expired access token using a valid refresh token.
     *
     * <p>This endpoint allows clients to obtain a new access token without requiring
     * the user to log in again, provided they have a valid refresh token. The refresh
     * token should not be expired and must be associated with an active user session.</p>
     *
     * <p><strong>HTTP Method:</strong> POST</p>
     * <p><strong>Endpoint:</strong> {@code /lms/api/client-api/auth/refresh}</p>
     *
     * @param request The refresh token request containing the refresh token.
     *                Must include a valid, non-expired refresh token.
     * @return A {@code Mono<ResponseEntity<AuthResponse>>} containing:
     * <ul>
     *   <li>HTTP 200 OK with new AuthResponse containing fresh tokens on success</li>
     *   <li>HTTP 401 Unauthorized if refresh token is invalid or expired</li>
     *   <li>HTTP 400 Bad Request if request format is invalid</li>
     * </ul>
     * @throws IllegalArgumentException if the request is null or refresh token is missing
     * @see RefreshTokenRequest
     * @see AuthResponse
     */
    @PostMapping("/refresh")
    public Mono<ResponseEntity<AuthResponse>> refreshToken(@RequestBody final RefreshTokenRequest request) {
        return authService.refreshToken(request);
    }

    /**
     * Logs out a user by invalidating their current session and tokens.
     *
     * <p>This endpoint terminates the user's current session and invalidates their
     * access and refresh tokens. The Authorization header must contain a valid
     * Bearer token for the logout operation to succeed.</p>
     *
     * <p><strong>HTTP Method:</strong> POST</p>
     * <p><strong>Endpoint:</strong> {@code /lms/api/client-api/auth/logout}</p>
     *
     * @param authHeader The Authorization header containing the Bearer token.
     *                   Expected format: "Bearer {access_token}"
     * @return A {@code Mono<ResponseEntity<Map<String, String>>>} containing:
     * <ul>
     *   <li>HTTP 200 OK with success message on successful logout</li>
     *   <li>HTTP 401 Unauthorized if token is invalid or expired</li>
     *   <li>HTTP 400 Bad Request if Authorization header is missing or malformed</li>
     * </ul>
     * @throws IllegalArgumentException if authHeader is null or doesn't start with "Bearer "
     * @see Map
     */
    @PostMapping("/logout")
    public Mono<ResponseEntity<Map<String, String>>> logout(@RequestHeader("Authorization") final String authHeader) {
        return authService.logout(authHeader);
    }
}
