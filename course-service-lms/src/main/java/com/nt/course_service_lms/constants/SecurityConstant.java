package com.nt.course_service_lms.constants;

/**
 * Security constants used throughout the course service LMS application.
 * Contains HTTP headers, error messages, token types, and JWT claim keys
 * for authentication and authorization processes.
 */
public final class SecurityConstant {

    /**
     * Private constructor to prevent instantiation of this utility class.
     * Throws an {@link UnsupportedOperationException} if attempted.
     */
    private SecurityConstant() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * HTTP header name for gateway timestamp verification.
     */
    public static final String HEADER_X_GATEWAY_TIMESTAMP = "X-Gateway-Timestamp";

    /**
     * HTTP header name for gateway nonce to prevent replay attacks.
     */
    public static final String HEADER_X_GATEWAY_NONCE = "X-Gateway-Nonce";

    /**
     * HTTP header name for service-to-service authentication token.
     */
    public static final String HEADER_X_SERVICE_TOKEN = "X-Service-Token";

    /**
     * HTTP header name for original token type identification.
     */
    public static final String HEADER_X_ORIGINAL_TOKEN_TYPE = "X-Original-Token-Type";

    /**
     * Standard HTTP Authorization header name.
     */
    public static final String HEADER_AUTHORIZATION = "Authorization";

    /**
     * Bearer token prefix used in Authorization header.
     */
    public static final String BEARER_PREFIX = "Bearer ";

    /**
     * JSON error response template for unauthorized access (401).
     */
    public static final String ERROR_UNAUTHORIZED = "{\"error\": \"Unauthorized\", \"message\": \"%s\"}";

    /**
     * JSON error response template for forbidden access (403).
     */
    public static final String ERROR_FORBIDDEN = "{\"error\": \"Forbidden\", \"message\": \"%s\"}";

    /**
     * Token type identifier for service-to-service communication.
     */
    public static final String TOKEN_TYPE_SERVICE = "service";

    /**
     * Maximum allowed time difference in milliseconds for request validation (5 minutes).
     */
    public static final long MAX_REQUEST_TIME_DIFF_MS = 5 * 60 * 1000;

    /**
     * Spring Security role identifier for service accounts.
     */
    public static final String ROLE_SERVICE = "ROLE_SERVICE";

    /**
     * JWT claim key for token type identification.
     */
    public static final String CLAIM_TOKEN_TYPE = "token_type";

    /**
     * JWT claim key for user roles array.
     */
    public static final String CLAIM_ROLES = "roles";

    /**
     * JWT claim key for token scope information.
     */
    public static final String CLAIM_SCOPE = "scope";

    /**
     * JWT claim key for user identifier.
     */
    public static final String CLAIM_USER_ID = "userId";

    /**
     * JWT claim key for user email address.
     */
    public static final String CLAIM_USER_EMAIL = "user_email";

    /**
     * JWT claim key for user's full name.
     */
    public static final String CLAIM_USER_FULL_NAME = "fullName";

    /**
     * JWT claim key for user roles information.
     */
    public static final String CLAIM_USER_ROLES = "user_roles";

    /**
     * JWT claim key for OAuth client identifier.
     */
    public static final String CLAIM_CLIENT_ID = "client_id";
}
