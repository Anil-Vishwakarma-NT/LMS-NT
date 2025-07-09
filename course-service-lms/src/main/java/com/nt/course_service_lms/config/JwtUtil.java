package com.nt.course_service_lms.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import static com.nt.course_service_lms.constants.SecurityConstant.*;


@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String SECRET;

    @Value("${jwt.issuer}")
    private String issuer;

    private Key getSigningKey() {
        byte[] keyBytes = SECRET.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractAudience(String token) {
        return extractClaim(token, Claims::getAudience);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractTokenType(String token) {
        return extractClaim(token, claims -> (String) claims.get(CLAIM_TOKEN_TYPE));
    }

    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> (List<String>) claims.get(CLAIM_ROLES));
    }

    public String extractScope(String token) {
        return extractClaim(token, claims -> (String) claims.get(CLAIM_SCOPE));
    }

    public String extractUserId(String token) {
        return extractClaim(token, claims -> (String) claims.get(CLAIM_USER_ID));
    }

    public String extractUserEmail(String token) {
        return extractClaim(token, claims -> (String) claims.get(CLAIM_USER_EMAIL));
    }

    public String extractUserFullName(String token) {
        return extractClaim(token, claims -> (String) claims.get(CLAIM_USER_FULL_NAME));
    }

    public List<String> extractUserRoles(String token) {
        return extractClaim(token, claims -> (List<String>) claims.get(CLAIM_USER_ROLES));
    }

    public String extractClientId(String token) {
        return extractClaim(token, claims -> (String) claims.get(CLAIM_CLIENT_ID));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Boolean isTokenExpired(String token) {
        try {
            final Date expiration = extractExpiration(token);
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            logger.debug("Token is expired: {}", e.getMessage());
            return true;
        }
    }

    public Boolean validateServiceToken(String token, String expectedAudience) {
        try {
            final String tokenAudience = extractAudience(token);
            final String tokenType = extractTokenType(token);
            final List<String> roles = extractRoles(token);

            boolean isValidAudience = expectedAudience == null ||
                    expectedAudience.equals(tokenAudience);

            return (TOKEN_TYPE_SERVICE.equals(tokenType) &&
                    isValidAudience &&
                    roles.contains(ROLE_SERVICE) &&
                    !isTokenExpired(token));
        } catch (Exception e) {
            logger.debug("Service token validation failed: {}", e.getMessage());
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
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("JWT token validation failed: {}", e.getMessage());
        }
        return false;
    }

    public boolean hasScope(String token, String requiredScope) {
        try {
            String scopes = extractScope(token);
            if (scopes == null) return false;

            return scopes.contains(requiredScope)
                    || scopes.contains("internal.read")
                    || scopes.contains("internal.write");
        } catch (Exception e) {
            logger.debug("Scope validation failed: {}", e.getMessage());
            return false;
        }
    }

    public boolean hasUserRole(String token, String requiredRole) {
        try {
            List<String> userRoles = extractUserRoles(token);
            return userRoles != null && userRoles.contains(requiredRole);
        } catch (Exception e) {
            logger.debug("User role validation failed: {}", e.getMessage());
            return false;
        }
    }
}

