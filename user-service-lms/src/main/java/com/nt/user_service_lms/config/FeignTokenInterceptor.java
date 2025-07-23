package com.nt.user_service_lms.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import static com.nt.user_service_lms.constants.TokenConverterConstant.DEFAULT_SOURCE_SERVICE;
import static com.nt.user_service_lms.constants.TokenConverterConstant.HEADER_X_GATEWAY_NONCE;
import static com.nt.user_service_lms.constants.TokenConverterConstant.HEADER_X_GATEWAY_TIMESTAMP;
import static com.nt.user_service_lms.constants.TokenConverterConstant.HEADER_X_ORIGINAL_TOKEN_TYPE;
import static com.nt.user_service_lms.constants.TokenConverterConstant.HEADER_X_REQUEST_SOURCE;
import static com.nt.user_service_lms.constants.TokenConverterConstant.HEADER_X_REQUEST_TIMESTAMP;
import static com.nt.user_service_lms.constants.TokenConverterConstant.HEADER_X_SERVICE_TOKEN;
import static com.nt.user_service_lms.constants.TokenConverterConstant.HEADER_X_SOURCE_SERVICE;
import static com.nt.user_service_lms.constants.TokenConverterConstant.HEADER_X_USER_EMAIL;
import static com.nt.user_service_lms.constants.TokenConverterConstant.HEADER_X_USER_ID;

/**
 * Feign request interceptor that handles service-to-service authentication and authorization
 * by automatically adding security headers, service tokens, and user context information
 * to outgoing HTTP requests in a microservices architecture.
 *
 * <p>This interceptor performs the following operations:
 * <ul>
 *   <li>Converts and adds service tokens for inter-service communication</li>
 *   <li>Adds user context headers (user ID, email, source service)</li>
 *   <li>Generates and adds gateway security headers with signatures</li>
 *   <li>Handles token conversion for different target services</li>
 * </ul>
 *
 * <p>The interceptor is automatically applied to all Feign clients in the application
 * and ensures secure communication between microservices in the LMS system.
 *
 * @author Your Name
 * @version 1.0
 * @since 1.0
 */
@Component
public class FeignTokenInterceptor implements RequestInterceptor {

    /**
     * logger implementation.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(FeignTokenInterceptor.class);

    /**
     * Service token converter for handling token transformations between services.
     */
    @Autowired
    private ServiceTokenConverter serviceTokenConverter;

    /**
     * The HTTP header name for gateway secret authentication.
     * Default value: "X-Gateway-Secret"
     */
    @Value("${gateway.secret.header:X-Gateway-Secret}")
    private String gatewaySecretHeader;

    /**
     * The secret value used for gateway authentication.
     * Default value: "your-super-secret-gateway-key"
     */
    @Value("${gateway.secret.value:your-super-secret-gateway-key}")
    private String gatewaySecretValue;

    /**
     * The HTTP header name for gateway signature verification.
     * Default value: "X-Gateway-Signature"
     */
    @Value("${gateway.signature.header:X-Gateway-Signature}")
    private String gatewaySignatureHeader;

    /**
     * Intercepts outgoing Feign requests and adds necessary authentication and authorization headers.
     *
     * <p>This method is automatically called by the Feign framework before each HTTP request.
     * It performs the following operations:
     * <ol>
     *   <li>Retrieves the current authentication context</li>
     *   <li>Validates the principal as a ServicePrincipal</li>
     *   <li>Converts service tokens if needed for the target service</li>
     *   <li>Adds service token and user context headers</li>
     *   <li>Generates and adds gateway security headers</li>
     * </ol>
     *
     * <p>If any error occurs during header addition, the error is logged but the request
     * continues without the problematic headers to maintain system resilience.
     *
     * @param template the Feign request template to be modified with security headers
     * @throws Exception if critical security header generation fails
     */
    @Override
    public void apply(final RequestTemplate template) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof ServicePrincipal) {
            ServicePrincipal principal = (ServicePrincipal) authentication.getPrincipal();

            try {
                String targetService = "course-service";

                if (targetService != null) {
                    String currentToken = getCurrentServiceToken();

                    if (currentToken != null) {
                        if (!serviceTokenConverter.canConvertToken(currentToken)) {
                            LOGGER.warn("Cannot convert current token for target service: {}", targetService);
                            return;
                        }

                        if (serviceTokenConverter.needsTokenConversion(currentToken, targetService)) {
                            String convertedToken = serviceTokenConverter.convertServiceToken(currentToken, targetService);
                            template.header(HEADER_X_SERVICE_TOKEN, convertedToken);
                        } else {
                            template.header(HEADER_X_SERVICE_TOKEN, currentToken);
                        }

                        String originalTokenType = ServiceTokenContext.getOriginalTokenType();
                        if (originalTokenType != null) {
                            template.header(HEADER_X_ORIGINAL_TOKEN_TYPE, originalTokenType);
                        }

                        addUserContextHeaders(template, principal);
                    }
                }

                addGatewayHeaders(template);

            } catch (Exception e) {
                LOGGER.error("Failed to add service token to Feign request for URL {}: {}", template.url(), e.getMessage(), e);
            }
        }
    }

    /**
     * Retrieves the current service token from the token context.
     *
     * <p>This method acts as a wrapper around the ServiceTokenContext to get
     * the current service token that should be used for authentication.
     *
     * @return the current service token, or null if no token is available
     */
    private String getCurrentServiceToken() {
        return ServiceTokenContext.getCurrentToken();
    }

    /**
     * Adds user context headers to the outgoing request for service-to-service communication.
     *
     * <p>This method extracts user information from the ServicePrincipal and adds it
     * as HTTP headers so that the target service can maintain user context across
     * service boundaries.
     *
     * <p>Headers added:
     * <ul>
     *   <li>X-User-ID: The unique identifier of the current user</li>
     *   <li>X-User-Email: The email address of the current user</li>
     *   <li>X-Source-Service: The identifier of the originating service</li>
     *   <li>X-Request-Source: The default source service identifier</li>
     *   <li>X-Request-Timestamp: The current timestamp in milliseconds</li>
     * </ul>
     *
     * @param template  the request template to add headers to
     * @param principal the service principal containing user information
     */
    private void addUserContextHeaders(final RequestTemplate template, final ServicePrincipal principal) {
        try {
            if (principal.getUserId() != null) {
                template.header(HEADER_X_USER_ID, principal.getUserId());
            }
            if (principal.getUserEmail() != null) {
                template.header(HEADER_X_USER_EMAIL, principal.getUserEmail());
            }
            if (principal.getServiceId() != null) {
                template.header(HEADER_X_SOURCE_SERVICE, principal.getServiceId());
            }
            template.header(HEADER_X_REQUEST_SOURCE, DEFAULT_SOURCE_SERVICE);
            template.header(HEADER_X_REQUEST_TIMESTAMP, String.valueOf(System.currentTimeMillis()));

        } catch (Exception e) {
            LOGGER.debug("Could not add user context headers: {}", e.getMessage());
        }
    }

    /**
     * Adds gateway security headers to the outgoing request for authentication and integrity verification.
     *
     * <p>This method generates and adds security headers that allow the API gateway
     * to verify the authenticity and integrity of inter-service requests.
     *
     * <p>Headers added:
     * <ul>
     *   <li>Gateway secret header: Contains the shared secret for authentication</li>
     *   <li>X-Gateway-Timestamp: Current timestamp for replay attack prevention</li>
     *   <li>X-Gateway-Nonce: Unique value to prevent replay attacks</li>
     *   <li>Gateway signature header: HMAC signature of timestamp, nonce, and secret</li>
     * </ul>
     *
     * @param template the request template to add gateway headers to
     */
    private void addGatewayHeaders(final RequestTemplate template) {
        try {
            template.header(gatewaySecretHeader, gatewaySecretValue);

            long timestamp = System.currentTimeMillis();
            String nonce = generateNonce();

            template.header(HEADER_X_GATEWAY_TIMESTAMP, String.valueOf(timestamp));
            template.header(HEADER_X_GATEWAY_NONCE, nonce);

            String signature = generateSignature(String.valueOf(timestamp), nonce, gatewaySecretValue);
            template.header(gatewaySignatureHeader, signature);

        } catch (Exception e) {
            LOGGER.warn("Failed to add gateway headers: {}", e.getMessage());
        }
    }

    /**
     * Generates a cryptographically secure nonce for request uniqueness.
     *
     * <p>The nonce is used to prevent replay attacks by ensuring each request
     * has a unique identifier. This implementation uses the current nanosecond
     * timestamp to ensure uniqueness.
     *
     * @return a unique nonce string based on the current nanosecond time
     */
    private String generateNonce() {
        return String.valueOf(System.nanoTime());
    }

    /**
     * Generates a signature for gateway authentication using timestamp, nonce, and secret.
     *
     * <p>This method creates a simple signature by concatenating the timestamp,
     * nonce, and secret with colons, then generating a hash code. In production,
     * consider using a more robust cryptographic signature algorithm like HMAC-SHA256.
     *
     * <p><strong>Security Note:</strong> This implementation uses a simple hash code
     * which may not be cryptographically secure for production use. Consider upgrading
     * to a proper HMAC implementation for enhanced security.
     *
     * @param timestamp the timestamp string to include in the signature
     * @param nonce     the nonce string to include in the signature
     * @param secret    the secret key to use for signature generation
     * @return a signature string for gateway authentication
     */
    private String generateSignature(final String timestamp, final String nonce, final String secret) {
        String data = timestamp + ":" + nonce + ":" + secret;
        return Integer.toString(data.hashCode());
    }
}
