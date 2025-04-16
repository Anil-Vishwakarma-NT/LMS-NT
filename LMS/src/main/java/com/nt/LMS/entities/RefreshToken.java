package com.nt.LMS.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;

/**
 * Entity representing a RefreshToken.
 * Used to store information about the refresh tokens associated with users.
 */
@Entity
@Table(name = "Refreshtoken")
@Data
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
    @JoinColumn(name = "user_id", nullable = false)
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
}
