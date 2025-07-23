package com.nt.course_service_lms.dto.inDTO;

import com.nt.course_service_lms.constants.CommonConstants;
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
 * Data Transfer Object used for updating an existing quiz question.
 * <p>
 * This DTO includes validation annotations to ensure the integrity of data
 * sent by the client during a quiz question update operation.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateQuizQuestionInDTO {

    /**
     * The actual text or content of the quiz question.
     * <p>
     * Cannot be blank and must be within 5000 characters.
     * </p>
     */
    @NotBlank(message = "Question text is required")
    @Size(max = CommonConstants.NUMBER_FIVE_THOUSAND, message = "Question text cannot exceed 5000 characters")
    private String questionText;

    /**
     * The type of the quiz question.
     * <p>
     * Supported types: MCQ_SINGLE, MCQ_MULTIPLE, SHORT_ANSWER.
     * Must match one of the accepted values and not exceed 20 characters.
     * </p>
     */
    @NotBlank(message = "Question type is required")
    @Size(max = CommonConstants.NUMBER_TWENTY, message = "Question type cannot exceed 20 characters")
    @Pattern(
            regexp = "^(MCQ_SINGLE|MCQ_MULTIPLE|SHORT_ANSWER)$",
            message = "Question type must be one of: MULTIPLE_CHOICE, TRUE_FALSE, SHORT_ANSWER, ESSAY, FILL_IN_BLANK"
    )
    private String questionType;

    /**
     * JSON-formatted string containing the options for the question.
     * <p>
     * Applicable for multiple choice question types.
     * </p>
     */
    @Size(max = CommonConstants.NUMBER_TEN_THOUSAND, message = "Options cannot exceed 10000 characters")
    private String options;

    /**
     * JSON-formatted string representing the correct answer(s) to the question.
     * <p>
     * Cannot be blank and must be within 5000 characters.
     * </p>
     */
    @NotBlank(message = "Correct answer is required")
    @Size(max = CommonConstants.NUMBER_FIVE_THOUSAND, message = "Correct answer cannot exceed 5000 characters")
    private String correctAnswer;

    /**
     * The number of points assigned to the question.
     * <p>
     * Must be a non-null value between 0.0 and 999.99.
     * </p>
     */
    @NotNull(message = "Points are required")
    @DecimalMin(value = "0.0", message = "Points cannot be negative")
    @DecimalMax(value = "999.99", message = "Points cannot exceed 999.99")
    private BigDecimal points;

    /**
     * Explanation for the answer, shown as feedback after submission.
     * <p>
     * Optional and must not exceed 5000 characters.
     * </p>
     */
    @Size(max = CommonConstants.NUMBER_FIVE_THOUSAND, message = "Explanation cannot exceed 5000 characters")
    private String explanation;

    /**
     * Indicates whether answering the question is mandatory.
     */
    @NotNull(message = "Required field must be specified")
    private Boolean required = true;

    /**
     * Position or order of the question in the quiz.
     * <p>
     * Must be a positive integer and is required.
     * </p>
     */
    @NotNull(message = "Position is required")
    @Positive(message = "Position must be positive")
    private Integer position;

    /**
     * Checks equality based on question attributes.
     *
     * @param o the object to compare
     * @return {@code true} if the given object is equal to this DTO
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UpdateQuizQuestionInDTO that = (UpdateQuizQuestionInDTO) o;
        return Objects.equals(questionText, that.questionText)
                && Objects.equals(questionType, that.questionType)
                && Objects.equals(options, that.options)
                && Objects.equals(correctAnswer, that.correctAnswer)
                && Objects.equals(points, that.points)
                && Objects.equals(explanation, that.explanation)
                && Objects.equals(required, that.required)
                && Objects.equals(position, that.position);
    }

    /**
     * Generates hash code based on the fields of the DTO.
     *
     * @return the hash code for this DTO
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                questionText, questionType, options, correctAnswer,
                points, explanation, required, position
        );
    }
}
