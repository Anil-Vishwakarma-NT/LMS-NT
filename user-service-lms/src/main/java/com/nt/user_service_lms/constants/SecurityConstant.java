package com.nt.user_service_lms.constants;

/**
 * Security-related constants used throughout the user service LMS application.
 * This class contains various constants for HTTP headers, authentication tokens,
 * error responses, JWT claims, and security-related configurations.
 *
 * <p>All constants are public static final to ensure they are immutable and
 * can be accessed without instantiating the class.</p>
 *
 * @author System
 * @version 1.0
 * @since 1.0
 */
public final class SecurityConstant {

    private SecurityConstant() {

    }

    /**
     * HTTP header name for gateway timestamp.
     * Used to validate the freshness of requests coming through the gateway.
     */
    public static final String HEADER_X_GATEWAY_TIMESTAMP = "X-Gateway-Timestamp";

    /**
     * HTTP header name for gateway nonce.
     * Used to prevent replay attacks by ensuring request uniqueness.
     */
    public static final String HEADER_X_GATEWAY_NONCE = "X-Gateway-Nonce";

    /**
     * HTTP header name for service token.
     * Contains the JWT token used for service-to-service authentication.
     */
    public static final String HEADER_X_SERVICE_TOKEN = "X-Service-Token";

    /**
     * HTTP header name for original token type.
     * Indicates the type of the original token before any conversions.
     */
    public static final String HEADER_X_ORIGINAL_TOKEN_TYPE = "X-Original-Token-Type";

    // Authorization

    /**
     * Standard HTTP authorization header name.
     * Used for carrying authentication credentials in HTTP requests.
     */
    public static final String HEADER_AUTHORIZATION = "Authorization";

    /**
     * Bearer token prefix used in Authorization header.
     * Standard prefix for JWT tokens in Authorization header (e.g., "Bearer eyJ0eXAi...").
     */
    public static final String BEARER_PREFIX = "Bearer ";

    // Error responses

    /**
     * JSON error response template for unauthorized access.
     * Contains a placeholder (%s) for the specific error message.
     * Format: {"error": "Unauthorized", "message": "specific message"}
     */
    public static final String ERROR_UNAUTHORIZED = "{\"error\": \"Unauthorized\", \"message\": \"%s\"}";

    /**
     * JSON error response template for forbidden access.
     * Contains a placeholder (%s) for the specific error message.
     * Format: {"error": "Forbidden", "message": "specific message"}
     */
    public static final String ERROR_FORBIDDEN = "{\"error\": \"Forbidden\", \"message\": \"%s\"}";

    // Token type

    /**
     * Token type identifier for service tokens.
     * Used to distinguish service tokens from other token types (e.g., user tokens).
     */
    public static final String TOKEN_TYPE_SERVICE = "service";

    // Time constants

    /**
     * Maximum allowed time difference in milliseconds for request validation.
     * Set to 5 minutes (300,000 ms) to prevent replay attacks while allowing
     * for reasonable clock skew between services.
     */
    public static final long MAX_REQUEST_TIME_DIFF_MS = 5 * 60 * 1000; // 5 minutes

    /**
     * Role identifier for service authentication.
     * Standard role assigned to service tokens for authorization purposes.
     */
    public static final String ROLE_SERVICE = "ROLE_SERVICE";

    // Claim Keys

    /**
     * JWT claim key for token type.
     * Used to identify the type of token (e.g., "service", "user").
     */
    public static final String CLAIM_TOKEN_TYPE = "token_type";

    /**
     * JWT claim key for roles.
     * Contains the list of roles assigned to the token holder.
     */
    public static final String CLAIM_ROLES = "roles";

    /**
     * JWT claim key for scope.
     * Defines the permissions or scope of access granted by the token.
     */
    public static final String CLAIM_SCOPE = "scope";

    /**
     * JWT claim key for user ID.
     * Contains the unique identifier of the user associated with the token.
     */
    public static final String CLAIM_USER_ID = "userId";

    /**
     * JWT claim key for user email.
     * Contains the email address of the user associated with the token.
     */
    public static final String CLAIM_USER_EMAIL = "user_email";

    /**
     * JWT claim key for user full name.
     * Contains the complete name of the user associated with the token.
     */
    public static final String CLAIM_USER_FULL_NAME = "fullName";

    /**
     * JWT claim key for user roles.
     * Contains the list of roles specifically assigned to the user.
     */
    public static final String CLAIM_USER_ROLES = "user_roles";

    /**
     * JWT claim key for client ID.
     * Contains the identifier of the client application that requested the token.
     */
    public static final String CLAIM_CLIENT_ID = "client_id";
}
