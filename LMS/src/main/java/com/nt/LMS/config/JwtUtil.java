package com.nt.LMS.config;

import io.jsonwebtoken.*;
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

@Component
public class JwtUtil {

    private final SecretKey SECRET_KEY;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.SECRET_KEY = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 60; // 1 hour
    private final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24; // 1 day

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .setAllowedClockSkewSeconds(300) // Increase allowed skew
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
public String generateAccessToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("roles", userDetails.getAuthorities().stream()
            .map(authority -> authority.getAuthority())  // Remove "ROLE_" prefix
            .collect(Collectors.joining(",")));

    return createToken(claims, userDetails.getUsername(), ACCESS_TOKEN_EXPIRATION);
}


    public String generateRefreshToken(UserDetails userDetails) {
        return createToken(new HashMap<>(), userDetails.getUsername(), REFRESH_TOKEN_EXPIRATION);
    }

//    private String createToken(Map<String, Object> claims, String subject, long expiration) {
//        return Jwts.builder()
//                .setClaims(claims)
//                .setSubject(subject)
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + expiration))
//                .signWith(SECRET_KEY)
//                .compact();
//    }

    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        long issuedAt = System.currentTimeMillis();
        long expiryAt = issuedAt + expiration;

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(issuedAt))
                .setExpiration(new Date(expiryAt))
                .signWith(SECRET_KEY)
                .compact();
    }


    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}