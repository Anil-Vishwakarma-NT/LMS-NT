package com.nt.lms.api_gateway.constant;

public class SecurityConstant {

    // Token Prefix
    public static final String BEARER_PREFIX = "Bearer ";

    // Token Types
    public static final String ACCESS_TOKEN_TYPE = "access";
    public static final String SERVICE_TOKEN_TYPE = "service";
    public static final String REFRESH_TOKEN_TYPE = "refresh";

    // Token Claims
    public static final String CLAIM_SCOPE = "scope";
    public static final String CLAIM_ROLES = "roles";
    public static final String CLAIM_EMAIL = "email";
    public static final String CLAIM_USER_ID = "userId";
    public static final String CLAIM_FULL_NAME = "fullName";
    public static final String CLAIM_CLIENT_ID = "client_id";
    public static final String CLAIM_TOKEN_TYPE = "token_type";
    public static final String CLAIM_USER_ROLES = "user_roles";
    public static final String CLAIM_USER_EMAIL = "user_email";

    // Token Scopes
    public static final String DEFAULT_SCOPE = "read";
    public static final String INTERNAL_SCOPE = "internal.read internal.write";

    // Token Role
    public static final String ROLE_SERVICE = "ROLE_SERVICE";

    // Token Headers
    public static final String SERVICE_TOKEN_HEADER = "X-Service-Token";
    public static final String ORIGINAL_TOKEN_TYPE_HEADER = "X-Original-Token-Type";

    // Gateway Security Headers
    public static final String GATEWAY_SECRET_HEADER = "X-Gateway-Secret";
    public static final String GATEWAY_TIMESTAMP_HEADER = "X-Gateway-Timestamp";
    public static final String GATEWAY_NONCE_HEADER = "X-Gateway-Nonce";
    public static final String GATEWAY_SIGNATURE_HEADER = "X-Gateway-Signature";
    public static final String GATEWAY_SOURCE_HEADER = "X-Gateway-Source";
    public static final String USER_AGENT_HEADER = "User-Agent";

    // Gateway Values
    public static final String GATEWAY_SOURCE_VALUE = "api-gateway";
    public static final String USER_AGENT_HEADER_VALUE = "API-Gateway/1.0";

    // Error Messages
    public static final String INVALID_TOKEN_TYPE_MSG = "Invalid token type";
    public static final String TOKEN_EXPIRED_MSG = "JWT token has expired";
    public static final String INVALID_TOKEN_MSG = "Invalid JWT token";
    public static final String INVALID_ACCESS_TOKEN_MSG = "Invalid access token";
    public static final String USER_NOT_FOUND_MSG = "User not found";
    public static final String TOKEN_PROCESSING_ERROR_MSG = "Token processing error";
    public static final String INVALID_SERVICE_TOKEN_MSG = "Invalid service token";
    public static final String SERVICE_TOKEN_PROCESSING_ERROR_MSG = "Service token processing error";

    // Target Services
    public static final String USER_SERVICE = "user-service";
    public static final String COURSE_SERVICE = "course-service";
    public static final String EMAIL_SERVICE = "email-service";
    public static final String PRODUCT_SERVICE = "product-service";
    public static final String UNKNOWN_SERVICE = "unknown-service";

    // JWT Configuration Defaults (optional override via properties)
    public static final String DEFAULT_JWT_SECRET = "my_secret_key_my_secret_key_my_secret_key";
    public static final long DEFAULT_ACCESS_TOKEN_EXPIRATION = 900000; // 15 min
    public static final long DEFAULT_REFRESH_TOKEN_EXPIRATION = 604800000; // 7 days
    public static final long DEFAULT_SERVICE_TOKEN_EXPIRATION = 3600000; // 1 hour
    public static final String DEFAULT_JWT_ISSUER = "https://auth.nucleusteq.com";

    // Refresh window
    public static final long REFRESH_WINDOW = 24 * 60 * 60 * 1000L; // 1 day

    private SecurityConstant() {
        // Private constructor to prevent instantiation
    }
}
