package com.example.course_service_lms.dto.inDTO;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * Data Transfer Object for incoming quiz question data.
 * Used for creating and updating quiz questions.
 */
@Data
public class QuizQuestionInDTO {

    @NotNull(message = "Quiz ID is required")
    @Positive(message = "Quiz ID must be positive")
    private Long quizId;

    @NotBlank(message = "Question text is required")
    @Size(max = 5000, message = "Question text cannot exceed 5000 characters")
    private String questionText;

    @NotBlank(message = "Question type is required")
    @Size(max = 20, message = "Question type cannot exceed 20 characters")
    @Pattern(regexp = "^(MCQ_SINGLE|MCQ_MULTIPLE|SHORT_ANSWER)$",
            message = "Question type must be one of: MCQ_SINGLE, MCQ_MULTIPLE, SHORT_ANSWER")
    private String questionType;

    @Size(max = 10000, message = "Options cannot exceed 10000 characters")
    private String options; // JSON string for question options

    @NotBlank(message = "Correct answer is required")
    @Size(max = 5000, message = "Correct answer cannot exceed 5000 characters")
    private String correctAnswer; // JSON string for correct answer(s)

    @NotNull(message = "Points are required")
    @DecimalMin(value = "0.0", message = "Points cannot be negative")
    @DecimalMax(value = "999.99", message = "Points cannot exceed 999.99")
    private BigDecimal points;

    @Size(max = 5000, message = "Explanation cannot exceed 5000 characters")
    private String explanation;

    @NotNull(message = "Required field must be specified")
    private Boolean required = true;


}