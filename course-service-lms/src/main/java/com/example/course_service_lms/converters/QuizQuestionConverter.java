package com.example.course_service_lms.converters;

import com.example.course_service_lms.dto.inDTO.QuizQuestionCreateInDTO;
import com.example.course_service_lms.dto.inDTO.QuizQuestionUpdateInDTO;
import com.example.course_service_lms.dto.outDTO.QuizQuestionOutDTO;
import com.example.course_service_lms.entity.QuizQuestion;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Converter class for transforming QuizQuestion entities to DTOs and vice versa.
 *
 * <p>This class provides methods to convert between QuizQuestion entity objects
 * and their corresponding Data Transfer Objects (DTOs) for API operations.
 */
@Component
public class QuizQuestionConverter {

    /**
     * Converts a QuestionCreateInDTO to a QuizQuestion entity for creation.
     *
     * @param questionCreateInDTO The input DTO containing question creation data.
     * @return A new QuizQuestion entity with data from the DTO.
     */
    public QuizQuestion convertCreateInDTOToEntity(final QuizQuestionCreateInDTO questionCreateInDTO) {
        if (questionCreateInDTO == null) {
            return null;
        }

        QuizQuestion question = new QuizQuestion();
        question.setQuizId(questionCreateInDTO.getQuizId());
        question.setQuestionText(questionCreateInDTO.getQuestionText());
        question.setQuestionType(questionCreateInDTO.getQuestionType());
        question.setPoints(questionCreateInDTO.getPoints());
        question.setExplanation(questionCreateInDTO.getExplanation());
        question.setRequired(questionCreateInDTO.getRequired());
        question.setPosition(questionCreateInDTO.getPosition());
        question.setCreatedAt(LocalDateTime.now());

        return question;
    }

    /**
     * Updates an existing QuizQuestion entity with data from QuestionUpdateInDTO.
     *
     * @param existingQuestion The existing QuizQuestion entity to update.
     * @param questionUpdateInDTO The input DTO containing updated question data.
     * @return The updated QuizQuestion entity.
     */
    public QuizQuestion updateEntityFromUpdateInDTO(final QuizQuestion existingQuestion, final QuizQuestionUpdateInDTO questionUpdateInDTO) {
        if (existingQuestion == null || questionUpdateInDTO == null) {
            return existingQuestion;
        }

        existingQuestion.setQuestionText(questionUpdateInDTO.getQuestionText());
        existingQuestion.setQuestionType(questionUpdateInDTO.getQuestionType());
        existingQuestion.setPoints(questionUpdateInDTO.getPoints());
        existingQuestion.setExplanation(questionUpdateInDTO.getExplanation());
        existingQuestion.setRequired(questionUpdateInDTO.getRequired());
        existingQuestion.setPosition(questionUpdateInDTO.getPosition());

        return existingQuestion;
    }

    /**
     * Converts a QuizQuestion entity to a QuestionOutDTO.
     *
     * @param question The QuizQuestion entity to convert.
     * @return A QuestionOutDTO containing the entity data.
     */
    public QuizQuestionOutDTO convertEntityToOutDTO(final QuizQuestion question) {
        if (question == null) {
            return null;
        }

        QuizQuestionOutDTO questionOutDTO = new QuizQuestionOutDTO();
        questionOutDTO.setQuestionId(question.getQuestionId());
        questionOutDTO.setQuizId(question.getQuizId());
        questionOutDTO.setQuestionText(question.getQuestionText());
        questionOutDTO.setQuestionType(question.getQuestionType());
        questionOutDTO.setPoints(question.getPoints());
        questionOutDTO.setExplanation(question.getExplanation());
        questionOutDTO.setRequired(question.getRequired());
        questionOutDTO.setPosition(question.getPosition());
        questionOutDTO.setCreatedAt(question.getCreatedAt());

        return questionOutDTO;
    }

    /**
     * Converts a list of QuizQuestion entities to a list of QuestionOutDTOs.
     *
     * @param questions The list of QuizQuestion entities to convert.
     * @return A list of QuestionOutDTOs.
     */
    public List<QuizQuestionOutDTO> convertEntityListToOutDTOList(final List<QuizQuestion> questions) {
        if (questions == null) {
            return null;
        }

        return questions.stream()
                .map(this::convertEntityToOutDTO)
                .collect(Collectors.toList());
    }
}
