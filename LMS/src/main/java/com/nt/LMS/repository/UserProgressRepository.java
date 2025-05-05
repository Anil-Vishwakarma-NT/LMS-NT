package com.nt.LMS.repository;

import com.nt.LMS.entities.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, Integer> {

    // Fetch user progress for a specific content
    @Query("SELECT u FROM UserProgress u WHERE u.userId = :userId AND u.contentId = :contentId")
    UserProgress findProgressByUserIdAndContentId(@Param("userId") int userId, @Param("contentId") int contentId);
}