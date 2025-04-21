package com.nt.LMS.repository;

import com.nt.LMS.entities.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for handling CRUD operations on RefreshToken entities.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Find a RefreshToken by its token string.
     *
     * @param token the refresh token string to search for.
     * @return an Optional containing the RefreshToken if found, otherwise empty.
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Delete all RefreshTokens associated with a specific user.
     *
     * @param userId the ID of the user whose refresh tokens should be deleted.
     */
    @Transactional
    void deleteByUserId(Long userId);
}
