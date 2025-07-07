package com.example.course_service_lms.constants;

public class SecurityConstant {

    // Gateway headers
    public static final String HEADER_X_GATEWAY_TIMESTAMP = "X-Gateway-Timestamp";
    public static final String HEADER_X_GATEWAY_NONCE = "X-Gateway-Nonce";
    public static final String HEADER_X_SERVICE_TOKEN = "X-Service-Token";
    public static final String HEADER_X_ORIGINAL_TOKEN_TYPE = "X-Original-Token-Type";
//    public static final String SERVICE_TOKEN_HEADER = "X-Service-Token";

    // Authorization
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    // Error responses
    public static final String ERROR_UNAUTHORIZED = "{\"error\": \"Unauthorized\", \"message\": \"%s\"}";
    public static final String ERROR_FORBIDDEN = "{\"error\": \"Forbidden\", \"message\": \"%s\"}";

    // Token type
    public static final String TOKEN_TYPE_SERVICE = "service";

    // Time constants
    public static final long MAX_REQUEST_TIME_DIFF_MS = 5 * 60 * 1000; // 5 minutes

    public static final String ROLE_SERVICE = "ROLE_SERVICE";

    // Claim Keys
    public static final String CLAIM_TOKEN_TYPE = "token_type";
    public static final String CLAIM_ROLES = "roles";
    public static final String CLAIM_SCOPE = "scope";
    public static final String CLAIM_USER_ID = "userId";
    public static final String CLAIM_USER_EMAIL = "user_email";
    public static final String CLAIM_USER_FULL_NAME = "fullName";
    public static final String CLAIM_USER_ROLES = "user_roles";
    public static final String CLAIM_CLIENT_ID = "client_id";


}

