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
import java.util.Base64;
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

@Component
public class JwtUtil {

    /**
     * Logger for logging JWT-related operations and errors.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtil.class);

    /**
     * The secret key used for signing the JWT.
     * Retrieved from application properties.
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * The expected issuer of the JWT.
     * Retrieved from application properties.
     */
    @Value("${jwt.issuer}")
    private String issuer;

    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extracts the audience claim from the token.
     *
     * @param token the JWT token
     * @return the audience value
     */
    public String extractAudience(final String token) {
        return extractClaim(token, Claims::getAudience);
    }

    /**
     * Extracts the expiration date from the token.
     *
     * @param token the JWT token
     * @return the expiration date
     */
    public Date extractExpiration(final String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts the subject claim from the token.
     *
     * @param token the JWT token
     * @return the subject value
     */
    public String extractSubject(final String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the token type from the claims.
     *
     * @param token the JWT token
     * @return the token type
     */
    public String extractTokenType(final String token) {
        return extractClaim(token, claims -> (String) claims.get(CLAIM_TOKEN_TYPE));
    }

    /**
     * Extracts the roles from the token.
     *
     * @param token the JWT token
     * @return the list of roles
     */
    public List<String> extractRoles(final String token) {
        return extractClaim(token, claims -> (List<String>) claims.get(CLAIM_ROLES));
    }

    /**
     * Extracts the scope value from the token.
     *
     * @param token the JWT token
     * @return the scope
     */
    public String extractScope(final String token) {
        return extractClaim(token, claims -> (String) claims.get(CLAIM_SCOPE));
    }

    /**
     * Extracts the user ID from the token.
     *
     * @param token the JWT token
     * @return the user ID
     */
    public String extractUserId(final String token) {
        return extractClaim(token, claims -> (String) claims.get(CLAIM_USER_ID));
    }

    /**
     * Extracts the user email from the token.
     *
     * @param token the JWT token
     * @return the user email
     */
    public String extractUserEmail(final String token) {
        return extractClaim(token, claims -> (String) claims.get(CLAIM_USER_EMAIL));
    }

    /**
     * Extracts the full name of the user from the token.
     *
     * @param token the JWT token
     * @return the user's full name
     */
    public String extractUserFullName(final String token) {
        return extractClaim(token, claims -> (String) claims.get(CLAIM_USER_FULL_NAME));
    }

    /**
     * Extracts the user roles from the token.
     *
     * @param token the JWT token
     * @return the list of user roles
     */
    public List<String> extractUserRoles(final String token) {
        return extractClaim(token, claims -> (List<String>) claims.get(CLAIM_USER_ROLES));
    }

    /**
     * Extracts the client ID from the token.
     *
     * @param token the JWT token
     * @return the client ID
     */
    public String extractClientId(final String token) {
        return extractClaim(token, claims -> (String) claims.get(CLAIM_CLIENT_ID));
    }

    /**
     * Extracts a claim from the token using a claims resolver function.
     *
     * @param token          the JWT token
     * @param claimsResolver function to extract specific claim
     * @param <T>            the type of the claim
     * @return the extracted claim
     */
    public <T> T extractClaim(final String token, final Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(final String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Checks if the token is expired.
     *
     * @param token the JWT token
     * @return true if expired, otherwise false
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
     * Validates a service token based on audience, type, and role.
     *
     * @param token            the JWT token
     * @param expectedAudience the expected audience value
     * @return true if valid, otherwise false
     */
    public Boolean validateServiceToken(final String token, final String expectedAudience) {
        try {
            final String tokenAudience = extractAudience(token);
            final String tokenType = extractTokenType(token);
            final List<String> roles = extractRoles(token);

            boolean isValidAudience = expectedAudience == null
                    ||
                    expectedAudience.equals(tokenAudience);

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
     * Validates the token signature and structure.
     *
     * @param token the JWT token
     * @return true if token is valid, otherwise false
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
     * Checks if the token contains the required scope.
     *
     * @param token         the JWT token
     * @param requiredScope the scope to check
     * @return true if the required scope is present, otherwise false
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
     * @param token        the JWT token
     * @param requiredRole the role to check
     * @return true if the role is present, otherwise false
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
