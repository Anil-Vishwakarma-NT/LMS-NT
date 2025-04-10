package com.nt.LMS.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EnrollmentDTO {

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Long userId;

    @NotNull(message = "Course ID is required")
    @Positive(message = "Course ID must be positive")
    private Long courseId;

    private Long bundleId;

    @NotNull(message = "Enrollment date and time is required")
    private LocalDateTime enrolledAt;

    @Future(message = "Deadline must be a future date and time")
    private LocalDateTime deadline;

    private boolean isEnrolled = true;
}
