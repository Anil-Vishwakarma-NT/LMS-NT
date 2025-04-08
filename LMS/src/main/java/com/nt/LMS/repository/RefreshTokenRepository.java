package com.nt.LMS.repository;

import com.nt.LMS.entities.RefreshToken;
import com.nt.LMS.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUser(User user);
    void deleteByUser_UserId(Long userId);
    void deleteByUser(User user);
}
