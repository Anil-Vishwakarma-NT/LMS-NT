package com.example.course_service_lms.dto.inDTO;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO for updating an existing quiz attempt
 */
@Data
public class QuizAttemptUpdateInDTO {

    private LocalDateTime finishedAt;

    private String scoreDetails;

    @Pattern(regexp = "STARTED|IN_PROGRESS|COMPLETED|ABANDONED|TIMED_OUT",
            message = "Status must be one of: STARTED, IN_PROGRESS, COMPLETED, ABANDONED, TIMED_OUT")
    private String status;
}
