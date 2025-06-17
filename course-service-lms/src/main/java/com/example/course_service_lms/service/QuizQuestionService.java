package com.example.course_service_lms.service;

import com.example.course_service_lms.dto.inDTO.QuizQuestionCreateInDTO;
import com.example.course_service_lms.dto.inDTO.QuizQuestionUpdateInDTO;
import com.example.course_service_lms.dto.outDTO.QuizQuestionOutDTO;

import java.util.List;

/**
 * Service interface for quiz question operations.
 *
 * <p>This interface defines the contract for managing quiz questions including
 * creation, retrieval, updating, and deletion operations.</p>
 */
public interface QuizQuestionService {

    /**
     * Creates a new quiz question.
     *
     * @param questionCreateInDTO The DTO containing question creation data
     * @return The created question as an output DTO
     */
    QuizQuestionOutDTO createQuestion(QuizQuestionCreateInDTO questionCreateInDTO);

    /**
     * Retrieves all questions for a specific quiz.
     *
     * @param quizId The ID of the quiz
     * @return List of questions for the specified quiz
     */
    List<QuizQuestionOutDTO> getAllQuestionsByQuizId(Integer quizId);

    /**
     * Retrieves a specific question by its ID.
     *
     * @param questionId The ID of the question to retrieve
     * @return The question as an output DTO
     */
    QuizQuestionOutDTO getQuestionById(Integer questionId);

    /**
     * Updates an existing quiz question.
     *
     * @param questionId The ID of the question to update
     * @param questionUpdateInDTO The DTO containing updated question data
     * @return The updated question as an output DTO
     */
    QuizQuestionOutDTO updateQuestion(Integer questionId, QuizQuestionUpdateInDTO questionUpdateInDTO);

    /**
     * Deletes a quiz question by its ID.
     *
     * @param questionId The ID of the question to delete
     */
    void deleteQuestion(Integer questionId);
}
