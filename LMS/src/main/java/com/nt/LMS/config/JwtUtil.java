package com.nt.LMS.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.nt.LMS.constants.JwtConstant.ALLOWED_CLOCK_SKEW_SECONDS;

/**
 * Utility class for working with JWT tokens.
 *
 * This class provides methods to generate, validate, and extract information from JWT tokens.
 * It is designed for use with Spring Security to secure applications with JWT-based authentication.
 */
@Component
public final class JwtUtil {

    /**
     * Secret key for signing the JWT token.
     */
    private final SecretKey secretKey;

    /**
     * The expiration time for access tokens (1 hour).
     */
//    private final long accessTokenExpiration = 1000 * 60 * 60; // 1 hour
    private final long accessTokenExpiration = 1000 * 60;

    /**
     * The expiration time for refresh tokens (1 day).
     */
//    private final long refreshTokenExpiration = 1000 * 60 * 60 * 24; // 1 day
    private final long refreshTokenExpiration = 1000 * 60 * 5;
    /**
     * Constructs a JwtUtil instance with the provided secret.
     *
     * @param secret the secret used for signing JWT tokens
     */
    public JwtUtil(@Value("${jwt.secret}") final String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Extracts the username from the JWT token.
     *
     * @param token the JWT token to extract the username from
     * @return the extracted username
     */
    public String extractUsername(final String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a claim from the JWT token.
     *
     * @param token the JWT token to extract the claim from
     * @param claimsResolver a function to extract the claim from the Claims object
     * @param <T> the type of the claim
     * @return the extracted claim
     */
    public <T> T extractClaim(final String token, final Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from the JWT token.
     *
     * @param token the JWT token to extract the claims from
     * @return the extracted claims
     * @throws JwtException if the token is invalid
     */
    private Claims extractAllClaims(final String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .setAllowedClockSkewSeconds(ALLOWED_CLOCK_SKEW_SECONDS)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims;
        } catch (ExpiredJwtException e) {
            throw e; // Re-throw or handle it based on your use case
        } catch (JwtException e) {
            System.out.println("Invalid token: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Generates an access token for the specified user details.
     *
     * @param userDetails the user details to generate the token for
     * @return the generated access token
     */
    public String generateAccessToken(final UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.joining(",")));

        return createToken(claims, userDetails.getUsername(), accessTokenExpiration);
    }

    /**
     * Generates a refresh token for the specified user details.
     *
     * @param userDetails the user details to generate the refresh token for
     * @return the generated refresh token
     */
    public String generateRefreshToken(final UserDetails userDetails) {
        return createToken(new HashMap<>(), userDetails.getUsername(), refreshTokenExpiration);
    }

    /**
     * Creates a JWT token with the specified claims, subject, and expiration time.
     *
     * @param claims the claims to include in the token
     * @param subject the subject (usually the username) of the token
     * @param expiration the expiration time for the token
     * @return the created JWT token
     */
    private String createToken(final Map<String, Object> claims, final String subject, final long expiration) {
        long issuedAt = System.currentTimeMillis();
        long expiryAt = issuedAt + expiration;

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(issuedAt))
                .setExpiration(new Date(expiryAt))
                .signWith(secretKey)
                .compact();
    }

    /**
     * Validates the JWT token.
     *
     * @param token the JWT token to validate
     * @param userDetails the user details to validate the token against
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(final String token, final UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Checks if the JWT token has expired.
     *
     * @param token the JWT token to check
     * @return true if the token is expired, false otherwise
     */
    private boolean isTokenExpired(final String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}
