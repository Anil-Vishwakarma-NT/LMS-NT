package com.nt.course_service_lms.dto.inDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nt.course_service_lms.constants.CommonConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Input DTO for UserResponse operations.
 * <p>
 * This DTO is used for creating and updating user responses to quiz questions.
 * It includes validation annotations to ensure data integrity and proper format.
 * The userAnswer field accepts JSON format to support various question types
 * (multiple choice, text, etc.).
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseInDTO {

    /**
     * The ID of the user who submitted the response.
     * Must be a positive number.
     */
    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be a positive number")
    private Long userId;

    /**
     * The ID of the quiz being attempted.
     * Must be a positive number.
     */
    @NotNull(message = "Quiz ID is required")
    @Positive(message = "Quiz ID must be a positive number")
    private Long quizId;

    /**
     * The ID of the specific question being answered.
     * Must be a positive number.
     */
    @NotNull(message = "Question ID is required")
    @Positive(message = "Question ID must be a positive number")
    private Long questionId;

    /**
     * The attempt number for this quiz by the user.
     * Must be a positive number starting from 1.
     */
    @NotNull(message = "Attempt number is required")
    @Positive(message = "Attempt number must be a positive number")
    private Long attempt;

    /**
     * The user's answer in JSON format.
     * Supports various answer types like single choice, multiple choice, text, etc.
     * Cannot be null or empty.
     */
    @NotBlank(message = "User answer is required")
    @Size(max = CommonConstants.NUMBER_TWO_HUNDRED, message = "User answer cannot exceed 200 characters")
    private String userAnswer;

    /**
     * Timestamp when the answer was submitted.
     * If not provided, will be set to current time during processing.
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime answeredAt;

    /**
     * Indicates whether this object is equal to another object.
     *
     * @param o the other object to compare with
     * @return {@code true} if the objects are equal; {@code false} otherwise
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserResponseInDTO that = (UserResponseInDTO) o;
        return Objects.equals(userId, that.userId)
                && Objects.equals(quizId, that.quizId)
                && Objects.equals(questionId, that.questionId)
                && Objects.equals(attempt, that.attempt)
                && Objects.equals(userAnswer, that.userAnswer)
                && Objects.equals(answeredAt, that.answeredAt);
    }

    /**
     * Computes the hash code for this object based on its fields.
     *
     * @return the computed hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(userId, quizId, questionId, attempt, userAnswer, answeredAt);
    }

}
