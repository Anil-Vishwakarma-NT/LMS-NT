package com.nt.LMS.dto;

import com.nt.LMS.validator.AtLeastOnePresent;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AtLeastOnePresent
public class EnrollmentDTO {

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Long userId;

    @NotNull(message = "Course ID is required")
    @Positive(message = "Course ID must be positive")
    private Long courseId;

    private Long bundleId;

    @Future(message = "Deadline must be a future date and time")
    private LocalDateTime deadline;

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Long assignedBy;

    @NotNull(message = "Please specify if the course is individually assigned")
    private Boolean isIndividualAssigned;

    @NotNull(message = "Please specify if the course is assigned through a group")
    private Boolean isGroupAssigned;
}
