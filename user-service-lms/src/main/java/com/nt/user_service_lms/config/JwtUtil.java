package com.nt.user_service_lms.config;

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

import static com.nt.user_service_lms.constants.SecurityConstant.CLAIM_CLIENT_ID;
import static com.nt.user_service_lms.constants.SecurityConstant.CLAIM_ROLES;
import static com.nt.user_service_lms.constants.SecurityConstant.CLAIM_SCOPE;
import static com.nt.user_service_lms.constants.SecurityConstant.CLAIM_TOKEN_TYPE;
import static com.nt.user_service_lms.constants.SecurityConstant.CLAIM_USER_EMAIL;
import static com.nt.user_service_lms.constants.SecurityConstant.CLAIM_USER_FULL_NAME;
import static com.nt.user_service_lms.constants.SecurityConstant.CLAIM_USER_ID;
import static com.nt.user_service_lms.constants.SecurityConstant.CLAIM_USER_ROLES;
import static com.nt.user_service_lms.constants.SecurityConstant.ROLE_SERVICE;
import static com.nt.user_service_lms.constants.SecurityConstant.TOKEN_TYPE_SERVICE;

/**
 * Utility class for handling JSON Web Tokens (JWT) operations such as
 * extracting claims, validating tokens, and checking scopes or roles.
 * <p>
 * This class uses the JJWT library for parsing and verifying JWT tokens.
 * It supports extraction of various custom claims and validation against roles, scopes, and audiences.
 */
@Component
public class JwtUtil {

    /**
     * Logger for debugging and error reporting.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtil.class);

    /**
     * Secret key used for signing and verifying JWT tokens, injected from application properties.
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Issuer of the JWT token, injected from application properties.
     */
    @Value("${jwt.issuer}")
    private String issuer;

    /**
     * Generates the signing key from the secret.
     *
     * @return Signing {@link Key} object.
     */
    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extracts the audience from the JWT token.
     *
     * @param token JWT token.
     * @return Audience claim.
     */
    public String extractAudience(final String token) {
        return extractClaim(token, Claims::getAudience);
    }

    /**
     * Extracts the expiration date from the JWT token.
     *
     * @param token JWT token.
     * @return Expiration date.
     */
    public Date extractExpiration(final String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts the subject (typically the user email or ID) from the JWT token.
     *
     * @param token JWT token.
     * @return Subject claim.
     */
    public String extractSubject(final String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the token type from the JWT token.
     *
     * @param token JWT token.
     * @return Token type claim.
     */
    public String extractTokenType(final String token) {
        return extractClaim(token, claims -> (String) claims.get(CLAIM_TOKEN_TYPE));
    }

    /**
     * Extracts the roles from a service token.
     *
     * @param token JWT token.
     * @return List of roles.
     */
    public List<String> extractRoles(final String token) {
        return extractClaim(token, claims -> (List<String>) claims.get(CLAIM_ROLES));
    }

    /**
     * Extracts the scope string from the JWT token.
     *
     * @param token JWT token.
     * @return Scope value.
     */
    public String extractScope(final String token) {
        return extractClaim(token, claims -> (String) claims.get(CLAIM_SCOPE));
    }

    /**
     * Extracts the user ID from the JWT token.
     *
     * @param token JWT token.
     * @return User ID.
     */
    public String extractUserId(final String token) {
        return extractClaim(token, claims -> (String) claims.get(CLAIM_USER_ID));
    }

    /**
     * Extracts the user email from the JWT token.
     *
     * @param token JWT token.
     * @return User email.
     */
    public String extractUserEmail(final String token) {
        return extractClaim(token, claims -> (String) claims.get(CLAIM_USER_EMAIL));
    }

    /**
     * Extracts the full name of the user from the JWT token.
     *
     * @param token JWT token.
     * @return User's full name.
     */
    public String extractUserFullName(final String token) {
        return extractClaim(token, claims -> (String) claims.get(CLAIM_USER_FULL_NAME));
    }

    /**
     * Extracts the list of user roles from the JWT token.
     *
     * @param token JWT token.
     * @return List of user roles.
     */
    public List<String> extractUserRoles(final String token) {
        return extractClaim(token, claims -> (List<String>) claims.get(CLAIM_USER_ROLES));
    }

    /**
     * Extracts the client ID from the JWT token.
     *
     * @param token JWT token.
     * @return Client ID.
     */
    public String extractClientId(final String token) {
        return extractClaim(token, claims -> (String) claims.get(CLAIM_CLIENT_ID));
    }

    /**
     * Generic method to extract a specific claim using a resolver function.
     *
     * @param token          JWT token.
     * @param claimsResolver Function to resolve a specific claim.
     * @param <T>            Type of the claim.
     * @return Extracted claim value.
     */
    public <T> T extractClaim(final String token, final Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from the JWT token.
     *
     * @param token JWT token.
     * @return All claims.
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
     * @param token JWT token.
     * @return True if expired, false otherwise.
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
     * Validates a service token by checking its type, roles, and expiration status.
     *
     * @param token            JWT token.
     * @param expectedAudience Expected audience to match (can be null).
     * @return True if valid, false otherwise.
     */
    public Boolean validateServiceToken(final String token, final String expectedAudience) {
        try {
            final String tokenAudience = extractAudience(token);
            final String tokenType = extractTokenType(token);
            final List<String> roles = extractRoles(token);

            boolean isValidAudience = expectedAudience == null || expectedAudience.equals(tokenAudience);

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
     * Validates a JWT token against its signature and structure.
     *
     * @param token JWT token.
     * @return True if valid, false otherwise.
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
     * Checks whether the token has a required scope or any internal scopes.
     *
     * @param token         JWT token.
     * @param requiredScope Scope to check.
     * @return True if scope is present, false otherwise.
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
     * Checks whether the token contains a specific user role.
     *
     * @param token        JWT token.
     * @param requiredRole Role to check.
     * @return True if role is present, false otherwise.
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
