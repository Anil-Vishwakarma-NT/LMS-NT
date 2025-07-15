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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateQuizQuestionInDTO {
    @NotBlank(message = "Question text is required")
    @Size(max = 5000, message = "Question text cannot exceed 5000 characters")
    private String questionText;

    @NotBlank(message = "Question type is required")
    @Size(max = 20, message = "Question type cannot exceed 20 characters")
    @Pattern(regexp = "^(MCQ_SINGLE|MCQ_MULTIPLE|SHORT_ANSWER)$",
            message = "Question type must be one of: MULTIPLE_CHOICE, TRUE_FALSE, SHORT_ANSWER, ESSAY, FILL_IN_BLANK")
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

    @NotNull(message = "Position is required")
    @Positive(message = "Position must be positive")
    private Integer position;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UpdateQuizQuestionInDTO that = (UpdateQuizQuestionInDTO) o;
        return Objects.equals(questionText, that.questionText) && Objects.equals(questionType, that.questionType) && Objects.equals(options, that.options) && Objects.equals(correctAnswer, that.correctAnswer) && Objects.equals(points, that.points) && Objects.equals(explanation, that.explanation) && Objects.equals(required, that.required) && Objects.equals(position, that.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionText, questionType, options, correctAnswer, points, explanation, required, position);
    }
}
