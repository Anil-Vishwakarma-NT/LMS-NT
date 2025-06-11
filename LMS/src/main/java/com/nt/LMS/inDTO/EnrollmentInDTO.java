package com.nt.LMS.inDTO;

import com.nt.LMS.validation.ValidateEnrollmentDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ValidateEnrollmentDTO
public class EnrollmentInDTO {

    private Long userId;
    private Long groupId;
    private Long courseId;
    private Long bundleId;
    private Long assignedBy;

    private LocalDateTime assignedAt = LocalDateTime.now();

    private LocalDateTime deadline;
}