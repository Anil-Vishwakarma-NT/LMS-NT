package com.example.course_service_lms.dto.inDTO;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * Data Transfer Object for updating an existing quiz question.
 *
 * <p>This DTO is used when updating an existing question within a quiz.
 * Note that quizId is not included as questions cannot be moved between quizzes.
 */
@Data
public class QuizQuestionUpdateInDTO {

    /**
     * The text content of the question.
     * Cannot be null or empty.
     */
    @NotBlank(message = "Question text is required")
    @Size(max = 10000, message = "Question text is too long")
    private String questionText;

    /**
     * The type of question (e.g., "SINGLE-MULTIPLE_CHOICE", "MULTI-SELECT", "TEXT").
     * Cannot be null or empty.
     */
    @NotBlank(message = "Question type is required")
    @Size(max = 20, message = "Question type must not exceed 20 characters")
    private String questionType;

    /**
     * The point value for this question.
     * Must be a positive value with up to 2 decimal places.
     */
    @NotNull(message = "Points are required")
    @DecimalMin(value = "1.00", message = "Points must be at least 1.00")
    @DecimalMax(value = "1000.00", message = "Points must not exceed 1000.00")
    @Digits(integer = 4, fraction = 2, message = "Points must have at most 4 digits before decimal and 2 after")
    private BigDecimal points;

    /**
     * Explanation or feedback for the question.
     * Optional field.
     */
    private String explanation;

    /**
     * Flag indicating whether this question is required to be answered.
     */
    @NotNull(message = "Required field is required")
    private Boolean required;

    /**
     * Position/order of the question within the quiz.
     * Must be a positive integer.
     */
    @NotNull(message = "Position is required")
    @Positive(message = "Position must be positive")
    private Integer position;
}
