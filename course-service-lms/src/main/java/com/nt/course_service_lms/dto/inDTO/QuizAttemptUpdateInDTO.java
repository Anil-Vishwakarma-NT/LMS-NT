package com.nt.course_service_lms.dto.inDTO;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * DTO for updating an existing quiz attempt
 */
@Data
public class QuizAttemptUpdateInDTO {

    private LocalDateTime finishedAt;

    private String scoreDetails;

    @Pattern(regexp = "IN_PROGRESS|COMPLETED|ABANDONED|TIMED_OUT",
            message = "Status must be one of: IN_PROGRESS, COMPLETED, ABANDONED, TIMED_OUT")
    private String status;

    public QuizAttemptUpdateInDTO() {
    }

    public QuizAttemptUpdateInDTO(LocalDateTime finishedAt, String scoreDetails, String status) {
        this.finishedAt = finishedAt;
        this.scoreDetails = scoreDetails;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        QuizAttemptUpdateInDTO that = (QuizAttemptUpdateInDTO) o;
        return Objects.equals(finishedAt, that.finishedAt) && Objects.equals(scoreDetails, that.scoreDetails) && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(finishedAt, scoreDetails, status);
    }
}
