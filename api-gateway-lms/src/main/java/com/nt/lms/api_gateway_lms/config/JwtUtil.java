// JwtUtil.java
package com.nt.lms.api_gateway_lms.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.nt.lms.api_gateway_lms.constant.SecurityConstant.*;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret:" + DEFAULT_JWT_SECRET + "}")
    private String SECRET;

    @Value("${jwt.access-token.expiration:" + DEFAULT_ACCESS_TOKEN_EXPIRATION + "}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration:" + DEFAULT_REFRESH_TOKEN_EXPIRATION + "}")
    private Long refreshTokenExpiration;

    @Value("${jwt.service-token.expiration:" + DEFAULT_SERVICE_TOKEN_EXPIRATION + "}")
    private Long serviceTokenExpiration;

    @Value("${jwt.issuer:" + DEFAULT_JWT_ISSUER + "}")
    private String issuer;

    private Key getSigningKey() {
        byte[] keyBytes = SECRET.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(UserDetails userDetails, String userId, String email, String name) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_SCOPE, DEFAULT_SCOPE);
        claims.put(CLAIM_ROLES, userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        claims.put(CLAIM_EMAIL, email);
        claims.put(CLAIM_USER_ID, userId);
        claims.put(CLAIM_FULL_NAME, name);
        claims.put(CLAIM_TOKEN_TYPE, ACCESS_TOKEN_TYPE);

        return createToken(userId, GATEWAY_SOURCE_VALUE, claims, accessTokenExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails, String userId, String email, String name) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_SCOPE, DEFAULT_SCOPE);
        claims.put(CLAIM_ROLES, userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));
        claims.put(CLAIM_EMAIL, email);
        claims.put(CLAIM_USER_ID, userId);
        claims.put(CLAIM_FULL_NAME, name);
        claims.put(CLAIM_TOKEN_TYPE, REFRESH_TOKEN_TYPE);

        return createToken(userId, GATEWAY_SOURCE_VALUE, claims, refreshTokenExpiration);
    }

    public String generateServiceToken(String sourceService, String targetService, List<String> scopes) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_SCOPE, String.join(" ", scopes));
        claims.put(CLAIM_ROLES, List.of(ROLE_SERVICE));
        claims.put(CLAIM_CLIENT_ID, sourceService);
        claims.put(CLAIM_TOKEN_TYPE, SERVICE_TOKEN_TYPE);

        return createToken(sourceService, targetService, claims, serviceTokenExpiration);
    }

    public String convertToServiceToken(String accessToken, String targetService) {
        try {
            Claims claims = extractAllClaims(accessToken);
            String tokenType = (String) claims.get(CLAIM_TOKEN_TYPE);

            if (!ACCESS_TOKEN_TYPE.equals(tokenType)) {
                throw new IllegalArgumentException(INVALID_TOKEN_TYPE_MSG);
            }

            String userId = claims.getSubject();
            String fullName = (String) claims.get(CLAIM_FULL_NAME);
            List<String> userRoles = (List<String>) claims.get(CLAIM_ROLES);
            String email = (String) claims.get(CLAIM_EMAIL);

            Map<String, Object> serviceClaims = new HashMap<>();
            serviceClaims.put(CLAIM_SCOPE, INTERNAL_SCOPE);
            serviceClaims.put(CLAIM_ROLES, List.of(ROLE_SERVICE));
            serviceClaims.put(CLAIM_CLIENT_ID, GATEWAY_SOURCE_VALUE);
            serviceClaims.put(CLAIM_TOKEN_TYPE, SERVICE_TOKEN_TYPE);
            serviceClaims.put(CLAIM_USER_ID, userId);
            serviceClaims.put(CLAIM_USER_ROLES, userRoles);
            serviceClaims.put(CLAIM_USER_EMAIL, email);
            serviceClaims.put(CLAIM_FULL_NAME, fullName);

            return createToken(GATEWAY_SOURCE_VALUE, targetService, serviceClaims, serviceTokenExpiration);
        } catch (Exception e) {
            log.error(SERVICE_TOKEN_PROCESSING_ERROR_MSG + ": {}", e.getMessage());
            throw new RuntimeException("Cannot convert token", e);
        }
    }

    public String generateServiceTokenForDirectAccess(String accessToken, String targetService) {
        try {
            Claims claims = extractAllClaims(accessToken);
            String tokenType = (String) claims.get(CLAIM_TOKEN_TYPE);

            if (!ACCESS_TOKEN_TYPE.equals(tokenType)) {
                throw new IllegalArgumentException(INVALID_TOKEN_TYPE_MSG);
            }

            String userId = claims.getSubject();
            String fullName = (String) claims.get(CLAIM_FULL_NAME);
            List<String> userRoles = (List<String>) claims.get(CLAIM_ROLES);
            String email = (String) claims.get(CLAIM_EMAIL);

            Map<String, Object> serviceClaims = new HashMap<>();
            serviceClaims.put(CLAIM_SCOPE, INTERNAL_SCOPE);
            serviceClaims.put(CLAIM_ROLES, List.of(ROLE_SERVICE));
            serviceClaims.put(CLAIM_CLIENT_ID, "NA");
            serviceClaims.put(CLAIM_TOKEN_TYPE, SERVICE_TOKEN_TYPE);
            serviceClaims.put(CLAIM_USER_ID, userId);
            serviceClaims.put(CLAIM_USER_ROLES, userRoles);
            serviceClaims.put(CLAIM_USER_EMAIL, email);
            serviceClaims.put(CLAIM_FULL_NAME, fullName);

            return createToken("null", targetService, serviceClaims, serviceTokenExpiration);
        } catch (Exception e) {
            log.error(SERVICE_TOKEN_PROCESSING_ERROR_MSG + ": {}", e.getMessage());
            throw new RuntimeException("Cannot convert token", e);
        }
    }


    private String createToken(String sub, String audience, Map<String, Object> claims, Long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(sub)
                .setIssuer(issuer)
                .setAudience(audience)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Boolean isTokenExpired(String token) {
        try {
            final Date expiration = extractExpiration(token);
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            log.debug(TOKEN_EXPIRED_MSG + ": {}", e.getMessage());
            return true;
        }
    }

    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractAudience(String token) {
        return extractClaim(token, Claims::getAudience);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractTokenType(String token) {
        return extractClaim(token, claims -> (String) claims.get(CLAIM_TOKEN_TYPE));
    }

    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> (List<String>) claims.get(CLAIM_ROLES));
    }

    public String extractEmail(String token) {
        return extractClaim(token, claims -> (String) claims.get(CLAIM_EMAIL));
    }

    public String extractScope(String token) {
        return extractClaim(token, claims -> (String) claims.get(CLAIM_SCOPE));
    }

    public Boolean validateAccessToken(String token, String username) {
        try {
            final String tokenEmail = extractEmail(token);
            final String tokenType = extractTokenType(token);
            return (tokenEmail.equals(username) &&
                    ACCESS_TOKEN_TYPE.equals(tokenType) &&
                    !isTokenExpired(token));
        } catch (Exception e) {
            log.debug(INVALID_ACCESS_TOKEN_MSG + ": {}", e.getMessage());
            return false;
        }
    }

    public Boolean validateRefreshToken(String token, String userId) {
        try {
            final String tokenSubject = extractSubject(token);
            final String tokenType = extractTokenType(token);
            return (tokenSubject.equals(userId) &&
                    REFRESH_TOKEN_TYPE.equals(tokenType) &&
                    !isTokenExpired(token));
        } catch (Exception e) {
            log.debug(INVALID_TOKEN_MSG + ": {}", e.getMessage());
            return false;
        }
    }

    public Boolean validateServiceToken(String token, String expectedAudience) {
        try {
            final String tokenAudience = extractAudience(token);
            final String tokenType = extractTokenType(token);
            final List<String> roles = extractRoles(token);

            return (SERVICE_TOKEN_TYPE.equals(tokenType) &&
                    (expectedAudience == null || expectedAudience.equals(tokenAudience)) &&
                    roles.contains(ROLE_SERVICE) &&
                    !isTokenExpired(token));
        } catch (Exception e) {
            log.debug(INVALID_SERVICE_TOKEN_MSG + ": {}", e.getMessage());
            return false;
        }
    }

    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error(TOKEN_PROCESSING_ERROR_MSG + ": {}", e.getMessage());
            return false;
        }
    }

    public String refreshAccessToken(String refreshToken, UserDetails userDetails, String userId, String email, String name) {
        if (!validateRefreshToken(refreshToken, userId)) {
            throw new RuntimeException("Invalid refresh token");
        }
        return generateAccessToken(userDetails, userId, email, name);
    }

    public Map<String, Object> introspectToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Map<String, Object> tokenInfo = new HashMap<>();
            tokenInfo.put("subject", claims.getSubject());
            tokenInfo.put("audience", claims.getAudience());
            tokenInfo.put("issuer", claims.getIssuer());
            tokenInfo.put("issuedAt", claims.getIssuedAt());
            tokenInfo.put("expiration", claims.getExpiration());
            tokenInfo.put("tokenType", claims.get(CLAIM_TOKEN_TYPE));
            tokenInfo.put("roles", claims.get(CLAIM_ROLES));
            tokenInfo.put("scope", claims.get(CLAIM_SCOPE));
            tokenInfo.put("email", claims.get(CLAIM_EMAIL));
            tokenInfo.put("expired", isTokenExpired(token));
            return tokenInfo;
        } catch (Exception e) {
            log.error("Token introspection failed: {}", e.getMessage());
            throw new RuntimeException("Cannot introspect token", e);
        }
    }

    public Long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    public Long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

    public Long getServiceTokenExpiration() {
        return serviceTokenExpiration;
    }

    public Boolean canTokenBeRefreshed(String token) {
        try {
            final Date expiration = extractExpiration(token);
            final Date now = new Date();
            return now.getTime() - expiration.getTime() < REFRESH_WINDOW;
        } catch (Exception e) {
            log.debug("Cannot refresh token: {}", e.getMessage());
            return false;
        }
    }
}
