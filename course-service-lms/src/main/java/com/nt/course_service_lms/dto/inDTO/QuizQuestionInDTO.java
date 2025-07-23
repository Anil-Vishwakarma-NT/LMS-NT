package com.nt.course_service_lms.dto.inDTO;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Data Transfer Object for incoming quiz question data.
 * Used for creating and updating quiz questions.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QuizQuestionInDTO that = (QuizQuestionInDTO) o;
        return Objects.equals(quizId, that.quizId) && Objects.equals(questionText, that.questionText) && Objects.equals(questionType, that.questionType) && Objects.equals(options, that.options) && Objects.equals(correctAnswer, that.correctAnswer) && Objects.equals(points, that.points) && Objects.equals(explanation, that.explanation) && Objects.equals(required, that.required);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quizId, questionText, questionType, options, correctAnswer, points, explanation, required);
    }
}