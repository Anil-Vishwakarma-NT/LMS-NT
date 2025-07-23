package com.nt.course_service_lms.dto.inDTO;

import com.nt.course_service_lms.constants.CommonConstants;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Data Transfer Object (DTO) for updating a user's response to a quiz question.
 * <p>
 * This class encapsulates the user-submitted answer, its correctness,
 * the points earned, and the timestamp of when the answer was submitted.
 * It includes validation annotations to ensure the integrity of the input data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseUpdateInDTO {

    /**
     * The user's answer to the quiz question.
     * <p>
     * This field must not be blank and must not exceed 200 characters.
     */
    @NotBlank(message = "User answer cannot be blank")
    @Size(max = CommonConstants.NUMBER_TWO_HUNDRED, message = "User answer cannot exceed 200 characters")
    private String userAnswer;

    /**
     * Indicates whether the user's answer is correct.
     * <p>
     * This field must not be null.
     */
    @NotNull(message = "Correct status is required")
    private Boolean isCorrect;

    /**
     * The number of points earned for the user's answer.
     * <p>
     * This field must not be null and must be a non-negative number
     * with up to 3 integer digits and 2 decimal places.
     */
    @NotNull(message = "Points earned is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Points earned cannot be negative")
    @DecimalMax(value = "999.99", message = "Points earned cannot exceed 999.99")
    @Digits(integer = CommonConstants.NUMBER_THREE, fraction = 2,
            message = "Points earned must have at most 3 integer digits and 2 decimal places")
    private BigDecimal pointsEarned;

    /**
     * The date and time when the answer was submitted.
     * <p>
     * This field may be null if the timestamp is not required at the time of submission.
     */
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
        UserResponseUpdateInDTO that = (UserResponseUpdateInDTO) o;
        return Objects.equals(userAnswer, that.userAnswer)
                && Objects.equals(isCorrect, that.isCorrect)
                && Objects.equals(pointsEarned, that.pointsEarned)
                && Objects.equals(answeredAt, that.answeredAt);
    }

    /**
     * Computes the hash code for this object based on its fields.
     *
     * @return the computed hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(userAnswer, isCorrect, pointsEarned, answeredAt);
    }
}
