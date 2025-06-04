package com.example.course_service_lms.repository;

import com.example.course_service_lms.entity.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, Integer> {

    // Fetch user progress for a specific content
    @Query("SELECT u FROM UserProgress u WHERE u.userId = :userId AND u.contentId = :contentId")
    Optional<UserProgress> findProgressByUserIdAndContentId(@Param("userId") int userId, @Param("contentId") int contentId);

    // Fetch all progress records for a specific course
    @Query("SELECT u FROM UserProgress u WHERE u.userId = :userId AND u.courseId = :courseId")
    List<UserProgress> findProgressByUserIdAndCourseId(@Param("userId") int userId, @Param("courseId") long courseId);

    @Query("SELECT u FROM UserProgress u WHERE u.userId = :userId AND u.courseId = :courseId ORDER BY u.progressId ASC LIMIT 1")
    UserProgress findSingleCourseProgress(int userId, int courseId);

    @Query("SELECT u.lastPosition FROM UserProgress u WHERE u.userId = :userId AND u.courseId = :courseId AND u.contentId = :contentId")
    Double findLastPosition(int userId, int courseId, int contentId);

    @Query("SELECT u.contentType FROM UserProgress u WHERE u.userId = :userId AND u.contentId = :contentId")
    String findContentType(@Param("userId") int userId, @Param("contentId") int contentId);

    @Query("SELECT u FROM UserProgress u WHERE u.userId = :userId AND u.courseId = :courseId and u.contentId = :contentId ORDER BY u.progressId ASC")
    UserProgress findContentProgress(int userId, int courseId, int contentId);

}
