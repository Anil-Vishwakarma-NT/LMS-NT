package com.nt.lms.api_gateway_lms.constant;

/**
 * Security constants used throughout the API Gateway LMS application.
 * This class contains all security-related constants including token types,
 * claims, headers, error messages, and JWT configuration defaults.
 */
public final class SecurityConstant {

    /**
     * Private constructor to prevent instantiation of this utility class.
     * Throws an {@link UnsupportedOperationException} if attempted.
     */
    private SecurityConstant() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // ========================== Token Prefix ==========================

    /**
     * Bearer token prefix used in Authorization headers.
     * Standard OAuth 2.0 bearer token prefix.
     */
    public static final String BEARER_PREFIX = "Bearer ";

    // ========================== Token Types ==========================

    /**
     * Access token type identifier.
     * Used to identify tokens for user authentication and authorization.
     */
    public static final String ACCESS_TOKEN_TYPE = "access";

    /**
     * Service token type identifier.
     * Used to identify tokens for service-to-service communication.
     */
    public static final String SERVICE_TOKEN_TYPE = "service";

    /**
     * Refresh token type identifier.
     * Used to identify tokens for obtaining new access tokens.
     */
    public static final String REFRESH_TOKEN_TYPE = "refresh";

    // ========================== Token Claims ==========================

    /**
     * JWT claim key for token scope.
     * Defines the permissions and access level of the token.
     */
    public static final String CLAIM_SCOPE = "scope";

    /**
     * JWT claim key for user roles.
     * Contains the roles assigned to the authenticated user.
     */
    public static final String CLAIM_ROLES = "roles";

    /**
     * JWT claim key for user email address.
     * Contains the email address of the authenticated user.
     */
    public static final String CLAIM_EMAIL = "email";

    /**
     * JWT claim key for user ID.
     * Contains the unique identifier of the authenticated user.
     */
    public static final String CLAIM_USER_ID = "userId";

    /**
     * JWT claim key for user's full name.
     * Contains the complete name of the authenticated user.
     */
    public static final String CLAIM_FULL_NAME = "fullName";

    /**
     * JWT claim key for client ID.
     * Identifies the client application making the request.
     */
    public static final String CLAIM_CLIENT_ID = "client_id";

    /**
     * JWT claim key for token type.
     * Specifies whether the token is access, service, or refresh type.
     */
    public static final String CLAIM_TOKEN_TYPE = "token_type";

    /**
     * JWT claim key for user roles (alternative format).
     * Alternative claim name for user role information.
     */
    public static final String CLAIM_USER_ROLES = "user_roles";

    /**
     * JWT claim key for user email (alternative format).
     * Alternative claim name for user email information.
     */
    public static final String CLAIM_USER_EMAIL = "user_email";

    // ========================== Token Scopes ==========================

    /**
     * Default scope for standard user tokens.
     * Provides basic read access permissions.
     */
    public static final String DEFAULT_SCOPE = "read";

    /**
     * Internal scope for service-to-service communication.
     * Provides both read and write access for internal services.
     */
    public static final String INTERNAL_SCOPE = "internal.read internal.write";

    // ========================== Token Role ==========================

    /**
     * Service role identifier.
     * Role assigned to service tokens for inter-service communication.
     */
    public static final String ROLE_SERVICE = "ROLE_SERVICE";

    // ========================== Token Headers ==========================

    /**
     * HTTP header name for service tokens.
     * Custom header used to pass service tokens between microservices.
     */
    public static final String SERVICE_TOKEN_HEADER = "X-Service-Token";

    /**
     * HTTP header name for original token type.
     * Used to track the original token type during token transformations.
     */
    public static final String ORIGINAL_TOKEN_TYPE_HEADER = "X-Original-Token-Type";

    // ========================== Gateway Security Headers ==========================

    /**
     * HTTP header name for gateway secret.
     * Contains the secret key for gateway authentication.
     */
    public static final String GATEWAY_SECRET_HEADER = "X-Gateway-Secret";

    /**
     * HTTP header name for gateway timestamp.
     * Contains the timestamp when the request was made through the gateway.
     */
    public static final String GATEWAY_TIMESTAMP_HEADER = "X-Gateway-Timestamp";

    /**
     * HTTP header name for gateway nonce.
     * Contains a unique value to prevent replay attacks.
     */
    public static final String GATEWAY_NONCE_HEADER = "X-Gateway-Nonce";

    /**
     * HTTP header name for gateway signature.
     * Contains the cryptographic signature for request validation.
     */
    public static final String GATEWAY_SIGNATURE_HEADER = "X-Gateway-Signature";

    /**
     * HTTP header name for gateway source identifier.
     * Identifies the source of the request (API Gateway).
     */
    public static final String GATEWAY_SOURCE_HEADER = "X-Gateway-Source";

    /**
     * HTTP header name for User-Agent.
     * Standard HTTP header for client identification.
     */
    public static final String USER_AGENT_HEADER = "User-Agent";

    // ========================== Gateway Values ==========================

    /**
     * Value for gateway source header.
     * Identifies requests as coming from the API Gateway.
     */
    public static final String GATEWAY_SOURCE_VALUE = "api-gateway";

    /**
     * Value for User-Agent header when making requests through the gateway.
     * Standard User-Agent string for API Gateway requests.
     */
    public static final String USER_AGENT_HEADER_VALUE = "API-Gateway/1.0";

    // ========================== Error Messages ==========================

    /**
     * Error message for invalid token type.
     * Used when a token type is not recognized or supported.
     */
    public static final String INVALID_TOKEN_TYPE_MSG = "Invalid token type";

    /**
     * Error message for expired JWT tokens.
     * Used when a token has passed its expiration time.
     */
    public static final String TOKEN_EXPIRED_MSG = "JWT token has expired";

    /**
     * Error message for invalid JWT tokens.
     * Used when a token is malformed, corrupted, or invalid.
     */
    public static final String INVALID_TOKEN_MSG = "Invalid JWT token";

    /**
     * Error message for invalid access tokens.
     * Used specifically for access token validation failures.
     */
    public static final String INVALID_ACCESS_TOKEN_MSG = "Invalid access token";

    /**
     * Error message when user is not found.
     * Used when user lookup fails during authentication.
     */
    public static final String USER_NOT_FOUND_MSG = "User not found";

    /**
     * Error message for general token processing errors.
     * Used for generic token processing failures.
     */
    public static final String TOKEN_PROCESSING_ERROR_MSG = "Token processing error";

    /**
     * Error message for invalid service tokens.
     * Used specifically for service token validation failures.
     */
    public static final String INVALID_SERVICE_TOKEN_MSG = "Invalid service token";

    /**
     * Error message for service token processing errors.
     * Used for errors during service token processing.
     */
    public static final String SERVICE_TOKEN_PROCESSING_ERROR_MSG = "Service token processing error";

    // ========================== Target Services ==========================

    /**
     * Identifier for the user management service.
     * Used for routing requests to the user service.
     */
    public static final String USER_SERVICE = "user-service";

    /**
     * Identifier for the course management service.
     * Used for routing requests to the course service.
     */
    public static final String COURSE_SERVICE = "course-service";

    /**
     * Identifier for the email service.
     * Used for routing requests to the email service.
     */
    public static final String EMAIL_SERVICE = "email-service";

    /**
     * Identifier for the product management service.
     * Used for routing requests to the product service.
     */
    public static final String PRODUCT_SERVICE = "product-service";

    /**
     * Identifier for unknown or unrecognized services.
     * Used as a fallback when service cannot be identified.
     */
    public static final String UNKNOWN_SERVICE = "unknown-service";

    // ========================== JWT Configuration Defaults ==========================

    /**
     * Default JWT secret key for token signing and validation.
     * This should be overridden in production with a secure, randomly generated key.
     * Note: This default key should not be used in production environments.
     */
    public static final String DEFAULT_JWT_SECRET = "my_secret_key_my_secret_key_my_secret_key";

    /**
     * Default expiration time for access tokens in milliseconds.
     * Set to 15 minutes (900,000 milliseconds) for security.
     */
    public static final long DEFAULT_ACCESS_TOKEN_EXPIRATION = 900000; // 15 min

    /**
     * Default expiration time for refresh tokens in milliseconds.
     * Set to 7 days (604,800,000 milliseconds) for user convenience.
     */
    public static final long DEFAULT_REFRESH_TOKEN_EXPIRATION = 604800000; // 7 days

    /**
     * Default expiration time for service tokens in milliseconds.
     * Set to 1 hour (3,600,000 milliseconds) for service-to-service communication.
     */
    public static final long DEFAULT_SERVICE_TOKEN_EXPIRATION = 3600000; // 1 hour

    /**
     * Default JWT issuer identifier.
     * Identifies the authentication service that issued the token.
     */
    public static final String DEFAULT_JWT_ISSUER = "https://auth.nucleusteq.com";

    // ========================== Refresh Window ==========================

    /**
     * Time window for token refresh operations in milliseconds.
     * Set to 24 hours (86,400,000 milliseconds).
     * Tokens can be refreshed within this window before expiration.
     */
    public static final long REFRESH_WINDOW = 24 * 60 * 60 * 1000L; // 1 day

}
