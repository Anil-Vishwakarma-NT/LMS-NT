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

import static com.nt.user_service_lms.constants.TokenConverterConstant.*;

@Component
public class ServiceTokenConverter {

    private static final Logger logger = LoggerFactory.getLogger(ServiceTokenConverter.class);

    @Value("${jwt.secret:my_secret_key_my_secret_key_my_secret_key}")
    private String SECRET;

    @Value("${jwt.issuer:https://auth.nucleusteq.com}")
    private String issuer;

    @Value("${jwt.expiration:3600000}")
    private Long expiration;

    @Value("${spring.application.name}")
    private String expectedAudience;

    @Autowired
    private JwtUtil jwtUtil;

    private Key getSigningKey() {
        byte[] keyBytes = SECRET.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String convertServiceToken(String originalToken, String targetService) {
        try {
            String userId = jwtUtil.extractUserId(originalToken);
            String userEmail = jwtUtil.extractUserEmail(originalToken);
            String userFullName = jwtUtil.extractUserFullName(originalToken);
            List<String> userRoles = jwtUtil.extractUserRoles(originalToken);
            List<String> serviceRoles = jwtUtil.extractRoles(originalToken);
            String scope = jwtUtil.extractScope(originalToken);
            String originalTokenType = jwtUtil.extractTokenType(originalToken);
            String originalClientId = jwtUtil.extractClientId(originalToken);

            Map<String, Object> claims = new HashMap<>();
            claims.put(TOKEN_TYPE, SERVICE);
            claims.put(CLIENT_ID, expectedAudience);
            claims.put(ROLES, serviceRoles != null ? serviceRoles : List.of(ROLE_SERVICE));
            claims.put(SCOPE, scope != null ? scope : DEFAULT_SCOPE);

            if (userId != null) claims.put(USER_ID, userId);
            if (userEmail != null) claims.put(USER_EMAIL, userEmail);
            if (userFullName != null) claims.put(FULL_NAME, userFullName);
            if (userRoles != null) claims.put(USER_ROLES, userRoles);

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
                    .setExpiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(getSigningKey())
                    .compact();

        } catch (Exception e) {
            logger.error("Failed to convert service token for target service {}: {}", targetService, e.getMessage());
            throw new RuntimeException("Token conversion failed", e);
        }
    }

    public boolean needsTokenConversion(String currentToken, String targetService) {
        try {
            String currentAudience = jwtUtil.extractAudience(currentToken);
            return !targetService.equals(currentAudience);
        } catch (Exception e) {
            logger.warn("Failed to check if token conversion is needed: {}", e.getMessage());
            return true;
        }
    }

    private String buildConversionChain(String originalToken) {
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
            logger.debug("Could not build conversion chain: {}", e.getMessage());
            return UNKNOWN + " -> " + expectedAudience;
        }
    }

    public boolean canConvertToken(String token) {
        try {
            String tokenType = jwtUtil.extractTokenType(token);
            return SERVICE.equals(tokenType) && jwtUtil.validateToken(token);
        } catch (Exception e) {
            logger.warn("Cannot convert token: {}", e.getMessage());
            return false;
        }
    }

    public Map<String, Object> getConversionMetadata(String token) {
        Map<String, Object> metadata = new HashMap<>();
        try {
            metadata.put(ORIGINAL_TOKEN_TYPE, jwtUtil.extractClaim(token, claims -> claims.get(ORIGINAL_TOKEN_TYPE)));
            metadata.put(CONVERTED_FROM, jwtUtil.extractClaim(token, claims -> claims.get(CONVERTED_FROM)));
            metadata.put(CONVERTED_AT, jwtUtil.extractClaim(token, claims -> claims.get(CONVERTED_AT)));
            metadata.put(CONVERSION_CHAIN, jwtUtil.extractClaim(token, claims -> claims.get(CONVERSION_CHAIN)));
        } catch (Exception e) {
            logger.debug("Could not extract conversion metadata: {}", e.getMessage());
        }
        return metadata;
    }
}
