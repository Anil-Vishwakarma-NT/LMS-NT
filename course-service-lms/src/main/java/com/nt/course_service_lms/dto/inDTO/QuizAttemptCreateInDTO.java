package com.nt.course_service_lms.dto.inDTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * DTO for creating a new quiz attempt
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAttemptCreateInDTO {

    @NotNull(message = "Quiz ID is required")
    @Positive(message = "Quiz ID must be positive")
    private Long quizId;

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Long userId;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        QuizAttemptCreateInDTO that = (QuizAttemptCreateInDTO) o;
        return Objects.equals(quizId, that.quizId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quizId, userId);
    }
}
