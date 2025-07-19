package com.nt.lms.api_gateway_lms.config;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import static com.nt.lms.api_gateway_lms.constant.SecurityConstant.*;

/**
 * JWT Authentication Filter for LMS API Gateway.
 *
 * <p>This filter intercepts HTTP requests and validates JWT tokens for authentication.
 * It handles two types of tokens:
 * <ul>
 *   <li>Access Tokens - Used for user authentication from client applications</li>
 *   <li>Service Tokens - Used for inter-service communication</li>
 * </ul>
 *
 * <p>The filter performs the following operations:
 * <ul>
 *   <li>Extracts JWT tokens from Authorization headers</li>
 *   <li>Validates token authenticity and expiration</li>
 *   <li>Converts access tokens to service tokens for downstream services</li>
 *   <li>Adds gateway security headers for internal communication</li>
 *   <li>Sets up Spring Security authentication context</li>
 * </ul>
 *
 * @author NT LMS Team
 * @version 1.0
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter implements WebFilter {

    /**
     * JWT token utility service for token operations like validation, extraction, and conversion.
     */
    @Autowired
    private JwtUtil jwtTokenManager;

    /**
     * Reactive user details service for loading user information during authentication.
     */
    @Autowired
    private ReactiveUserDetailsService userDetailsService;

    /**
     * Secret value used for gateway-to-service communication authentication.
     * Configured via application properties with key 'gateway.secret.value'.
     */
    @Value("${gateway.secret.value}")
    private String gatewaySecret;

    /**
     * Flag to enable/disable gateway security headers.
     * Defaults to true if not specified in application properties.
     * Configured via 'gateway.security.enabled' property.
     */
    @Value("${gateway.security.enabled:true}")
    private boolean gatewaySecurityEnabled;

    /**
     * Secure random number generator used for creating cryptographically strong nonces
     * for gateway security headers.
     */
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Main filter method that processes incoming HTTP requests for JWT authentication.
     *
     * <p>This method:
     * <ul>
     *   <li>Checks for Authorization header with Bearer token</li>
     *   <li>Determines token type (ACCESS or SERVICE)</li>
     *   <li>Delegates to appropriate handler method</li>
     *   <li>Handles token expiration and validation errors</li>
     * </ul>
     *
     * @param exchange the current server exchange containing request/response
     * @param chain the web filter chain for continuing request processing
     * @return a Mono&lt;Void&gt; representing completion of the filter operation
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            return chain.filter(exchange);
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        try {
            String tokenType = jwtTokenManager.extractTokenType(token);

            return switch (tokenType) {
                case ACCESS_TOKEN_TYPE -> handleAccessToken(exchange, chain, token);
                case SERVICE_TOKEN_TYPE -> handleServiceToken(exchange, chain, token);
                default -> handleUnauthorized(response, INVALID_TOKEN_TYPE_MSG);
            };
        } catch (ExpiredJwtException e) {
            log.warn("JWT token has expired: {}", e.getMessage());
            return handleUnauthorized(response, TOKEN_EXPIRED_MSG);
        } catch (Exception e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            return handleUnauthorized(response, INVALID_TOKEN_MSG);
        }
    }

    /**
     * Handles authentication for access tokens received from client applications.
     *
     * <p>This method:
     * <ul>
     *   <li>Extracts username from the access token</li>
     *   <li>Loads user details from the user service</li>
     *   <li>Validates the token against user information</li>
     *   <li>Converts access token to service token for downstream services</li>
     *   <li>Adds gateway security headers to the request</li>
     *   <li>Sets up Spring Security authentication context</li>
     * </ul>
     *
     * @param exchange the current server exchange
     * @param chain the web filter chain for continuing request processing
     * @param token the access token to be processed
     * @return a Mono&lt;Void&gt; representing completion of access token handling
     */
    private Mono<Void> handleAccessToken(ServerWebExchange exchange, WebFilterChain chain, String token) {
        try {
            String username = jwtTokenManager.extractEmail(token);

            return userDetailsService.findByUsername(username)
                    .cast(Object.class)
                    .flatMap(userDetails -> {
                        if (jwtTokenManager.validateAccessToken(token, username)) {
                            String targetService = extractTargetService(exchange.getRequest());
                            String serviceToken = jwtTokenManager.convertToServiceToken(token, targetService);

                            ServerHttpRequest modifiedRequest = addGatewaySecurityHeaders(
                                    exchange.getRequest(), serviceToken, ACCESS_TOKEN_TYPE);

                            ServerWebExchange modifiedExchange = exchange.mutate()
                                    .request(modifiedRequest)
                                    .build();

                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(
                                            userDetails,
                                            null,
                                            ((UserDetails) userDetails).getAuthorities()
                                    );

                            return chain.filter(modifiedExchange)
                                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
                        } else {
                            return handleUnauthorized(exchange.getResponse(), INVALID_ACCESS_TOKEN_MSG);
                        }
                    })
                    .switchIfEmpty(handleUnauthorized(exchange.getResponse(), USER_NOT_FOUND_MSG));

        } catch (Exception e) {
            log.error("Error handling access token: {}", e.getMessage());
            return handleUnauthorized(exchange.getResponse(), TOKEN_PROCESSING_ERROR_MSG);
        }
    }

    /**
     * Handles authentication for service tokens used in inter-service communication.
     *
     * <p>This method:
     * <ul>
     *   <li>Validates the service token for the target service</li>
     *   <li>Extracts roles/authorities from the token</li>
     *   <li>Adds gateway security headers to the request</li>
     *   <li>Sets up Spring Security authentication context with service authorities</li>
     * </ul>
     *
     * @param exchange the current server exchange
     * @param chain the web filter chain for continuing request processing
     * @param token the service token to be processed
     * @return a Mono&lt;Void&gt; representing completion of service token handling
     */
    private Mono<Void> handleServiceToken(ServerWebExchange exchange, WebFilterChain chain, String token) {
        try {
            String targetService = extractTargetService(exchange.getRequest());

            if (jwtTokenManager.validateServiceToken(token, targetService)) {
                List<String> roles = jwtTokenManager.extractRoles(token);

                ServerHttpRequest modifiedRequest = addGatewaySecurityHeaders(
                        exchange.getRequest(), token, SERVICE_TOKEN_TYPE);

                ServerWebExchange modifiedExchange = exchange.mutate()
                        .request(modifiedRequest)
                        .build();

                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                jwtTokenManager.extractSubject(token),
                                null,
                                authorities
                        );

                return chain.filter(modifiedExchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
            } else {
                return handleUnauthorized(exchange.getResponse(), INVALID_SERVICE_TOKEN_MSG);
            }

        } catch (Exception e) {
            log.error("Error handling service token: {}", e.getMessage());
            return handleUnauthorized(exchange.getResponse(), SERVICE_TOKEN_PROCESSING_ERROR_MSG);
        }
    }

    /**
     * Adds gateway security headers to the HTTP request for secure inter-service communication.
     *
     * <p>This method adds:
     * <ul>
     *   <li>Service token header for authentication</li>
     *   <li>Original token type header for context</li>
     *   <li>Gateway security headers (if enabled): secret, timestamp, nonce, signature, source</li>
     * </ul>
     *
     * <p>The security headers include a cryptographic signature to prevent tampering
     * and ensure the request originated from the API gateway.
     *
     * @param request the original HTTP request
     * @param serviceToken the service token to be included in headers
     * @param originalTokenType the type of the original token (ACCESS or SERVICE)
     * @return a new ServerHttpRequest with added security headers
     */
    private ServerHttpRequest addGatewaySecurityHeaders(ServerHttpRequest request,
                                                        String serviceToken,
                                                        String originalTokenType) {

        ServerHttpRequest.Builder builder = request.mutate()
                .header(SERVICE_TOKEN_HEADER, serviceToken)
                .header(ORIGINAL_TOKEN_TYPE_HEADER, originalTokenType);

        if (gatewaySecurityEnabled) {
            long timestamp = System.currentTimeMillis();
            String nonce = generateNonce();
            String signature = generateSignature(timestamp, nonce, gatewaySecret);

            builder.header(GATEWAY_SECRET_HEADER, gatewaySecret)
                    .header(GATEWAY_TIMESTAMP_HEADER, String.valueOf(timestamp))
                    .header(GATEWAY_NONCE_HEADER, nonce)
                    .header(GATEWAY_SIGNATURE_HEADER, signature)
                    .header(GATEWAY_SOURCE_HEADER, GATEWAY_SOURCE_VALUE);
        }

        return builder.build();
    }

    /**
     * Generates a cryptographically secure random nonce for gateway security headers.
     *
     * <p>The nonce is a 16-byte random value encoded in Base64 format.
     * It helps prevent replay attacks by ensuring each request has a unique identifier.
     *
     * @return a Base64-encoded random nonce string
     */
    private String generateNonce() {
        byte[] nonce = new byte[16];
        secureRandom.nextBytes(nonce);
        return Base64.getEncoder().encodeToString(nonce);
    }

    /**
     * Generates a signature for gateway security headers using timestamp, nonce, and secret.
     *
     * <p>The signature is created by combining timestamp, nonce, and gateway secret,
     * then computing a hash code. This provides a simple integrity check to verify
     * that the request originated from the gateway and hasn't been tampered with.
     *
     * <p><strong>Note:</strong> This implementation uses hashCode() for simplicity.
     * In production environments, consider using HMAC-SHA256 for better security.
     *
     * @param timestamp the current timestamp in milliseconds
     * @param nonce the random nonce value
     * @param secret the gateway secret key
     * @return a signature string for request verification
     */
    private String generateSignature(long timestamp, String nonce, String secret) {
        String data = timestamp + ":" + nonce + ":" + secret;
        return Integer.toString(data.hashCode());
    }

    /**
     * Extracts the target service name from the request path.
     *
     * <p>This method analyzes the request path to determine which downstream service
     * should handle the request. The routing is based on path prefixes:
     * <ul>
     *   <li>/lms/user/ routes to USER_SERVICE</li>
     *   <li>/lms/course/ routes to COURSE_SERVICE</li>
     *   <li>All other paths return UNKNOWN_SERVICE</li>
     * </ul>
     *
     * @param request the HTTP request containing the path information
     * @return the target service name as defined in SecurityConstant
     */
    private String extractTargetService(ServerHttpRequest request) {
        String path = request.getPath().value();
        if (path.startsWith("/lms/user/")) {
            return USER_SERVICE;
        }
        if (path.startsWith("/lms/course/")) {
            return COURSE_SERVICE;
        }
        return UNKNOWN_SERVICE;
    }

    /**
     * Handles unauthorized requests by setting appropriate HTTP response status and body.
     *
     * <p>This method:
     * <ul>
     *   <li>Checks if the response is already committed to avoid IllegalStateException</li>
     *   <li>Sets HTTP status to 401 Unauthorized</li>
     *   <li>Sets Content-Type to application/json</li>
     *   <li>Writes a JSON error response with the provided message</li>
     * </ul>
     *
     * @param response the HTTP response object
     * @param message the error message to include in the response body
     * @return a Mono&lt;Void&gt; representing completion of the error response writing
     */
    private Mono<Void> handleUnauthorized(ServerHttpResponse response, String message) {
        if (response.isCommitted()) {
            log.warn("Response already committed, cannot set unauthorized status");
            return Mono.empty();
        }
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", "application/json");
        String body = String.format("{\"error\": \"Unauthorized\", \"message\": \"%s\"}", message);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }
}
