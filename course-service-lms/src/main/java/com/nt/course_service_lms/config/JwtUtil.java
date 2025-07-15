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
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import static com.nt.course_service_lms.constants.SecurityConstant.CLAIM_CLIENT_ID;
import static com.nt.course_service_lms.constants.SecurityConstant.CLAIM_ROLES;
import static com.nt.course_service_lms.constants.SecurityConstant.CLAIM_SCOPE;
import static com.nt.course_service_lms.constants.SecurityConstant.CLAIM_TOKEN_TYPE;
import static com.nt.course_service_lms.constants.SecurityConstant.CLAIM_USER_EMAIL;
import static com.nt.course_service_lms.constants.SecurityConstant.CLAIM_USER_FULL_NAME;
import static com.nt.course_service_lms.constants.SecurityConstant.CLAIM_USER_ID;
import static com.nt.course_service_lms.constants.SecurityConstant.CLAIM_USER_ROLES;
import static com.nt.course_service_lms.constants.SecurityConstant.ROLE_SERVICE;
import static com.nt.course_service_lms.constants.SecurityConstant.TOKEN_TYPE_SERVICE;

/**
 * Utility class for JWT token operations including validation, extraction, and verification.
 * Provides methods to extract claims, validate tokens, and check permissions for service authentication.
 */
@Component
public class JwtUtil {

    /**
     * Logger instance for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtil.class);

    /**
     * JWT secret key for token signing and verification.
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * JWT issuer identifier.
     */
    @Value("${jwt.issuer}")
    private String issuer;

    /**
     * Generates the signing key from the base64-encoded secret.
     *
     * @return Key object for JWT signing and verification
     */
    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extracts the audience claim from the JWT token.
     *
     * @param token JWT token string
     * @return audience value from the token
     */
    public String extractAudience(final String token) {
        return extractClaim(token, Claims::getAudience);
    }

    /**
     * Extracts the expiration date from the JWT token.
     *
     * @param token JWT token string
     * @return expiration date of the token
     */
    public Date extractExpiration(final String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts the subject claim from the JWT token.
     *
     * @param token JWT token string
     * @return subject value from the token
     */
    public String extractSubject(final String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the token type from the JWT token.
     *
     * @param token JWT token string
     * @return token type value
     */
    public String extractTokenType(final String token) {
        return extractClaim(token, claims -> (String) claims.get(CLAIM_TOKEN_TYPE));
    }

    /**
     * Extracts the roles list from the JWT token.
     *
     * @param token JWT token string
     * @return list of roles assigned to the token
     */
    public List<String> extractRoles(final String token) {
        return extractClaim(token, claims -> (List<String>) claims.get(CLAIM_ROLES));
    }

    /**
     * Extracts the scope from the JWT token.
     *
     * @param token JWT token string
     * @return scope value from the token
     */
    public String extractScope(final String token) {
        return extractClaim(token, claims -> (String) claims.get(CLAIM_SCOPE));
    }

    /**
     * Extracts the user ID from the JWT token.
     *
     * @param token JWT token string
     * @return user ID value from the token
     */
    public String extractUserId(final String token) {
        return extractClaim(token, claims -> (String) claims.get(CLAIM_USER_ID));
    }

    /**
     * Extracts the user email from the JWT token.
     *
     * @param token JWT token string
     * @return user email value from the token
     */
    public String extractUserEmail(final String token) {
        return extractClaim(token, claims -> (String) claims.get(CLAIM_USER_EMAIL));
    }

    /**
     * Extracts the user full name from the JWT token.
     *
     * @param token JWT token string
     * @return user full name value from the token
     */
    public String extractUserFullName(final String token) {
        return extractClaim(token, claims -> (String) claims.get(CLAIM_USER_FULL_NAME));
    }

    /**
     * Extracts the user roles list from the JWT token.
     *
     * @param token JWT token string
     * @return list of user roles from the token
     */
    public List<String> extractUserRoles(final String token) {
        return extractClaim(token, claims -> (List<String>) claims.get(CLAIM_USER_ROLES));
    }

    /**
     * Extracts the client ID from the JWT token.
     *
     * @param token JWT token string
     * @return client ID value from the token
     */
    public String extractClientId(final String token) {
        return extractClaim(token, claims -> (String) claims.get(CLAIM_CLIENT_ID));
    }

    /**
     * Generic method to extract a specific claim from the JWT token.
     *
     * @param <T>            the type of the claim value
     * @param token          JWT token string
     * @param claimsResolver function to extract the desired claim
     * @return the extracted claim value
     */
    public <T> T extractClaim(final String token, final Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from the JWT token.
     *
     * @param token JWT token string
     * @return Claims object containing all token claims
     */
    private Claims extractAllClaims(final String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Checks if the JWT token is expired.
     *
     * @param token JWT token string
     * @return true if the token is expired, false otherwise
     */
    public Boolean isTokenExpired(final String token) {
        try {
            final Date expiration = extractExpiration(token);
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            LOGGER.debug("Token is expired: {}", e.getMessage());
            return true;
        }
    }

    /**
     * Validates a service token by checking its type, audience, roles, and expiration.
     *
     * @param token            JWT token string
     * @param expectedAudience expected audience value (can be null)
     * @return true if the service token is valid, false otherwise
     */
    public Boolean validateServiceToken(final String token, final String expectedAudience) {
        try {
            final String tokenAudience = extractAudience(token);
            final String tokenType = extractTokenType(token);
            final List<String> roles = extractRoles(token);

            boolean isValidAudience = expectedAudience == null
                    || expectedAudience.equals(tokenAudience);
            return (TOKEN_TYPE_SERVICE.equals(tokenType)
                    && isValidAudience
                    && roles.contains(ROLE_SERVICE)
                    && !isTokenExpired(token));
        } catch (Exception e) {
            LOGGER.debug("Service token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validates the JWT token signature and structure.
     *
     * @param token JWT token string
     * @return true if the token is valid, false otherwise
     */
    public Boolean validateToken(final String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            LOGGER.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            LOGGER.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            LOGGER.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            LOGGER.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            LOGGER.error("JWT claims string is empty: {}", e.getMessage());
        } catch (Exception e) {
            LOGGER.error("JWT token validation failed: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Checks if the token has the required scope or internal permissions.
     *
     * @param token         JWT token string
     * @param requiredScope the scope to check for
     * @return true if the token has the required scope or internal permissions, false otherwise
     */
    public boolean hasScope(final String token, final String requiredScope) {
        try {
            String scopes = extractScope(token);
            if (scopes == null) {
                return false;
            }

            return scopes.contains(requiredScope)
                    || scopes.contains("internal.read")
                    || scopes.contains("internal.write");
        } catch (Exception e) {
            LOGGER.debug("Scope validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Checks if the token contains the required user role.
     *
     * @param token        JWT token string
     * @param requiredRole the role to check for
     * @return true if the token contains the required user role, false otherwise
     */
    public boolean hasUserRole(final String token, final String requiredRole) {
        try {
            List<String> userRoles = extractUserRoles(token);
            return userRoles != null && userRoles.contains(requiredRole);
        } catch (Exception e) {
            LOGGER.debug("User role validation failed: {}", e.getMessage());
            return false;
        }
    }
}
