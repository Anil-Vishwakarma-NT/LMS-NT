package com.nt.lms.api_gateway_lms.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.nt.lms.api_gateway_lms.constant.SecurityConstant.ACCESS_TOKEN_TYPE;
import static com.nt.lms.api_gateway_lms.constant.SecurityConstant.CLAIM_CLIENT_ID;
import static com.nt.lms.api_gateway_lms.constant.SecurityConstant.CLAIM_EMAIL;
import static com.nt.lms.api_gateway_lms.constant.SecurityConstant.CLAIM_FULL_NAME;
import static com.nt.lms.api_gateway_lms.constant.SecurityConstant.CLAIM_ROLES;
import static com.nt.lms.api_gateway_lms.constant.SecurityConstant.CLAIM_SCOPE;
import static com.nt.lms.api_gateway_lms.constant.SecurityConstant.CLAIM_TOKEN_TYPE;
import static com.nt.lms.api_gateway_lms.constant.SecurityConstant.CLAIM_USER_EMAIL;
import static com.nt.lms.api_gateway_lms.constant.SecurityConstant.CLAIM_USER_ID;
import static com.nt.lms.api_gateway_lms.constant.SecurityConstant.CLAIM_USER_ROLES;
import static com.nt.lms.api_gateway_lms.constant.SecurityConstant.DEFAULT_JWT_ISSUER;
import static com.nt.lms.api_gateway_lms.constant.SecurityConstant.DEFAULT_JWT_SECRET;
import static com.nt.lms.api_gateway_lms.constant.SecurityConstant.DEFAULT_SCOPE;
import static com.nt.lms.api_gateway_lms.constant.SecurityConstant.GATEWAY_SOURCE_VALUE;
import static com.nt.lms.api_gateway_lms.constant.SecurityConstant.INTERNAL_SCOPE;
import static com.nt.lms.api_gateway_lms.constant.SecurityConstant.INVALID_ACCESS_TOKEN_MSG;
import static com.nt.lms.api_gateway_lms.constant.SecurityConstant.INVALID_SERVICE_TOKEN_MSG;
import static com.nt.lms.api_gateway_lms.constant.SecurityConstant.INVALID_TOKEN_MSG;
import static com.nt.lms.api_gateway_lms.constant.SecurityConstant.INVALID_TOKEN_TYPE_MSG;
import static com.nt.lms.api_gateway_lms.constant.SecurityConstant.REFRESH_TOKEN_TYPE;
import static com.nt.lms.api_gateway_lms.constant.SecurityConstant.REFRESH_WINDOW;
import static com.nt.lms.api_gateway_lms.constant.SecurityConstant.ROLE_SERVICE;
import static com.nt.lms.api_gateway_lms.constant.SecurityConstant.SERVICE_TOKEN_PROCESSING_ERROR_MSG;
import static com.nt.lms.api_gateway_lms.constant.SecurityConstant.SERVICE_TOKEN_TYPE;
import static com.nt.lms.api_gateway_lms.constant.SecurityConstant.TOKEN_EXPIRED_MSG;
import static com.nt.lms.api_gateway_lms.constant.SecurityConstant.TOKEN_PROCESSING_ERROR_MSG;

/**
 * JWT (JSON Web Token) utility class for handling token operations in the LMS API Gateway.
 *
 * <p>This utility class provides comprehensive JWT token management including:
 * <ul>
 *   <li>Token generation (access, refresh, and service tokens)</li>
 *   <li>Token validation and verification</li>
 *   <li>Token introspection and claim extraction</li>
 *   <li>Token conversion between different types</li>
 *   <li>Token expiration and refresh management</li>
 * </ul>
 *
 * <p>The class supports three types of tokens:
 * <ul>
 *   <li><strong>Access Tokens:</strong> Used for user authentication and authorization</li>
 *   <li><strong>Refresh Tokens:</strong> Used to obtain new access tokens without re-authentication</li>
 *   <li><strong>Service Tokens:</strong> Used for service-to-service communication</li>
 * </ul>
 *
 * <p>All tokens are signed using HMAC-SHA256 algorithm and include standard JWT claims
 * along with custom claims for user information and authorization scopes.
 *
 * @author LMS Development Team
 * @version 1.0
 * @since 1.0
 */
@Component
@Slf4j
public class JwtUtil {

    /**
     * The secret key used for signing JWT tokens.
     * Can be configured via the {@code jwt.secret} property.
     */
    @Value("${jwt.secret:" + DEFAULT_JWT_SECRET + "}")
    private String secret;

    /**
     * The expiration time for access tokens in milliseconds.
     * Can be configured via the {@code jwt.access-token.expiration} property.
     */
    @Value("${jwt.access-token.expiration:}")
    private Long accessTokenExpiration;

    /**
     * The expiration time for refresh tokens in milliseconds.
     * Can be configured via the {@code jwt.refresh-token.expiration} property.
     */
    @Value("${jwt.refresh-token.expiration:}")
    private Long refreshTokenExpiration;

    /**
     * The expiration time for service tokens in milliseconds.
     * Can be configured via the {@code jwt.service-token.expiration} property.
     */
    @Value("${jwt.service-token.expiration:}")
    private Long serviceTokenExpiration;

    /**
     * The JWT issuer identifier.
     * Can be configured via the {@code jwt.issuer} property.
     */
    @Value("${jwt.issuer:" + DEFAULT_JWT_ISSUER + "}")
    private String issuer;

    /**
     * Creates and returns the HMAC signing key for JWT tokens.
     *
     * <p>The signing key is generated from the base64-decoded SECRET using HMAC-SHA algorithm.
     * This key is used to sign and verify JWT tokens to ensure their authenticity and integrity.
     *
     * @return the HMAC signing key for JWT operations
     * @throws IllegalArgumentException if the secret is not properly base64 encoded
     */
    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generates a new access token for user authentication.
     *
     * <p>Creates a JWT access token containing user information and authorities.
     * The token includes standard claims (subject, issuer, audience, expiration)
     * and custom claims for user details, roles, and token type.
     *
     * @param userDetails the Spring Security user details containing authorities
     * @param userId      the unique identifier for the user
     * @param email       the user's email address
     * @param name        the user's full name
     * @return a signed JWT access token as a string
     * @throws IllegalArgumentException if any required parameter is null or invalid
     */
    public String generateAccessToken(final UserDetails userDetails, final String userId, final String email, final String name) {
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

    /**
     * Generates a new refresh token for token renewal.
     *
     * <p>Creates a JWT refresh token that can be used to obtain new access tokens
     * without requiring the user to re-authenticate. The refresh token has a longer
     * expiration time than access tokens and contains similar user information.
     *
     * @param userDetails the Spring Security user details containing authorities
     * @param userId      the unique identifier for the user
     * @param email       the user's email address
     * @param name        the user's full name
     * @return a signed JWT refresh token as a string
     * @throws IllegalArgumentException if any required parameter is null or invalid
     */
    public String generateRefreshToken(
            final UserDetails userDetails,
            final String userId,
            final String email,
            final String name
    ) {
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

    /**
     * Generates a service token for service-to-service communication.
     *
     * <p>Creates a JWT service token that allows one service to authenticate
     * with another service. The token includes the source service as the subject,
     * target service as the audience, and specified scopes for authorization.
     *
     * @param sourceService the identifier of the service requesting the token
     * @param targetService the identifier of the service that will validate the token
     * @param scopes        a list of permission scopes granted to the source service
     * @return a signed JWT service token as a string
     * @throws IllegalArgumentException if any required parameter is null or invalid
     */
    public String generateServiceToken(final String sourceService, final String targetService, final List<String> scopes) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_SCOPE, String.join(" ", scopes));
        claims.put(CLAIM_ROLES, List.of(ROLE_SERVICE));
        claims.put(CLAIM_CLIENT_ID, sourceService);
        claims.put(CLAIM_TOKEN_TYPE, SERVICE_TOKEN_TYPE);

        return createToken(sourceService, targetService, claims, serviceTokenExpiration);
    }

    /**
     * Converts an access token to a service token for internal service communication.
     *
     * <p>Takes a valid user access token and converts it to a service token that can be
     * used for internal service-to-service calls while preserving user context.
     * The resulting token includes both service credentials and user information.
     *
     * @param accessToken   the original user access token to convert
     * @param targetService the identifier of the target service
     * @return a signed JWT service token containing user context
     * @throws IllegalArgumentException if the access token is invalid or not an access token
     * @throws RuntimeException         if token conversion fails
     */
    public String convertToServiceToken(final String accessToken, final String targetService) {
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

    /**
     * Generates a service token for direct access with user context.
     *
     * <p>Similar to {@link #convertToServiceToken(String, String)} but creates a service token
     * for direct access scenarios where no specific client ID is applicable.
     * Sets the client ID to "NA" and subject to "null" for direct access patterns.
     *
     * @param accessToken   the original user access token containing user context
     * @param targetService the identifier of the target service
     * @return a signed JWT service token for direct access
     * @throws IllegalArgumentException if the access token is invalid or not an access token
     * @throws RuntimeException         if token generation fails
     */
    public String generateServiceTokenForDirectAccess(final String accessToken, final String targetService) {
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

    /**
     * Creates a JWT token with the specified parameters.
     *
     * <p>This is the core token creation method used by all token generation methods.
     * It builds a complete JWT with standard claims (subject, issuer, audience, issued at, expiration)
     * and any custom claims provided in the claims map.
     *
     * @param sub        the subject claim (typically user ID or service ID)
     * @param audience   the audience claim (target service or gateway)
     * @param claims     a map of custom claims to include in the token
     * @param expiration the token expiration time in milliseconds from now
     * @return a signed JWT token as a compact string
     * @throws IllegalArgumentException if any required parameter is null or invalid
     */
    private String createToken(final String sub, final String audience, final Map<String, Object> claims, final Long expiration) {
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

    /**
     * Extracts all claims from a JWT token.
     *
     * <p>Parses and verifies the JWT token signature, then returns all claims
     * contained within the token. This method performs signature verification
     * and will throw an exception if the token is invalid or expired.
     *
     * @param token the JWT token to parse
     * @return the claims contained in the token
     * @throws JwtException             if the token is invalid, expired, or signature verification fails
     * @throws IllegalArgumentException if the token format is invalid
     */
    private Claims extractAllClaims(final String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Extracts a specific claim from a JWT token using a claims resolver function.
     *
     * <p>This generic method allows extraction of any claim from a token by providing
     * a function that takes the Claims object and returns the desired value.
     * It handles token parsing and applies the resolver function to extract the claim.
     *
     * @param <T>            the type of the claim value to extract
     * @param token          the JWT token to extract the claim from
     * @param claimsResolver a function that extracts the desired claim from the Claims object
     * @return the extracted claim value
     * @throws JwtException if the token is invalid or expired
     */
    public <T> T extractClaim(final String token, final Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Checks if a JWT token has expired.
     *
     * <p>Compares the token's expiration time with the current time to determine
     * if the token is still valid. Handles {@link ExpiredJwtException} gracefully
     * and logs debug information for expired tokens.
     *
     * @param token the JWT token to check for expiration
     * @return {@code true} if the token has expired, {@code false} otherwise
     */
    public Boolean isTokenExpired(final String token) {
        try {
            final Date expiration = extractExpiration(token);
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            log.debug(TOKEN_EXPIRED_MSG + ": {}", e.getMessage());
            return true;
        }
    }

    /**
     * Extracts the subject claim from a JWT token.
     *
     * <p>The subject typically represents the principal that the token was issued for,
     * such as a user ID or service identifier.
     *
     * @param token the JWT token to extract the subject from
     * @return the subject claim value
     * @throws JwtException if the token is invalid or expired
     */
    public String extractSubject(final String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the audience claim from a JWT token.
     *
     * <p>The audience identifies the intended recipient of the token,
     * typically a service or application that should accept the token.
     *
     * @param token the JWT token to extract the audience from
     * @return the audience claim value
     * @throws JwtException if the token is invalid or expired
     */
    public String extractAudience(final String token) {
        return extractClaim(token, Claims::getAudience);
    }

    /**
     * Extracts the expiration date from a JWT token.
     *
     * <p>Returns the date and time when the token expires and should
     * no longer be considered valid.
     *
     * @param token the JWT token to extract the expiration from
     * @return the expiration date of the token
     * @throws JwtException if the token is invalid or expired
     */
    public Date extractExpiration(final String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts the token type from a JWT token.
     *
     * <p>Returns the custom token type claim that identifies whether the token
     * is an access token, refresh token, or service token.
     *
     * @param token the JWT token to extract the token type from
     * @return the token type (e.g., "access", "refresh", "service")
     * @throws JwtException if the token is invalid or expired
     */
    public String extractTokenType(final String token) {
        return extractClaim(token, claims -> (String) claims.get(CLAIM_TOKEN_TYPE));
    }

    /**
     * Extracts the roles claim from a JWT token.
     *
     * <p>Returns the list of roles or authorities associated with the token,
     * which are used for authorization decisions.
     *
     * @param token the JWT token to extract the roles from
     * @return a list of role strings
     * @throws JwtException if the token is invalid or expired
     */
    public List<String> extractRoles(final String token) {
        return extractClaim(token, claims -> (List<String>) claims.get(CLAIM_ROLES));
    }

    /**
     * Extracts the email claim from a JWT token.
     *
     * <p>Returns the email address associated with the user represented by the token.
     *
     * @param token the JWT token to extract the email from
     * @return the email address
     * @throws JwtException if the token is invalid or expired
     */
    public String extractEmail(final String token) {
        return extractClaim(token, claims -> (String) claims.get(CLAIM_EMAIL));
    }

    /**
     * Extracts the scope claim from a JWT token.
     *
     * <p>Returns the authorization scope associated with the token,
     * which defines the level of access granted.
     *
     * @param token the JWT token to extract the scope from
     * @return the scope string
     * @throws JwtException if the token is invalid or expired
     */
    public String extractScope(final String token) {
        return extractClaim(token, claims -> (String) claims.get(CLAIM_SCOPE));
    }

    /**
     * Validates an access token against a username.
     *
     * <p>Performs comprehensive validation of an access token by checking:
     * <ul>
     *   <li>Email in token matches the provided username</li>
     *   <li>Token type is ACCESS_TOKEN_TYPE</li>
     *   <li>Token has not expired</li>
     * </ul>
     *
     * @param token    the JWT access token to validate
     * @param username the username (email) to validate against
     * @return {@code true} if the token is valid for the user, {@code false} otherwise
     */
    public Boolean validateAccessToken(final String token, final String username) {
        try {
            final String tokenEmail = extractEmail(token);
            final String tokenType = extractTokenType(token);
            return (tokenEmail.equals(username)
                    && ACCESS_TOKEN_TYPE.equals(tokenType)
                    && !isTokenExpired(token));
        } catch (Exception e) {
            log.debug(INVALID_ACCESS_TOKEN_MSG + ": {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validates a refresh token against a user ID.
     *
     * <p>Performs validation of a refresh token by checking:
     * <ul>
     *   <li>Subject in token matches the provided user ID</li>
     *   <li>Token type is REFRESH_TOKEN_TYPE</li>
     *   <li>Token has not expired</li>
     * </ul>
     *
     * @param token  the JWT refresh token to validate
     * @param userId the user ID to validate against
     * @return {@code true} if the refresh token is valid for the user, {@code false} otherwise
     */
    public Boolean validateRefreshToken(final String token, final String userId) {
        try {
            final String tokenSubject = extractSubject(token);
            final String tokenType = extractTokenType(token);
            return (tokenSubject.equals(userId)
                    && REFRESH_TOKEN_TYPE.equals(tokenType)
                    && !isTokenExpired(token));
        } catch (Exception e) {
            log.debug(INVALID_TOKEN_MSG + ": {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validates a service token against an expected audience.
     *
     * <p>Performs validation of a service token by checking:
     * <ul>
     *   <li>Token type is SERVICE_TOKEN_TYPE</li>
     *   <li>Audience matches the expected audience (if provided)</li>
     *   <li>Token contains the SERVICE role</li>
     *   <li>Token has not expired</li>
     * </ul>
     *
     * @param token            the JWT service token to validate
     * @param expectedAudience the expected audience, or {@code null} to skip audience validation
     * @return {@code true} if the service token is valid, {@code false} otherwise
     */
    public Boolean validateServiceToken(final String token, final String expectedAudience) {
        try {
            final String tokenAudience = extractAudience(token);
            final String tokenType = extractTokenType(token);
            final List<String> roles = extractRoles(token);

            return (SERVICE_TOKEN_TYPE.equals(tokenType)
                    && (expectedAudience == null || expectedAudience.equals(tokenAudience))
                    && roles.contains(ROLE_SERVICE)
                    && !isTokenExpired(token));
        } catch (Exception e) {
            log.debug(INVALID_SERVICE_TOKEN_MSG + ": {}", e.getMessage());
            return false;
        }
    }

    /**
     * Validates the signature and structure of any JWT token.
     *
     * <p>Performs basic token validation by attempting to parse and verify
     * the token signature. This method does not validate token type, expiration,
     * or any specific claims - only structural validity and signature.
     *
     * @param token the JWT token to validate
     * @return {@code true} if the token structure and signature are valid, {@code false} otherwise
     */
    public Boolean validateToken(final String token) {
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

    /**
     * Creates a new access token using a valid refresh token.
     *
     * <p>Validates the provided refresh token and generates a new access token
     * with the same user information but updated expiration time. This allows
     * users to obtain new access tokens without re-authentication.
     *
     * @param refreshToken the refresh token to use for generating a new access token
     * @param userDetails  the Spring Security user details containing authorities
     * @param userId       the user ID associated with the refresh token
     * @param email        the user's email address
     * @param name         the user's full name
     * @return a new signed JWT access token
     * @throws RuntimeException if the refresh token is invalid or expired
     */
    public String refreshAccessToken(
            final String refreshToken,
            final UserDetails userDetails,
            final String userId,
            final String email,
            final String name
    ) {
        if (!validateRefreshToken(refreshToken, userId)) {
            throw new RuntimeException("Invalid refresh token");
        }
        return generateAccessToken(userDetails, userId, email, name);
    }

    /**
     * Provides detailed information about a JWT token (token introspection).
     *
     * <p>Extracts and returns comprehensive information from a JWT token including
     * all standard claims, custom claims, and expiration status. This is useful
     * for debugging, auditing, and token analysis.
     *
     * @param token the JWT token to introspect
     * @return a map containing all token information and metadata
     * @throws RuntimeException if token introspection fails due to invalid token
     */
    public Map<String, Object> introspectToken(final String token) {
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

    /**
     * Gets the configured access token expiration time.
     *
     * @return the access token expiration time in milliseconds
     */
    public Long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    /**
     * Gets the configured refresh token expiration time.
     *
     * @return the refresh token expiration time in milliseconds
     */
    public Long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

    /**
     * Gets the configured service token expiration time.
     *
     * @return the service token expiration time in milliseconds
     */
    public Long getServiceTokenExpiration() {
        return serviceTokenExpiration;
    }

    /**
     * Determines if an expired token can still be refreshed.
     *
     * <p>Checks if an expired token is still within the refresh window,
     * allowing for token refresh even after expiration within a grace period.
     * This helps handle clock skew and provides better user experience.
     *
     * @param token the JWT token to check for refresh eligibility
     * @return {@code true} if the token can be refreshed, {@code false} otherwise
     */
    public Boolean canTokenBeRefreshed(final String token) {
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
