package com.example.course_service_lms.service;

import com.example.course_service_lms.dto.inDTO.QuizAttemptCreateInDTO;
import com.example.course_service_lms.dto.inDTO.QuizAttemptUpdateInDTO;
import com.example.course_service_lms.entity.QuizAttempt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for QuizAttempt operations
 */
public interface QuizAttemptService {

    /**
     * Create a new quiz attempt
     */
    QuizAttempt createQuizAttempt(QuizAttemptCreateInDTO dto);

    /**
     * Update an existing quiz attempt
     */
    QuizAttempt updateQuizAttempt(Long quizAttemptId, QuizAttemptUpdateInDTO dto);

    /**
     * Get quiz attempt by ID
     */
    Optional<QuizAttempt> getQuizAttemptById(Long quizAttemptId);

    /**
     * Get all quiz attempts with pagination
     */
    Page<QuizAttempt> getAllQuizAttempts(Pageable pageable);

    /**
     * Get quiz attempts by user ID
     */
    List<QuizAttempt> getQuizAttemptsByUserId(Long userId);

    /**
     * Get quiz attempts by quiz ID
     */
    List<QuizAttempt> getQuizAttemptsByQuizId(Long quizId);

    /**
     * Get quiz attempts by user and quiz
     */
    List<QuizAttempt> getQuizAttemptsByUserAndQuiz(Long userId, Long quizId);

    /**
     * Get quiz attempts by status
     */
    List<QuizAttempt> getQuizAttemptsByStatus(String status);

    /**
     * Get latest attempt for a user and quiz
     */
    Optional<QuizAttempt> getLatestAttemptByUserAndQuiz(Long userId, Long quizId);

    /**
     * Delete quiz attempt by ID
     */
    void deleteQuizAttempt(Long quizAttemptId);

    /**
     * Mark attempt as completed
     */
    QuizAttempt completeAttempt(Long quizAttemptId, String scoreDetails);

    /**
     * Mark attempt as abandoned
     */
    QuizAttempt abandonAttempt(Long quizAttemptId);

    /**
     * Mark attempt as timed out
     */
    QuizAttempt timeOutAttempt(Long quizAttemptId);

    /**
     * Check if attempt exists
     */
    boolean existsById(Long quizAttemptId);

    /**
     * Count attempts by user and quiz
     */
    long countAttemptsByUserAndQuiz(Long userId, Long quizId);
}