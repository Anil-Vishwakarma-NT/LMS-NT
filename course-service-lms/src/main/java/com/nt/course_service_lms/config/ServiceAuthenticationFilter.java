package com.nt.course_service_lms.config;

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

import static com.nt.course_service_lms.constants.SecurityConstant.BEARER_PREFIX;
import static com.nt.course_service_lms.constants.SecurityConstant.ERROR_FORBIDDEN;
import static com.nt.course_service_lms.constants.SecurityConstant.ERROR_UNAUTHORIZED;
import static com.nt.course_service_lms.constants.SecurityConstant.HEADER_X_GATEWAY_NONCE;
import static com.nt.course_service_lms.constants.SecurityConstant.HEADER_X_GATEWAY_TIMESTAMP;
import static com.nt.course_service_lms.constants.SecurityConstant.HEADER_X_ORIGINAL_TOKEN_TYPE;
import static com.nt.course_service_lms.constants.SecurityConstant.HEADER_X_SERVICE_TOKEN;
import static com.nt.course_service_lms.constants.SecurityConstant.MAX_REQUEST_TIME_DIFF_MS;
import static com.nt.course_service_lms.constants.SecurityConstant.TOKEN_TYPE_SERVICE;

/**
 * Authentication filter for service-to-service communication in the LMS system.
 * This filter handles both gateway-routed requests and direct service requests,
 * validating JWT tokens and gateway signatures as appropriate.
 * <p>
 * The filter supports two types of requests:
 * 1. Gateway requests - routed through API Gateway with additional validation headers
 * 2. Direct requests - direct service-to-service calls (when enabled)
 *
 * @author Course Service LMS Team
 * @version 1.0
 * @since 1.0
 */
@Component
public class ServiceAuthenticationFilter extends OncePerRequestFilter {

    /**
     * Logger instance for this class.
     */
    private final Logger logger = LoggerFactory.getLogger(ServiceAuthenticationFilter.class);

    /**
     * JWT utility for token validation and extraction.
     */
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Flag to enable or disable gateway validation.
     */
    @Value("${gateway.validation.enabled:true}")
    private boolean gatewayValidationEnabled;

    /**
     * Header name for gateway secret validation.
     */
    @Value("${gateway.secret.header:X-Gateway-Secret}")
    private String gatewaySecretHeader;

    /**
     * Expected value for gateway secret header.
     */
    @Value("${gateway.secret.value:your-super-secret-gateway-key}")
    private String gatewaySecretValue;

    /**
     * Header name for gateway signature validation.
     */
    @Value("${gateway.signature.header:X-Gateway-Signature}")
    private String gatewaySignatureHeader;

    /**
     * Expected audience for JWT token validation.
     */
    @Value("${spring.application.name}")
    private String expectedAudience;

    /**
     * Header name for direct access secret.
     */
    @Value("${direct.secret.header:X-Direct-Secret}")
    private String directSecretHeader;

    /**
     * Expected value for direct access secret.
     */
    @Value("${direct.secret.value:your-direct-secret}")
    private String directSecretValue;

    /**
     * Processes each HTTP request through the authentication filter.
     * Validates tokens and headers based on request type (gateway or direct).
     *
     * @param request     the HTTP servlet request
     * @param response    the HTTP servlet response
     * @param filterChain the filter chain to continue processing
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {


        try {
            String path = request.getRequestURI();
            if (path.startsWith("/video/") || path.startsWith("/pdf/")) {
                filterChain.doFilter(request, response);
                return;
            }
            if (path.startsWith("/h2-console") || path.startsWith("/favicon.ico")){
                filterChain.doFilter(request, response);
                return;
            }

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

            boolean isGatewayRequest = true;
            if ("NA".equals(clientId)) {
                isGatewayRequest = false;
            }

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
            logger.debug("Cleared service token context after request processing");
        }
    }

    /**
     * Handles gateway-routed requests by validating gateway headers and service tokens.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @return true if the request is valid and should continue, false otherwise
     * @throws IOException if an I/O error occurs during response writing
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
            setServiceAuthentication(serviceToken, originalTokenType);
            return true;
        } else {
            handleUnauthorized(response, "Invalid or missing service token for gateway request");
            return false;
        }
    }

    /**
     * Validates gateway request headers including secret, signature, timestamp, and nonce.
     *
     * @param request the HTTP servlet request containing gateway headers
     * @return true if all gateway validation checks pass, false otherwise
     */
    private boolean validateGatewayRequest(final HttpServletRequest request) {
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

    /**
     * Validates that the provided secret header matches the expected secret value.
     *
     * @param secretHeader the secret value from the request header
     * @param secretValue  the expected secret value
     * @return true if the secrets match, false otherwise
     */
    private boolean validateSecret(final String secretHeader, final String secretValue) {
        return secretValue.equals(secretHeader);
    }

    /**
     * Validates the gateway signature using timestamp, nonce, and secret.
     * Also checks that the request timestamp is within acceptable time bounds.
     *
     * @param signature the signature from the request header
     * @param timestamp the timestamp from the request header
     * @param nonce     the nonce from the request header
     * @return true if the signature is valid and timestamp is acceptable, false otherwise
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
     * Generates a signature hash based on timestamp, nonce, and secret.
     *
     * @param timestamp the request timestamp
     * @param nonce     the request nonce
     * @param secret    the secret key for signature generation
     * @return the generated signature as a string
     */
    private String generateSignature(final String timestamp, final String nonce, final String secret) {
        String data = timestamp + ":" + nonce + ":" + secret;
        return Integer.toString(data.hashCode());
    }

    /**
     * Handles direct service-to-service requests by validating direct secret and service token.
     *
     * @param request  the HTTP servlet request
     * @param response the HTTP servlet response
     * @return true if the direct request is valid and should continue, false otherwise
     * @throws IOException if an I/O error occurs during response writing
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
     * Sets the service authentication in the Security Context based on the provided token.
     * Extracts user information and roles from the token and creates authentication object.
     *
     * @param token             the validated service token
     * @param originalTokenType the original token type from the request
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
     * Handles unauthorized requests by setting HTTP 401 status and writing error response.
     *
     * @param response the HTTP servlet response
     * @param message  the error message to include in the response
     * @throws IOException if an I/O error occurs during response writing
     */
    private void handleUnauthorized(final HttpServletResponse response, final String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(String.format(ERROR_UNAUTHORIZED, message));
    }

    /**
     * Handles forbidden requests by setting HTTP 403 status and writing error response.
     *
     * @param response the HTTP servlet response
     * @param message  the error message to include in the response
     * @throws IOException if an I/O error occurs during response writing
     */
    private void handleForbidden(final HttpServletResponse response, final String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.getWriter().write(String.format(ERROR_FORBIDDEN, message));
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/h2-console");
    }
}
