package com.nt.course_service_lms.repository;

import com.nt.course_service_lms.entity.QuizAttempt;
import com.nt.course_service_lms.entity.QuizQuestion;
import com.nt.course_service_lms.entity.UserResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for QuizAttempt entity.
 */
@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    /**
     * Find quiz attempts by user ID.
     *
     * @param userId the ID of the user
     * @return a list of quiz attempts ordered by creation date in descending order
     */
    List<QuizAttempt> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Find quiz attempts by quiz ID.
     *
     * @param quizId the ID of the quiz
     * @return a list of quiz attempts ordered by creation date in descending order
     */
    List<QuizAttempt> findByQuizIdOrderByCreatedAtDesc(Long quizId);

    /**
     * Find quiz attempts by user and quiz.
     *
     * @param userId the ID of the user
     * @param quizId the ID of the quiz
     * @return a list of quiz attempts ordered by attempt number in descending order
     */
    List<QuizAttempt> findByUserIdAndQuizIdOrderByAttemptDesc(Long userId, Long quizId);

    /**
     * Find quiz attempts by status.
     *
     * @param status the status of the quiz attempt (e.g., "IN_PROGRESS", "COMPLETED", etc.)
     * @return a list of quiz attempts with the specified status, ordered by creation date in descending order
     */
    List<QuizAttempt> findByStatusOrderByCreatedAtDesc(String status);

    /**
     * Find latest attempt by user and quiz.
     *
     * @param userId the ID of the user
     * @param quizId the ID of the quiz
     * @return an Optional containing the latest QuizAttempt for the user and quiz, or empty if not found
     */
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.userId = :userId AND qa.quizId = :quizId ORDER BY qa.attempt DESC LIMIT 1")
    Optional<QuizAttempt> findLatestAttemptByUserAndQuiz(@Param("userId") Long userId, @Param("quizId") Long quizId);

    /**
     * Count attempts by user and quiz.
     *
     * @param userId the ID of the user
     * @param quizId the ID of the quiz
     * @return the count of quiz attempts for the specified user and quiz
     */
    long countByUserIdAndQuizId(Long userId, Long quizId);

    /**
     * Find attempts by user and quiz with specific status.
     *
     * @param userId the ID of the user
     * @param quizId the ID of the quiz
     * @param status the status of the quiz attempt (e.g., "IN_PROGRESS", "COMPLETED", etc.)
     * @return a list of quiz attempts matching the user ID, quiz ID, and status
     */
    List<QuizAttempt> findByUserIdAndQuizIdAndStatus(Long userId, Long quizId, String status);

    /**
     * Check if user has any active attempts for a quiz.
     *
     * @param userId the ID of the user
     * @param quizId the ID of the quiz
     * @return true if there is at least one active attempt, false otherwise
     */
    @Query("SELECT COUNT(qa) > 0 FROM QuizAttempt"
            + " qa WHERE qa.userId = :userId AND qa.quizId = :quizId AND qa.status IN ('IN_PROGRESS')")
    boolean hasActiveAttempt(@Param("userId") Long userId, @Param("quizId") Long quizId);

    /**
     * Find the latest attempt for a user and quiz.
     *
     * @param userId the ID of the user
     * @param quizId the ID of the quiz
     * @return the latest QuizAttempt for the user and quiz
     */
    QuizAttempt findTopByUserIdAndQuizIdOrderByAttemptDesc(Long userId, Long quizId);

    /**
     * Find the single active attempt for a user and quiz.
     *
     * @param userId the ID of the user
     * @param quizId the ID of the quiz
     * @return an Optional containing the active QuizAttempt if found, or empty if not
     */
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.userId = :userId AND qa.quizId = :quizId AND qa.status IN ('IN_PROGRESS')")
    Optional<QuizAttempt> findActiveAttemptByUserAndQuiz(@Param("userId") Long userId, @Param("quizId") Long quizId);

    // QuizAttemptRepository - Custom query with JOIN
    @Query(value = """
    SELECT qa.quiz_attempt_id, qa.attempt, qa.quiz_id, qa.started_at, qa.finished_at, 
           qa.score_details, qa.status, qa.created_at, qa.updated_at,
           q.title, q.description, q.time_limit, q.attempts_allowed, q.passing_score
    FROM quiz_attempt qa 
    JOIN quiz q ON qa.quiz_id = q.quiz_id 
    WHERE qa.user_id = :userId 
      AND q.parent_type = 'course' 
      AND q.parent_id = :courseId 
      AND q.is_active = true
    ORDER BY qa.attempt DESC
    """, nativeQuery = true)
    List<Object[]> findUserAttemptDetailsWithQuizInfo(@Param("userId") Long userId,
                                                      @Param("courseId") Long courseId);




}
