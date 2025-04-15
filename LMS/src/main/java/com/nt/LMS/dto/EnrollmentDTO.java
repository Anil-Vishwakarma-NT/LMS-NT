package com.nt.LMS.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
public class EnrollmentDTO {

    private Long userId;
    private Long groupId;
    private Long courseId;
    private Long bundleId;
    private Long assignedBy;

    private LocalDateTime assignedAt = LocalDateTime.now();

    @NotNull(message = "Deadline is required")
    @Future(message = "Deadline must be in the future")
    private LocalDateTime deadline;

    @NotNull(message = "Status is required")
    @NotEmpty(message = "Status cannot be empty")
    private String status;

    @AssertTrue(message = "Either userId or groupId must be present, but not both")
    private boolean isUserOrGroupValid() {
        return (userId != null && groupId == null) || (userId == null && groupId != null);
    }

    @AssertTrue(message = "Either courseId or bundleId must be present, but not both")
    private boolean isCourseOrBundleValid() {
        return (courseId != null && bundleId == null) || (courseId == null && bundleId != null);
    }

    @AssertTrue(message = "All ID values must be Valid")
    private boolean areIdsValid() {
        if (userId != null && userId <= 0) return false;
        if (groupId != null && groupId <= 0) return false;
        if (courseId != null && courseId <= 0) return false;
        if (bundleId != null && bundleId <= 0) return false;
        if (assignedBy != null && assignedBy <= 0) return false;
        return true;
    }
}