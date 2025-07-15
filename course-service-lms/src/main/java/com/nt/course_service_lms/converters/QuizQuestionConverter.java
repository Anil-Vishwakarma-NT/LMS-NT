package com.nt.course_service_lms.converters;

import com.nt.course_service_lms.dto.inDTO.QuizQuestionInDTO;
import com.nt.course_service_lms.dto.outDTO.QuizQuestionOutDTO;
import com.nt.course_service_lms.entity.QuizQuestion;
import org.springframework.stereotype.Component;

/**
 * Converter class for transforming QuizQuestion entities to DTOs and vice versa.
 *
 * <p>This class provides methods to convert between QuizQuestion entity objects
 * and their corresponding Data Transfer Objects (DTOs) for API operations.
 */
@Component
public class QuizQuestionConverter {
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
     * Converts a QuizQuestionInDTO to a QuizQuestion entity.
     *
     * @param dto The QuizQuestionInDTO to convert.
     * @return A QuizQuestion entity populated with the DTO data.
     */
    public static QuizQuestion convertToEntity(final QuizQuestionInDTO dto) {
        QuizQuestion question = new QuizQuestion();
        question.setQuizId(dto.getQuizId());
        question.setQuestionText(dto.getQuestionText());
        question.setQuestionType(dto.getQuestionType());
        question.setOptions(dto.getOptions());
        question.setCorrectAnswer(dto.getCorrectAnswer());
        question.setPoints(dto.getPoints());
        question.setExplanation(dto.getExplanation());
        question.setRequired(dto.getRequired());
        //question.setPosition(dto.getPosition());
        return question;
    }

    /**
     * Converts a QuizQuestion entity to a QuizQuestionOutDTO.
     *
     * @param question The QuizQuestion entity to convert.
     * @return A QuizQuestionOutDTO containing the entity data.
     */
    public static QuizQuestionOutDTO convertToOutDTO(final QuizQuestion question) {
        return new QuizQuestionOutDTO(
                question.getQuestionId(),
                question.getQuizId(),
                question.getQuestionText(),
                question.getQuestionType(),
                question.getOptions(),
                question.getCorrectAnswer(),
                question.getPoints(),
                question.getExplanation(),
                question.getRequired(),
                question.getPosition(),
                question.getCreatedAt(),
                question.getUpdatedAt()
        );
    }
}
