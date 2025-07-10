package com.nt.user_service_lms.constants;

/**
 * Constants used for token conversion operations in the LMS user service.
 * This class contains HTTP headers, JWT token claims, and default values
 * used throughout the token conversion process.
 */
public final class TokenConverterConstant {

    // Feign Headers
    /** Header for passing service tokens in inter-service communication. */
    public static final String HEADER_X_SERVICE_TOKEN = "X-Service-Token";

    /** Header indicating the original token type before conversion. */
    public static final String HEADER_X_ORIGINAL_TOKEN_TYPE = "X-Original-Token-Type";

    /** Header containing the user ID for request context. */
    public static final String HEADER_X_USER_ID = "X-User-ID";

    /** Header containing the user email for request context. */
    public static final String HEADER_X_USER_EMAIL = "X-User-Email";

    /** Header indicating which service is making the request. */
    public static final String HEADER_X_SOURCE_SERVICE = "X-Source-Service";

    /** Header indicating the source of the request (web, mobile, api, etc.). */
    public static final String HEADER_X_REQUEST_SOURCE = "X-Request-Source";

    /** Header containing the timestamp when the request was initiated. */
    public static final String HEADER_X_REQUEST_TIMESTAMP = "X-Request-Timestamp";

    // Gateway Validation Headers
    /** Header containing the gateway timestamp for request validation. */
    public static final String HEADER_X_GATEWAY_TIMESTAMP = "X-Gateway-Timestamp";

    /** Header containing a unique nonce for gateway request validation. */
    public static final String HEADER_X_GATEWAY_NONCE = "X-Gateway-Nonce";

    // Token Claims
    /** JWT claim key for token type identification. */
    public static final String CLAIM_TOKEN_TYPE = "token_type";

    /** JWT claim key for OAuth2 client identifier. */
    public static final String CLAIM_CLIENT_ID = "client_id";

    /** JWT claim key for user roles array. */
    public static final String CLAIM_ROLES = "roles";

    /** JWT claim key for OAuth2 scope definition. */
    public static final String CLAIM_SCOPE = "scope";

    /** JWT claim key for user identifier. */
    public static final String CLAIM_USER_ID = "user_id";

    /** JWT claim key for user email address. */
    public static final String CLAIM_USER_EMAIL = "user_email";

    /** JWT claim key for user's full name. */
    public static final String CLAIM_USER_FULLNAME = "fullName";

    /** JWT claim key for user-specific roles. */
    public static final String CLAIM_USER_ROLES = "user_roles";

    /** JWT claim key for the original token type before conversion. */
    public static final String CLAIM_ORIGINAL_TOKEN_TYPE = "original_token_type";

    /** JWT claim key indicating what type of token this was converted from. */
    public static final String CLAIM_CONVERTED_FROM = "converted_from";

    /** JWT claim key for the timestamp when token conversion occurred. */
    public static final String CLAIM_CONVERTED_AT = "converted_at";

    /** JWT claim key for the chain of token conversions. */
    public static final String CLAIM_CONVERSION_CHAIN = "conversion_chain";

    // Token Defaults
    /** Default role assigned to service tokens. */
    public static final String DEFAULT_SERVICE_ROLE = "ROLE_SERVICE";

    /** Default OAuth2 scope for internal service communication. */
    public static final String DEFAULT_SCOPE = "internal.read internal.write";

    /** Default subject for service tokens. */
    public static final String SUBJECT_SERVICE_TOKEN = "service-token";

    // Token Type
    /** Token type identifier for service tokens. */
    public static final String SERVICE_TOKEN_TYPE = "service";

    // Service Name
    /** Default source service name for token operations. */
    public static final String DEFAULT_SOURCE_SERVICE = "order-service";

    /** Token type claim key (duplicate of CLAIM_TOKEN_TYPE). */
    public static final String TOKEN_TYPE = "token_type";

    /** Service token type value. */
    public static final String SERVICE = "service";

    /** Client ID claim key (duplicate of CLAIM_CLIENT_ID). */
    public static final String CLIENT_ID = "client_id";

    /** Roles claim key (duplicate of CLAIM_ROLES). */
    public static final String ROLES = "roles";

    /** Service role value. */
    public static final String ROLE_SERVICE = "ROLE_SERVICE";

    /** Scope claim key (duplicate of CLAIM_SCOPE). */
    public static final String SCOPE = "scope";

    /** User ID claim key (duplicate of CLAIM_USER_ID). */
    public static final String USER_ID = "user_id";

    /** User email claim key (duplicate of CLAIM_USER_EMAIL). */
    public static final String USER_EMAIL = "user_email";

    /** Full name claim key (duplicate of CLAIM_USER_FULLNAME). */
    public static final String FULL_NAME = "fullName";

    /** User roles claim key (duplicate of CLAIM_USER_ROLES). */
    public static final String USER_ROLES = "user_roles";

    /** Original token type claim key (duplicate of CLAIM_ORIGINAL_TOKEN_TYPE). */
    public static final String ORIGINAL_TOKEN_TYPE = "original_token_type";

    /** Converted from claim key (duplicate of CLAIM_CONVERTED_FROM). */
    public static final String CONVERTED_FROM = "converted_from";

    /** Converted at claim key (duplicate of CLAIM_CONVERTED_AT). */
    public static final String CONVERTED_AT = "converted_at";

    /** Conversion chain claim key (duplicate of CLAIM_CONVERSION_CHAIN). */
    public static final String CONVERSION_CHAIN = "conversion_chain";

    /** Service token subject value (duplicate of SUBJECT_SERVICE_TOKEN). */
    public static final String SERVICE_TOKEN_SUBJECT = "service-token";

    /** Default value for unknown or undefined fields. */
    public static final String UNKNOWN = "unknown";

    /**
     * Private constructor to prevent instantiation of this utility class.
     * This class should only be used to access static constants.
     */
    private TokenConverterConstant() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
