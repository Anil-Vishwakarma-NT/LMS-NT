package com.nt.course_service_lms.repository;

import com.nt.course_service_lms.entity.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository interface for UserResponse entity.
 * Provides data access methods for user quiz responses.
 */
@Repository
public interface UserResponseRepository extends JpaRepository<UserResponse, Long> {

    /**
     * Check if a user response exists for given user ID, question ID, and attempt number.
     *
     * @param userId     the user ID
     * @param questionId the question ID
     * @param attempt    the attempt number
     * @return true if response exists, false otherwise
     */
    boolean existsByUserIdAndQuestionIdAndAttempt(Long userId, Long questionId, Long attempt);

    /**
     * Find all user responses by user ID.
     *
     * @param userId the user ID
     * @return list of user responses
     */
    List<UserResponse> findByUserId(Long userId);

    /**
     * Find all user responses by user ID with pagination.
     *
     * @param userId   the user ID
     * @param pageable pagination information
     * @return paginated user responses
     */
    Page<UserResponse> findByUserId(Long userId, Pageable pageable);

    /**
     * Find all user responses by quiz ID.
     *
     * @param quizId the quiz ID
     * @return list of user responses
     */
    List<UserResponse> findByQuizId(Long quizId);

    /**
     * Find all user responses by quiz ID with pagination.
     *
     * @param quizId   the quiz ID
     * @param pageable pagination information
     * @return paginated user responses
     */
    Page<UserResponse> findByQuizId(Long quizId, Pageable pageable);

    /**
     * Find all user responses by user ID and quiz ID.
     *
     * @param userId the user ID
     * @param quizId the quiz ID
     * @return list of user responses
     */
    List<UserResponse> findByUserIdAndQuizId(Long userId, Long quizId);

    /**
     * Find all user responses by user ID, quiz ID, and attempt number.
     *
     * @param userId  the user ID
     * @param quizId  the quiz ID
     * @param attempt the attempt number
     * @return list of user responses
     */
    List<UserResponse> findByUserIdAndQuizIdAndAttempt(Long userId, Long quizId, Long attempt);

    /**
     * Calculate total score for a specific user, quiz, and attempt.
     * Uses pointsEarned field instead of score field.
     *
     * @param userId  the user ID
     * @param quizId  the quiz ID
     * @param attempt the attempt number
     * @return total score or null if no responses found
     */
    @Query("SELECT SUM(ur.pointsEarned) FROM UserResponse ur "
            + "WHERE ur.userId = :userId AND ur.quizId = :quizId AND ur.attempt = :attempt")
    BigDecimal getTotalScoreByUserIdAndQuizIdAndAttempt(@Param("userId") Long userId,
                                                        @Param("quizId") Long quizId,
                                                        @Param("attempt") Long attempt);

    /**
     * Count correct answers for a specific user, quiz, and attempt.
     *
     * @param userId  the user ID
     * @param quizId  the quiz ID
     * @param attempt the attempt number
     * @return count of correct answers
     */
    @Query("SELECT COUNT(ur) FROM UserResponse ur "
            + "WHERE ur.userId = :userId AND ur.quizId = :quizId AND ur.attempt = :attempt AND ur.isCorrect = true")
    Long countCorrectAnswersByUserIdAndQuizIdAndAttempt(@Param("userId") Long userId,
                                                        @Param("quizId") Long quizId,
                                                        @Param("attempt") Long attempt);

    /**
     * Get maximum attempt number for a specific user and quiz.
     *
     * @param userId the user ID
     * @param quizId the quiz ID
     * @return maximum attempt number or null if no attempts found
     */
    @Query("SELECT MAX(ur.attempt) FROM UserResponse ur WHERE ur.userId = :userId AND ur.quizId = :quizId")
    Long getMaxAttemptByUserIdAndQuizId(@Param("userId") Long userId, @Param("quizId") Long quizId);

    // UserResponseRepository - Bulk fetch with IN clauses
    @Query("SELECT ur FROM UserResponse ur WHERE ur.userId = :userId " +
            "AND ur.quizId IN :quizIds AND ur.attempt IN :attempts")
    List<UserResponse> findByUserIdAndQuizIdInAndAttemptIn(@Param("userId") Long userId,
                                                           @Param("quizIds") List<Long> quizIds,
                                                           @Param("attempts") List<Long> attempts);
}
