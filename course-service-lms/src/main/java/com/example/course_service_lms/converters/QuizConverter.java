package com.example.course_service_lms.converters;

import com.example.course_service_lms.dto.inDTO.QuizCreateInDTO;
import com.example.course_service_lms.dto.inDTO.QuizUpdateInDTO;
import com.example.course_service_lms.dto.outDTO.QuizOutDTO;
import com.example.course_service_lms.entity.Quiz;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Converter class for Quiz entity and DTOs.
 * Handles conversion between different representations of Quiz data.
 */
@Component
public class QuizConverter {

    /**
     * Converts QuizCreateInDTO to Quiz entity for creation.
     *
     * @param quizCreateInDTO the DTO to convert
     * @return new Quiz entity
     */
    public Quiz toEntity(QuizCreateInDTO quizCreateInDTO) {
        if (quizCreateInDTO == null) {
            return null;
        }

        Quiz quiz = new Quiz();
        quiz.setParentType(quizCreateInDTO.getParentType());
        quiz.setParentId(quizCreateInDTO.getParentId());
        quiz.setTitle(quizCreateInDTO.getTitle());
        quiz.setDescription(quizCreateInDTO.getDescription());
        quiz.setTimeLimit(quizCreateInDTO.getTimeLimit());
        quiz.setAttemptsAllowed(quizCreateInDTO.getAttemptsAllowed());
        quiz.setPassingScore(quizCreateInDTO.getPassingScore());
        if(quizCreateInDTO.getRandomizeQuestions() != null)
            quiz.setRandomizeQuestions(quizCreateInDTO.getRandomizeQuestions());
        if(quizCreateInDTO.getShowResults() != null)
            quiz.setShowResults(quizCreateInDTO.getShowResults());
        quiz.setIsActive(quizCreateInDTO.getIsActive());
        quiz.setCreatedBy(quizCreateInDTO.getCreatedBy());
        quiz.setCreatedAt(LocalDateTime.now());
        quiz.setUpdatedAt(LocalDateTime.now());

        return quiz;
    }

    /**
     * Updates existing Quiz entity with QuizUpdateInDTO data.
     *
     * @param existingQuiz the quiz to update
     * @param quizUpdateInDTO the update data
     * @return updated Quiz entity
     */
    public Quiz updateEntity(Quiz existingQuiz, QuizUpdateInDTO quizUpdateInDTO) {
        if (existingQuiz == null || quizUpdateInDTO == null) {
            return existingQuiz;
        }

        if (quizUpdateInDTO.getTitle() != null) {
            existingQuiz.setTitle(quizUpdateInDTO.getTitle());
        }
        if (quizUpdateInDTO.getDescription() != null) {
            existingQuiz.setDescription(quizUpdateInDTO.getDescription());
        }
        if (quizUpdateInDTO.getTimeLimit() != null) {
            existingQuiz.setTimeLimit(quizUpdateInDTO.getTimeLimit());
        }
        if (quizUpdateInDTO.getAttemptsAllowed() != null) {
            existingQuiz.setAttemptsAllowed(quizUpdateInDTO.getAttemptsAllowed());
        }
        if (quizUpdateInDTO.getPassingScore() != null) {
            existingQuiz.setPassingScore(quizUpdateInDTO.getPassingScore());
        }
        if (quizUpdateInDTO.getRandomizeQuestions() != null) {
            existingQuiz.setRandomizeQuestions(quizUpdateInDTO.getRandomizeQuestions());
        }
        if (quizUpdateInDTO.getShowResults() != null) {
            existingQuiz.setShowResults(quizUpdateInDTO.getShowResults());
        }
        if (quizUpdateInDTO.getIsActive() != null) {
            existingQuiz.setIsActive(quizUpdateInDTO.getIsActive());
        }

        existingQuiz.setUpdatedAt(LocalDateTime.now());

        return existingQuiz;
    }

    /**
     * Converts Quiz entity to QuizOutDTO for API responses.
     *
     * @param quiz the Quiz entity to convert
     * @return the converted QuizOutDTO
     */
    public QuizOutDTO toOutDTO(Quiz quiz) {
        if (quiz == null) {
            return null;
        }

        QuizOutDTO quizOutDTO = new QuizOutDTO();
        quizOutDTO.setQuizId(quiz.getQuizId());
        quizOutDTO.setParentType(quiz.getParentType());
        quizOutDTO.setParentId(quiz.getParentId());
        quizOutDTO.setTitle(quiz.getTitle());
        quizOutDTO.setDescription(quiz.getDescription());
        quizOutDTO.setTimeLimit(quiz.getTimeLimit());
        quizOutDTO.setAttemptsAllowed(quiz.getAttemptsAllowed());
        quizOutDTO.setPassingScore(quiz.getPassingScore());
        quizOutDTO.setRandomizeQuestions(quiz.getRandomizeQuestions());
        quizOutDTO.setShowResults(quiz.getShowResults());
        quizOutDTO.setIsActive(quiz.getIsActive());
        quizOutDTO.setCreatedBy(quiz.getCreatedBy());
        quizOutDTO.setCreatedAt(quiz.getCreatedAt());
        quizOutDTO.setUpdatedAt(quiz.getUpdatedAt());

        return quizOutDTO;
    }
}