package com.example.course_service_lms.dto.inDTO;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * Data Transfer Object for creating a new quiz question.
 *
 * <p>This DTO is used when creating a new question within a quiz.
 * It contains all the necessary fields required for question creation.
 */
@Data
public class QuizQuestionCreateInDTO {

    /**
     * The ID of the quiz to which this question belongs.
     * Must be a positive integer.
     */
    @NotNull(message = "Quiz ID is required")
    @Positive(message = "Quiz ID must be positive")
    private Integer quizId;

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
     * Default value is 1.0 if not provided.
     */
    @DecimalMin(value = "1.00", message = "Points must be at least 1.00")
    @DecimalMax(value = "1000.00", message = "Points must not exceed 1000.00")
    @Digits(integer = 4, fraction = 2, message = "Points must have at most 3 digits before decimal and 2 after")
    private BigDecimal points = BigDecimal.ONE;

    /**
     * Explanation or feedback for the question.
     * Optional field.
     */
    private String explanation;

    /**
     * Flag indicating whether this question is required to be answered.
     * Default value is true if not provided.
     */
    @NotNull(message = "Required field must be specified")
    private Boolean required = true;

    /**
     * Position/order of the question within the quiz.
     * Must be a positive integer.
     */
    @NotNull(message = "Position is required")
    @Positive(message = "Position must be positive")
    private Integer position;
}
