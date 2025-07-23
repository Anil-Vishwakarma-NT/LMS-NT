package com.nt.course_service_lms.service;

import com.nt.course_service_lms.dto.inDTO.QuizQuestionInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateQuizQuestionInDTO;
import com.nt.course_service_lms.dto.outDTO.QuizQuestionOutDTO;

import java.util.List;

/**
 * Service interface for managing quiz questions.
 */
public interface QuizQuestionService {

    /**
     * Creates a new quiz question.
     *
     * @param questionInDTO the question data to create
     * @return the created question as QuizQuestionOutDTO
     */
    QuizQuestionOutDTO createQuestion(QuizQuestionInDTO questionInDTO);

    /**
     * Retrieves all quiz questions.
     *
     * @return list of all questions
     */
    List<QuizQuestionOutDTO> getAllQuestions();

    /**
     * Retrieves all questions for a specific quiz.
     *
     * @param quizId the quiz ID
     * @return list of questions for the quiz
     */
    List<QuizQuestionOutDTO> getQuestionsByQuizId(Long quizId);

    /**
     * Retrieves a specific question by ID.
     *
     * @param questionId the question ID
     * @return the question details
     */
    QuizQuestionOutDTO getQuestionById(Long questionId);

    /**
     * Updates an existing quiz question.
     *
     * @param questionId              the question ID to update
     * @param updateQuizQuestionInDTO the updated question data
     * @return the updated question as QuizQuestionOutDTO
     */
    QuizQuestionOutDTO updateQuestion(Long questionId, UpdateQuizQuestionInDTO updateQuizQuestionInDTO);

    /**
     * Deletes a quiz question.
     *
     * @param questionId the question ID to delete
     */
    void deleteQuestion(Long questionId);
}
