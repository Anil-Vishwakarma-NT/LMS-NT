package com.nt.course_service_lms.dto.inDTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * DTO for creating a new quiz attempt
 */
@Data
public class QuizAttemptCreateInDTO {

    @NotNull(message = "Quiz ID is required")
    @Positive(message = "Quiz ID must be positive")
    private Long quizId;

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Long userId;
}
