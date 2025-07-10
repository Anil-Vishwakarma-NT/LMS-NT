package com.nt.user_service_lms.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nt.user_service_lms.constants.TokenConverterConstant.CLIENT_ID;
import static com.nt.user_service_lms.constants.TokenConverterConstant.CONVERSION_CHAIN;
import static com.nt.user_service_lms.constants.TokenConverterConstant.CONVERTED_AT;
import static com.nt.user_service_lms.constants.TokenConverterConstant.CONVERTED_FROM;
import static com.nt.user_service_lms.constants.TokenConverterConstant.DEFAULT_SCOPE;
import static com.nt.user_service_lms.constants.TokenConverterConstant.FULL_NAME;
import static com.nt.user_service_lms.constants.TokenConverterConstant.ORIGINAL_TOKEN_TYPE;
import static com.nt.user_service_lms.constants.TokenConverterConstant.ROLES;
import static com.nt.user_service_lms.constants.TokenConverterConstant.ROLE_SERVICE;
import static com.nt.user_service_lms.constants.TokenConverterConstant.SCOPE;
import static com.nt.user_service_lms.constants.TokenConverterConstant.SERVICE;
import static com.nt.user_service_lms.constants.TokenConverterConstant.SERVICE_TOKEN_SUBJECT;
import static com.nt.user_service_lms.constants.TokenConverterConstant.TOKEN_TYPE;
import static com.nt.user_service_lms.constants.TokenConverterConstant.UNKNOWN;
import static com.nt.user_service_lms.constants.TokenConverterConstant.USER_EMAIL;
import static com.nt.user_service_lms.constants.TokenConverterConstant.USER_ID;
import static com.nt.user_service_lms.constants.TokenConverterConstant.USER_ROLES;

/**
 * Service responsible for converting JWT tokens between different services.
 * This component handles the transformation of service tokens to ensure proper
 * authentication and authorization across microservices within the system.
 *
 * <p>The converter maintains conversion metadata including original token type,
 * conversion timestamps, and conversion chains to track token transformations.</p>
 *
 * @author System
 * @version 1.0
 * @since 1.0
 */
@Component
public class ServiceTokenConverter {

    /**
     * Logger instance for this class to handle logging operations.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceTokenConverter.class);

    /**
     * JWT secret key used for signing and verifying tokens.
     * Default value is provided if not specified in configuration.
     */
    @Value("${jwt.secret}")
    private String SECRET;

    /**
     * JWT issuer identifier that specifies who issued the token.
     * Default value points to the authentication service.
     */
    @Value("${jwt.issuer}")
    private String issuer;

    /**
     * Token expiration time in milliseconds.
     * Default value is 3600000ms (1 hour).
     */
    @Value("${jwt.expiration:3600000}")
    private Long expiration;

    /**
     * Expected audience for the current service, derived from application name.
     * This represents the current service that will be set as the client ID.
     */
    @Value("${spring.application.name}")
    private String expectedAudience;

    /**
     * Utility class for JWT operations including token extraction and validation.
     */
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Creates and returns the signing key used for JWT token signing.
     * The key is generated from the secret string using HMAC SHA algorithm.
     *
     * @return Key object used for JWT signing and verification
     */
    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Converts an original JWT token to a service token for a specific target service.
     * This method extracts claims from the original token and creates a new service token
     * with appropriate audience and conversion metadata.
     *
     * @param originalToken the original JWT token to be converted (must not be null)
     * @param targetService the target service name for which the token is being converted (must not be null)
     * @return newly created service token as a JWT string
     * @throws RuntimeException if token conversion fails due to invalid token or processing errors
     */
    public String convertServiceToken(final String originalToken, final String targetService) {
        try {
            String userId = jwtUtil.extractUserId(originalToken);
            String userEmail = jwtUtil.extractUserEmail(originalToken);
            String userFullName = jwtUtil.extractUserFullName(originalToken);
            List<String> userRoles = jwtUtil.extractUserRoles(originalToken);
            List<String> serviceRoles = jwtUtil.extractRoles(originalToken);
            String scope = jwtUtil.extractScope(originalToken);
            String originalTokenType = jwtUtil.extractTokenType(originalToken);
            String originalClientId = jwtUtil.extractClientId(originalToken);
            Date originalExpiration = jwtUtil.extractExpiration(originalToken);

            Map<String, Object> claims = new HashMap<>();
            claims.put(TOKEN_TYPE, SERVICE);
            claims.put(CLIENT_ID, expectedAudience);
            claims.put(ROLES, serviceRoles != null ? serviceRoles : List.of(ROLE_SERVICE));
            claims.put(SCOPE, scope != null ? scope : DEFAULT_SCOPE);

            if (userId != null) {
                claims.put(USER_ID, userId);
            }
            if (userEmail != null) {
                claims.put(USER_EMAIL, userEmail);
            }
            if (userFullName != null) {
                claims.put(FULL_NAME, userFullName);
            }
            if (userRoles != null) {
                claims.put(USER_ROLES, userRoles);
            }

            claims.put(ORIGINAL_TOKEN_TYPE, originalTokenType);
            claims.put(CONVERTED_FROM, originalClientId);
            claims.put(CONVERTED_AT, System.currentTimeMillis());
            claims.put(CONVERSION_CHAIN, buildConversionChain(originalToken));

            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(SERVICE_TOKEN_SUBJECT)
                    .setAudience(targetService)
                    .setIssuer(issuer)
                    .setIssuedAt(new Date())
                    .setExpiration(originalExpiration)
                    .signWith(getSigningKey())
                    .compact();

        } catch (Exception e) {
            LOGGER.error("Failed to convert service token for target service {}: {}", targetService, e.getMessage());
            throw new RuntimeException("Token conversion failed", e);
        }
    }

    /**
     * Determines whether a token needs conversion for a specific target service.
     * This method compares the current token's audience with the target service
     * to determine if conversion is necessary.
     *
     * @param currentToken the current JWT token to check (must not be null)
     * @param targetService the target service name to check against (must not be null)
     * @return true if token conversion is needed, false otherwise
     */
    public boolean needsTokenConversion(final String currentToken, final String targetService) {
        try {
            String currentAudience = jwtUtil.extractAudience(currentToken);
            return !targetService.equals(currentAudience);
        } catch (Exception e) {
            LOGGER.warn("Failed to check if token conversion is needed: {}", e.getMessage());
            return true;
        }
    }

    /**
     * Builds a conversion chain string that tracks the token's transformation history.
     * The chain shows the path of token conversions from original client to current service.
     *
     * @param originalToken the original JWT token from which to build the chain (must not be null)
     * @return string representation of the conversion chain (e.g., "client1 -> service1 -> service2")
     */
    private String buildConversionChain(final String originalToken) {
        try {
            String existingChain = jwtUtil.extractClaim(originalToken,
                    claims -> (String) claims.get(CONVERSION_CHAIN));

            String originalClientId = jwtUtil.extractClientId(originalToken);

            if (existingChain != null && !existingChain.isEmpty()) {
                return existingChain + " -> " + expectedAudience;
            } else {
                return (originalClientId != null ? originalClientId : UNKNOWN) + " -> " + expectedAudience;
            }
        } catch (Exception e) {
            LOGGER.debug("Could not build conversion chain: {}", e.getMessage());
            return UNKNOWN + " -> " + expectedAudience;
        }
    }

    /**
     * Validates whether a given token can be converted by this service.
     * Checks if the token is a service token and is valid according to JWT standards.
     *
     * @param token the JWT token to validate for conversion (must not be null)
     * @return true if the token can be converted, false otherwise
     */
    public boolean canConvertToken(final String token) {
        try {
            String tokenType = jwtUtil.extractTokenType(token);
            return SERVICE.equals(tokenType) && jwtUtil.validateToken(token);
        } catch (Exception e) {
            LOGGER.warn("Cannot convert token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extracts conversion metadata from a converted token.
     * This method retrieves information about the token's conversion history
     * including original token type, source, timestamp, and conversion chain.
     *
     * @param token the JWT token from which to extract metadata (must not be null)
     * @return Map containing conversion metadata with keys: ORIGINAL_TOKEN_TYPE,
     *         CONVERTED_FROM, CONVERTED_AT, CONVERSION_CHAIN
     */
    public Map<String, Object> getConversionMetadata(final String token) {
        Map<String, Object> metadata = new HashMap<>();
        try {
            metadata.put(ORIGINAL_TOKEN_TYPE, jwtUtil.extractClaim(token, claims -> claims.get(ORIGINAL_TOKEN_TYPE)));
            metadata.put(CONVERTED_FROM, jwtUtil.extractClaim(token, claims -> claims.get(CONVERTED_FROM)));
            metadata.put(CONVERTED_AT, jwtUtil.extractClaim(token, claims -> claims.get(CONVERTED_AT)));
            metadata.put(CONVERSION_CHAIN, jwtUtil.extractClaim(token, claims -> claims.get(CONVERSION_CHAIN)));
        } catch (Exception e) {
            LOGGER.debug("Could not extract conversion metadata: {}", e.getMessage());
        }
        return metadata;
    }
}
