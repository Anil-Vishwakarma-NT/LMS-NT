package com.nt.user_service_lms.constants;

public class TokenConverterConstant {
    // Feign Headers
    public static final String HEADER_X_SERVICE_TOKEN = "X-Service-Token";
    public static final String HEADER_X_ORIGINAL_TOKEN_TYPE = "X-Original-Token-Type";
    public static final String HEADER_X_USER_ID = "X-User-ID";
    public static final String HEADER_X_USER_EMAIL = "X-User-Email";
    public static final String HEADER_X_SOURCE_SERVICE = "X-Source-Service";
    public static final String HEADER_X_REQUEST_SOURCE = "X-Request-Source";
    public static final String HEADER_X_REQUEST_TIMESTAMP = "X-Request-Timestamp";

    // Gateway Validation Headers
    public static final String HEADER_X_GATEWAY_TIMESTAMP = "X-Gateway-Timestamp";
    public static final String HEADER_X_GATEWAY_NONCE = "X-Gateway-Nonce";

    // Token Claims
    public static final String CLAIM_TOKEN_TYPE = "token_type";
    public static final String CLAIM_CLIENT_ID = "client_id";
    public static final String CLAIM_ROLES = "roles";
    public static final String CLAIM_SCOPE = "scope";
    public static final String CLAIM_USER_ID = "user_id";
    public static final String CLAIM_USER_EMAIL = "user_email";
    public static final String CLAIM_USER_FULLNAME = "fullName";
    public static final String CLAIM_USER_ROLES = "user_roles";
    public static final String CLAIM_ORIGINAL_TOKEN_TYPE = "original_token_type";
    public static final String CLAIM_CONVERTED_FROM = "converted_from";
    public static final String CLAIM_CONVERTED_AT = "converted_at";
    public static final String CLAIM_CONVERSION_CHAIN = "conversion_chain";

    // Token Defaults
    public static final String DEFAULT_SERVICE_ROLE = "ROLE_SERVICE";
    public static final String DEFAULT_SCOPE = "internal.read internal.write";
    public static final String SUBJECT_SERVICE_TOKEN = "service-token";

    // Token Type
    public static final String SERVICE_TOKEN_TYPE = "service";

    // Service Name
    public static final String DEFAULT_SOURCE_SERVICE = "order-service";

    public static final String TOKEN_TYPE = "token_type";
    public static final String SERVICE = "service";
    public static final String CLIENT_ID = "client_id";
    public static final String ROLES = "roles";
    public static final String ROLE_SERVICE = "ROLE_SERVICE";
    public static final String SCOPE = "scope";
    public static final String USER_ID = "user_id";
    public static final String USER_EMAIL = "user_email";
    public static final String FULL_NAME = "fullName";
    public static final String USER_ROLES = "user_roles";

    public static final String ORIGINAL_TOKEN_TYPE = "original_token_type";
    public static final String CONVERTED_FROM = "converted_from";
    public static final String CONVERTED_AT = "converted_at";
    public static final String CONVERSION_CHAIN = "conversion_chain";

    public static final String SERVICE_TOKEN_SUBJECT = "service-token";
    public static final String UNKNOWN = "unknown";
}
