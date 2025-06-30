package com.nt.LMS.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.nt.LMS.constants.SecurityConstant.*;

@Component
public class ServiceAuthenticationFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(ServiceAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${gateway.validation.enabled:true}")
    private boolean gatewayValidationEnabled;

    @Value("${gateway.secret.header:X-Gateway-Secret}")
    private String gatewaySecretHeader;

    @Value("${gateway.secret.value:your-super-secret-gateway-key}")
    private String gatewaySecretValue;

    @Value("${gateway.signature.header:X-Gateway-Signature}")
    private String gatewaySignatureHeader;

    @Value("${spring.application.name}")
    private String expectedAudience;

    @Value("${direct.secret.header:X-Direct-Secret}")
    private String directSecretHeader;

    @Value("${direct.secret.value:your-direct-secret}")
    private String directSecretValue;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String serviceToken = request.getHeader(HEADER_X_SERVICE_TOKEN);
            System.out.println(serviceToken);
            String clientId = null;
            if (serviceToken != null) {
                try {
                    clientId = jwtUtil.extractClientId(serviceToken); // may return null
                } catch (Exception e) {
                    logger.warn("Failed to extract subject from token: {}", e.getMessage());
//                    throw new ResourceNotFoundException("failed to fetch the clientId from the token" + e.getMessage());
                    handleUnauthorized(response,"failed to fetch the clientId from the token, jwt token is expired");
                    return;
                }
            }

            boolean isGatewayRequest = true;
            if ("NA".equals(clientId)) {
                isGatewayRequest = false;
            }
            System.out.println(isGatewayRequest);
            System.out.println(serviceToken);

            logger.debug("Token subject: {}, isGatewayRequest: {}", clientId, isGatewayRequest);

            if (isGatewayRequest) {
                // Gateway requests are always allowed, but validated
                System.out.println("gateway header .......................");
                if (!handleGatewayRequest(request, response)) {
                    return;
                }
            } else {
                // Direct requests — allowed only when gatewayValidationEnabled is false
                if (!gatewayValidationEnabled) {
                    System.out.println("direct header...............");
                    if (!handleDirectRequest(request, response)) {
                        return;
                    }
                } else {
                    handleForbidden(response, "Direct access not allowed. Please route via API Gateway.");
                    return;
                }
            }

            filterChain.doFilter(request, response);

        } finally {
            ServiceTokenContext.clear();
            logger.debug("Cleared service token context after request processing");
        }
    }

    private boolean handleGatewayRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        logger.debug("Processing Gateway Request");

        // For gateway requests, validate gateway headers if gatewayValidationEnabled is true
        if (!validateGatewayRequest(request)) {
            handleForbidden(response, "Gateway request header validation failed.");
            return false;
        }

        // For gateway requests, check both X-Service-Token and Authorization header
        String serviceToken = request.getHeader(HEADER_X_SERVICE_TOKEN);
        String originalTokenType = request.getHeader(HEADER_X_ORIGINAL_TOKEN_TYPE);

        // If no X-Service-Token, check Authorization header for service token
        if (serviceToken == null) {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
                serviceToken = authHeader.substring(BEARER_PREFIX.length());
                originalTokenType = TOKEN_TYPE_SERVICE; // Default for gateway requests
            }
        }

        if (serviceToken != null && jwtUtil.validateServiceToken(serviceToken, expectedAudience)) {
            ServiceTokenContext.setCurrentToken(serviceToken);
            ServiceTokenContext.setOriginalTokenType(originalTokenType);
            setServiceAuthentication(serviceToken, originalTokenType);
            logger.debug("Gateway service token stored in context and authentication set");
            return true;
        } else {
            handleUnauthorized(response, "Invalid or missing service token for gateway request");
            return false;
        }
    }

    private boolean validateGatewayRequest(HttpServletRequest request) {
        String gatewaySecret = request.getHeader(gatewaySecretHeader);
        String signature = request.getHeader(gatewaySignatureHeader);
        String timestamp = request.getHeader(HEADER_X_GATEWAY_TIMESTAMP);
        String nonce = request.getHeader(HEADER_X_GATEWAY_NONCE);

        if (!validateSecret(gatewaySecret, gatewaySecretValue)) {
            logger.warn("Gateway secret validation failed");
            return false;
        }

        if (!validateSignature(signature, timestamp, nonce)) {
            logger.warn("Gateway signature validation failed");
            return false;
        }

        return true;
    }

    private boolean validateSecret(String secretHeader, String secretValue) {
        return secretValue.equals(secretHeader);
    }

    private boolean validateSignature(String signature, String timestamp, String nonce) {
        if (signature == null || timestamp == null || nonce == null) {
            return false;
        }

        try {
            long requestTime = Long.parseLong(timestamp);
            long currentTime = System.currentTimeMillis();
            long timeDiff = Math.abs(currentTime - requestTime);

            if (timeDiff > MAX_REQUEST_TIME_DIFF_MS) {
                logger.warn("Request timestamp too old: {}", timeDiff);
                return false;
            }

            String expectedSignature = generateSignature(timestamp, nonce, gatewaySecretValue);
            return signature.equals(expectedSignature);

        } catch (Exception e) {
            logger.warn("Gateway signature validation error: {}", e.getMessage());
            return false;
        }
    }

    private String generateSignature(String timestamp, String nonce, String secret) {
        String data = timestamp + ":" + nonce + ":" + secret;
        return Integer.toString(data.hashCode());
    }

    private boolean handleDirectRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        logger.debug("Processing Direct Request");
        System.out.println("Direct request received ..................");
        String directSecret = request.getHeader(directSecretHeader);

        if (directSecret == null) {
            handleUnauthorized(response, "Direct secret header is required for direct access");
            return false;
        }

        if (!validateSecret(directSecret, directSecretValue)) {
            handleForbidden(response, "Invalid Direct Secret Header");
            return false;
        }

        // For direct requests, service token should be in Authorization header
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            String serviceToken = authHeader.substring(BEARER_PREFIX.length());
            try {
                if (jwtUtil.validateServiceToken(serviceToken, expectedAudience)) {
                    ServiceTokenContext.setCurrentToken(serviceToken);
                    ServiceTokenContext.setOriginalTokenType(TOKEN_TYPE_SERVICE);

                    setServiceAuthentication(serviceToken, TOKEN_TYPE_SERVICE);
                    logger.debug("Direct service token stored in context and authentication set");
                    return true;
                } else {
                    handleUnauthorized(response, "Invalid service token for direct access");
                    return false;
                }
            } catch (Exception e) {
                logger.warn("Direct service token validation failed: {}", e.getMessage());
                handleUnauthorized(response, "Service token validation failed");
                return false;
            }
        } else {
            handleUnauthorized(response, "Missing service token in Authorization header for direct access");
            return false;
        }
    }

    private void setServiceAuthentication(String token, String originalTokenType) {
        List<String> roles = jwtUtil.extractRoles(token);
        String userId = jwtUtil.extractUserId(token);
        String userEmail = jwtUtil.extractUserEmail(token);
        String userFullName = jwtUtil.extractUserFullName(token);
        List<String> userRoles = jwtUtil.extractUserRoles(token);

        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        if (userRoles != null) {
            userRoles.stream()
                    .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                    .map(SimpleGrantedAuthority::new)
                    .forEach(authorities::add);
        }

        ServicePrincipal principal = new ServicePrincipal.Builder()
                .serviceId(jwtUtil.extractClientId(token))
                .userId(userId)
                .userEmail(userEmail)
                .userFullName(userFullName)
                .originalTokenType(originalTokenType)
                .build();

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        logger.debug("Service authentication set for user: {}, service: {}, authorities: {}",
                userEmail, principal.getServiceId(), authorities);
    }

    private void handleUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(String.format(ERROR_UNAUTHORIZED, message));
    }

    private void handleForbidden(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write(String.format(ERROR_FORBIDDEN, message));
    }
}