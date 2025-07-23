package com.nt.user_service_lms.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;

/**
 * Entity representing a RefreshToken.
 * Used to store information about the refresh tokens associated with users.
 */
@Entity
@Table(name = "Refreshtoken")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshToken {

    /**
     * The unique identifier for the refresh token.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user ID associated with this refresh token.
     */

    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * The refresh token string itself.
     * This token is unique and used for refreshing access tokens.
     */
    @Column(nullable = false, unique = true)
    private String token;

    /**
     * The expiration date of the refresh token.
     * After this date, the refresh token will no longer be valid.
     */
    @Column(nullable = false)
    private Instant expiryDate;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RefreshToken that = (RefreshToken) o;
        return Objects.equals(id, that.id) && Objects.equals(userId, that.userId) && Objects.equals(token, that.token) && Objects.equals(expiryDate, that.expiryDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, token, expiryDate);
    }
}
