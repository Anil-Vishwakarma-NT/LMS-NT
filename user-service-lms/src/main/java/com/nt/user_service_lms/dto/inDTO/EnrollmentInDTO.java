package com.nt.user_service_lms.dto.inDTO;

import com.nt.user_service_lms.validation.ValidateEnrollmentDTO;
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

    private LocalDateTime deadline;
}