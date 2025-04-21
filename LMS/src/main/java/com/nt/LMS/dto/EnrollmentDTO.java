package com.nt.LMS.dto;

import com.nt.LMS.validation.ValidateEnrollmentDTO;
import lombok.Data;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@ValidateEnrollmentDTO
public class EnrollmentDTO {

    private Long userId;
    private Long groupId;
    private Long courseId;
    private Long bundleId;
    private Long assignedBy;

    private LocalDateTime assignedAt = LocalDateTime.now();

    private LocalDateTime deadline;
}