package com.nt.course_service_lms.repository;

import com.nt.course_service_lms.entity.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing UserProgress entities.
 * Provides data access operations for user progress tracking in the LMS.
 */
@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, Integer> {

    /**
     * Finds user progress for a specific content.
     *
     * @param userId    the ID of the user
     * @param contentId the ID of the content
     * @return an Optional containing the UserProgress if found, empty otherwise
     */
    @Query("SELECT u FROM UserProgress u WHERE u.userId = :userId AND u.contentId = :contentId")
    Optional<UserProgress> findProgressByUserIdAndContentId(@Param("userId") Long userId, @Param("contentId") Long contentId);

    /**
     * Finds all progress records for a specific user and course.
     *
     * @param userId   the ID of the user
     * @param courseId the ID of the course
     * @return a list of UserProgress records for the specified user and course
     */
    @Query("SELECT u FROM UserProgress u WHERE u.userId = :userId AND u.courseId = :courseId")
    List<UserProgress> findProgressByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") long courseId);

    /**
     * Finds the first progress record for a specific user and course, ordered by progress ID.
     *
     * @param userId   the ID of the user
     * @param courseId the ID of the course
     * @return the first UserProgress record for the specified user and course
     */
    @Query("SELECT u FROM UserProgress u WHERE u.userId = :userId AND u.courseId = :courseId ORDER BY u.progressId ASC LIMIT 1")
    UserProgress findSingleCourseProgress(Long userId, Long courseId);

    /**
     * Finds the last position for a specific user's progress in a course content.
     *
     * @param userId    the ID of the user
     * @param courseId  the ID of the course
     * @param contentId the ID of the content
     * @return the last position as a Double value
     */
    @Query("SELECT u.lastPosition FROM UserProgress u"
            + " WHERE u.userId = :userId AND u.courseId = :courseId AND u.contentId = :contentId")
    Double findLastPosition(Long userId, Long courseId, Long contentId);

    /**
     * Finds the content type for a specific user's content progress.
     *
     * @param userId    the ID of the user
     * @param contentId the ID of the content
     * @return the content type as a String
     */
    @Query("SELECT u.contentType FROM UserProgress u WHERE u.userId = :userId AND u.contentId = :contentId")
    String findContentType(@Param("userId") Long userId, @Param("contentId") Long contentId);

    /**
     * Finds the content progress for a specific user, course, and content combination.
     *
     * @param userId    the ID of the user
     * @param courseId  the ID of the course
     * @param contentId the ID of the content
     * @return the UserProgress record for the specified criteria, ordered by progress ID
     */
    @Query("SELECT u FROM UserProgress u "
            + "WHERE u.userId = :userId AND u.courseId = :courseId and u.contentId = :contentId ORDER BY u.progressId ASC")
    UserProgress findContentProgress(Long userId, Long courseId, Long contentId);

    void deleteByContentId(Long contentId);

}
