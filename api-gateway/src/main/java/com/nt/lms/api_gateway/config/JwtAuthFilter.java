package com.nt.lms.api_gateway.config;

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
import static com.nt.lms.api_gateway.constant.SecurityConstant.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter implements WebFilter {

    @Autowired
    private JwtUtil jwtTokenManager;

    @Autowired
    private ReactiveUserDetailsService userDetailsService;

    @Value("${gateway.secret.value}")
    private String gatewaySecret;

    @Value("${gateway.security.enabled:true}")
    private boolean gatewaySecurityEnabled;

    private final SecureRandom secureRandom = new SecureRandom();

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
                    .header(GATEWAY_SOURCE_HEADER, GATEWAY_SOURCE_VALUE)
                    .header(HttpHeaders.USER_AGENT, USER_AGENT_HEADER_VALUE);
        }

        return builder.build();
    }

    private String generateNonce() {
        byte[] nonce = new byte[16];
        secureRandom.nextBytes(nonce);
        return Base64.getEncoder().encodeToString(nonce);
    }

    private String generateSignature(long timestamp, String nonce, String secret) {
        String data = timestamp + ":" + nonce + ":" + secret;
        return Integer.toString(data.hashCode());
    }

    private String extractTargetService(ServerHttpRequest request) {
        String path = request.getPath().value();
        if (path.startsWith("/user/")) return USER_SERVICE;
        if (path.startsWith("/course/")) return COURSE_SERVICE;
        if (path.startsWith("/product/")) return PRODUCT_SERVICE;
        return UNKNOWN_SERVICE;
    }

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
