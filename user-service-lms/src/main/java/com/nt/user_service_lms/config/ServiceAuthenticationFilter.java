package com.nt.user_service_lms.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.nt.user_service_lms.constants.SecurityConstant.BEARER_PREFIX;
import static com.nt.user_service_lms.constants.SecurityConstant.ERROR_FORBIDDEN;
import static com.nt.user_service_lms.constants.SecurityConstant.ERROR_UNAUTHORIZED;
import static com.nt.user_service_lms.constants.SecurityConstant.HEADER_X_GATEWAY_NONCE;
import static com.nt.user_service_lms.constants.SecurityConstant.HEADER_X_GATEWAY_TIMESTAMP;
import static com.nt.user_service_lms.constants.SecurityConstant.HEADER_X_ORIGINAL_TOKEN_TYPE;
import static com.nt.user_service_lms.constants.SecurityConstant.HEADER_X_SERVICE_TOKEN;
import static com.nt.user_service_lms.constants.SecurityConstant.MAX_REQUEST_TIME_DIFF_MS;
import static com.nt.user_service_lms.constants.SecurityConstant.TOKEN_TYPE_SERVICE;

/**
 * Filter to authenticate internal service-to-service communication using JWT tokens
 * and gateway/direct secret headers. Validates requests from API Gateway and
 * direct service calls based on headers and configuration.
 */
@Profile("!test")
@Component
public class ServiceAuthenticationFilter extends OncePerRequestFilter {

    /**
     * Logger for logging filter activities.
     */
    private final Logger logger = LoggerFactory.getLogger(ServiceAuthenticationFilter.class);

    /**
     * Utility for working with JWT tokens, including validation and claims extraction.
     */
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Flag to enable or disable gateway validation. Defaults to true.
     */
    @Value("${gateway.validation.enabled:true}")
    private boolean gatewayValidationEnabled;

    /**
     * Header name for the gateway secret.
     */
    @Value("${gateway.secret.header:X-Gateway-Secret}")
    private String gatewaySecretHeader;

    /**
     * Expected secret value for gateway validation.
     */
    @Value("${gateway.secret.value:your-super-secret-gateway-key}")
    private String gatewaySecretValue;

    /**
     * Header name for the gateway request signature.
     */
    @Value("${gateway.signature.header:X-Gateway-Signature}")
    private String gatewaySignatureHeader;

    /**
     * The expected audience claim (application name) for JWT validation.
     */
    @Value("${spring.application.name}")
    private String expectedAudience;

    /**
     * Header name for direct service call secret.
     */
    @Value("${direct.secret.header:X-Direct-Secret}")
    private String directSecretHeader;

    /**
     * Expected secret value for direct access.
     */
    @Value("${direct.secret.value:your-direct-secret}")
    private String directSecretValue;

    /**
     * Performs filtering on every request, checking JWT tokens and headers to
     * determine authentication and validity for gateway or direct service calls.
     *
     * @param request     incoming HTTP request
     * @param response    HTTP response
     * @param filterChain chain of filters to continue processing
     * @throws ServletException in case of filter error
     * @throws IOException      in case of I/O error
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {

        try {
            String serviceToken = request.getHeader(HEADER_X_SERVICE_TOKEN);
            String clientId = null;
            if (serviceToken != null) {
                try {
                    clientId = jwtUtil.extractClientId(serviceToken);
                } catch (Exception e) {
                    logger.warn("Failed to extract subject from token: {}", e.getMessage());
                    handleUnauthorized(response, "failed to fetch the clientId from the token, jwt token is expired");
                    return;
                }
            }

            boolean isGatewayRequest = !"NA".equals(clientId);

            logger.debug("Token subject: {}, isGatewayRequest: {}", clientId, isGatewayRequest);

            if (isGatewayRequest) {
                if (!handleGatewayRequest(request, response)) {
                    return;
                }
            } else {
                if (!gatewayValidationEnabled) {
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

    /**
     * Handles service-to-service authentication for requests routed via API Gateway.
     *
     * @param request  HTTP request
     * @param response HTTP response
     * @return true if valid and authenticated, false otherwise
     * @throws IOException in case of I/O error
     */
    private boolean handleGatewayRequest(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException {

        logger.debug("Processing Gateway Request");

        if (!validateGatewayRequest(request)) {
            handleForbidden(response, "Gateway request header validation failed.");
            return false;
        }

        String serviceToken = request.getHeader(HEADER_X_SERVICE_TOKEN);
        String originalTokenType = request.getHeader(HEADER_X_ORIGINAL_TOKEN_TYPE);

        if (serviceToken == null) {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
                serviceToken = authHeader.substring(BEARER_PREFIX.length());
                originalTokenType = TOKEN_TYPE_SERVICE;
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

    /**
     * Validates headers in a gateway request.
     *
     * @param request HTTP request
     * @return true if valid, false otherwise
     */
    private boolean validateGatewayRequest(final HttpServletRequest request) {
        String gatewaySecret = request.getHeader(gatewaySecretHeader);
        String signature = request.getHeader(gatewaySignatureHeader);
        String timestamp = request.getHeader(HEADER_X_GATEWAY_TIMESTAMP);
        String nonce = request.getHeader(HEADER_X_GATEWAY_NONCE);

        return validateSecret(gatewaySecret, gatewaySecretValue)
                && validateSignature(signature, timestamp, nonce);
    }

    /**
     * Validates a header-based secret.
     *
     * @param secretHeader the incoming secret
     * @param secretValue  the expected secret
     * @return true if they match, false otherwise
     */
    private boolean validateSecret(final String secretHeader, final String secretValue) {
        return secretValue.equals(secretHeader);
    }

    /**
     * Validates the request signature using timestamp and nonce.
     *
     * @param signature the provided signature
     * @param timestamp the request timestamp
     * @param nonce     a unique request identifier
     * @return true if valid, false otherwise
     */
    private boolean validateSignature(final String signature, final String timestamp, final String nonce) {
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

    /**
     * Generates a signature string from components.
     *
     * @param timestamp timestamp string
     * @param nonce     nonce string
     * @param secret    shared secret
     * @return hash-based signature string
     */
    private String generateSignature(final String timestamp, final String nonce, final String secret) {
        String data = timestamp + ":" + nonce + ":" + secret;
        return Integer.toString(data.hashCode());
    }

    /**
     * Handles validation and authentication for direct (non-gateway) service requests.
     *
     * @param request  HTTP request
     * @param response HTTP response
     * @return true if authenticated, false otherwise
     * @throws IOException in case of error writing response
     */
    private boolean handleDirectRequest(final HttpServletRequest request, final HttpServletResponse response)
            throws IOException {

        logger.debug("Processing Direct Request");
        String directSecret = request.getHeader(directSecretHeader);

        if (directSecret == null) {
            handleUnauthorized(response, "Direct secret header is required for direct access");
            return false;
        }

        if (!validateSecret(directSecret, directSecretValue)) {
            handleForbidden(response, "Invalid Direct Secret Header");
            return false;
        }

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

    /**
     * Sets the authentication object in the Spring SecurityContext based on token data.
     *
     * @param token             the JWT service token
     * @param originalTokenType the original token type (e.g., SERVICE)
     */
    private void setServiceAuthentication(final String token, final String originalTokenType) {
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

    /**
     * Sends an unauthorized (401) response with a JSON error message.
     *
     * @param response HTTP response
     * @param message  the error message to send
     * @throws IOException in case of write failure
     */
    private void handleUnauthorized(final HttpServletResponse response, final String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(String.format(ERROR_UNAUTHORIZED, message));
    }

    /**
     * Sends a forbidden (403) response with a JSON error message.
     *
     * @param response HTTP response
     * @param message  the error message to send
     * @throws IOException in case of write failure
     */
    private void handleForbidden(final HttpServletResponse response, final String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write(String.format(ERROR_FORBIDDEN, message));
    }
}
