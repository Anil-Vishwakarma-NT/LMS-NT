package com.nt.course_service_lms.dto.inDTO;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * DTO for updating user response to quiz questions.
 * Contains validation annotations for data integrity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseUpdateInDTO {

    @NotBlank(message = "User answer cannot be blank")
    @Size(max = 10000, message = "User answer cannot exceed 10000 characters")
    private String userAnswer;

    @NotNull(message = "Correct status is required")
    private Boolean isCorrect;

    @NotNull(message = "Points earned is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Points earned cannot be negative")
    @DecimalMax(value = "999.99", message = "Points earned cannot exceed 999.99")
    @Digits(integer = 3, fraction = 2, message = "Points earned must have at most 3 integer digits and 2 decimal places")
    private BigDecimal pointsEarned;

    private LocalDateTime answeredAt;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserResponseUpdateInDTO that = (UserResponseUpdateInDTO) o;
        return Objects.equals(userAnswer, that.userAnswer) && Objects.equals(isCorrect, that.isCorrect) && Objects.equals(pointsEarned, that.pointsEarned) && Objects.equals(answeredAt, that.answeredAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userAnswer, isCorrect, pointsEarned, answeredAt);
    }
}
