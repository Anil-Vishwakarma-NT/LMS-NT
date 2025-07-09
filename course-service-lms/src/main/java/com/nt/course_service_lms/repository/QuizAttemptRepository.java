package com.nt.course_service_lms.repository;

import com.nt.course_service_lms.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for QuizAttempt entity
 */
@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    /**
     * Find quiz attempts by user ID
     */
    List<QuizAttempt> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Find quiz attempts by quiz ID
     */
    List<QuizAttempt> findByQuizIdOrderByCreatedAtDesc(Long quizId);

    /**
     * Find quiz attempts by user and quiz
     */
    List<QuizAttempt> findByUserIdAndQuizIdOrderByAttemptDesc(Long userId, Long quizId);

    /**
     * Find quiz attempts by status
     */
    List<QuizAttempt> findByStatusOrderByCreatedAtDesc(String status);

    /**
     * Find latest attempt by user and quiz
     */
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.userId = :userId AND qa.quizId = :quizId ORDER BY qa.attempt DESC LIMIT 1")
    Optional<QuizAttempt> findLatestAttemptByUserAndQuiz(@Param("userId") Long userId, @Param("quizId") Long quizId);

    /**
     * Count attempts by user and quiz
     */
    long countByUserIdAndQuizId(Long userId, Long quizId);

    /**
     * Find attempts by user and quiz with specific status
     */
    List<QuizAttempt> findByUserIdAndQuizIdAndStatus(Long userId, Long quizId, String status);

    /**
     * Check if user has any active attempts for a quiz
     */
    @Query("SELECT COUNT(qa) > 0 FROM QuizAttempt qa WHERE qa.userId = :userId AND qa.quizId = :quizId AND qa.status IN ('IN_PROGRESS')")
    boolean hasActiveAttempt(@Param("userId") Long userId, @Param("quizId") Long quizId);

    QuizAttempt findTopByUserIdAndQuizIdOrderByAttemptDesc(Long userId, Long quizId);

    /**
     * Find the single active attempt for a user and quiz
     * Since there's only one active attempt per user per quiz, this returns Optional<QuizAttempt>
     */
    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.userId = :userId AND qa.quizId = :quizId AND qa.status IN ('IN_PROGRESS')")
    Optional<QuizAttempt> findActiveAttemptByUserAndQuiz(@Param("userId") Long userId, @Param("quizId") Long quizId);
}