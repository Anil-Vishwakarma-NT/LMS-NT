package com.example.course_service_lms.service;

import com.example.course_service_lms.dto.inDTO.QuizAttemptCreateInDTO;
import com.example.course_service_lms.dto.inDTO.QuizAttemptUpdateInDTO;
import com.example.course_service_lms.dto.outDTO.QuizAttemptOutDTO;
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
    QuizAttemptOutDTO createQuizAttempt(QuizAttemptCreateInDTO dto);

    /**
     * Update an existing quiz attempt
     */
    QuizAttemptOutDTO updateQuizAttempt(Long quizAttemptId, QuizAttemptUpdateInDTO dto);

    /**
     * Get quiz attempt by ID
     */
    Optional<QuizAttemptOutDTO> getQuizAttemptById(Long quizAttemptId);

    /**
     * Get all quiz attempts with pagination
     */
    Page<QuizAttemptOutDTO> getAllQuizAttempts(Pageable pageable);

    /**
     * Get quiz attempts by user ID
     */
    List<QuizAttemptOutDTO> getQuizAttemptsByUserId(Long userId);

    /**
     * Get quiz attempts by quiz ID
     */
    List<QuizAttemptOutDTO> getQuizAttemptsByQuizId(Long quizId);

    /**
     * Get quiz attempts by user and quiz
     */
    List<QuizAttemptOutDTO> getQuizAttemptsByUserAndQuiz(Long userId, Long quizId);

    /**
     * Get quiz attempts by status
     */
    List<QuizAttemptOutDTO> getQuizAttemptsByStatus(String status);

    /**
     * Get latest attempt for a user and quiz
     */
    Optional<QuizAttemptOutDTO> getLatestAttemptByUserAndQuiz(Long userId, Long quizId);

    /**
     * Delete quiz attempt by ID
     */
    void deleteQuizAttempt(Long quizAttemptId);

    /**
     * Mark attempt as completed
     */
    QuizAttemptOutDTO completeAttempt(Long quizAttemptId, String scoreDetails);

    /**
     * Mark attempt as abandoned
     */
    QuizAttemptOutDTO abandonAttempt(Long quizAttemptId);

    /**
     * Mark attempt as timed out
     */
    QuizAttemptOutDTO timeOutAttempt(Long quizAttemptId);

    /**
     * Check if attempt exists
     */
    boolean existsById(Long quizAttemptId);

    /**
     * Count attempts by user and quiz
     */
    long countAttemptsByUserAndQuiz(Long userId, Long quizId);
}