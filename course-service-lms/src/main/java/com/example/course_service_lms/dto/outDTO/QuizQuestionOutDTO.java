package com.example.course_service_lms.dto.outDTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for quiz question output.
 *
 * <p>This DTO is used to return question information to the client.
 * It contains all the question details that should be exposed to the frontend.
 */
@Data
public class QuizQuestionOutDTO {

    /**
     * The unique identifier of the question.
     */
    private Integer questionId;

    /**
     * The ID of the quiz to which this question belongs.
     */
    private Integer quizId;

    /**
     * The text content of the question.
     */
    private String questionText;

    /**
     * The type of question (e.g., "SINGLE-MULTIPLE_CHOICE", "MULTI-SELECT", "TEXT").
     */
    private String questionType;

    /**
     * The point value for this question.
     */
    private BigDecimal points;

    /**
     * Explanation or feedback for the question.
     */
    private String explanation;

    /**
     * Flag indicating whether this question is required to be answered.
     */
    private Boolean required;

    /**
     * Position/order of the question within the quiz.
     */
    private Integer position;

    /**
     * Timestamp when the question was created.
     */
    private LocalDateTime createdAt;
}
