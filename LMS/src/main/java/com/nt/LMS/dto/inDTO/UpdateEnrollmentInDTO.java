package com.nt.LMS.dto.inDTO;

import com.nt.LMS.validation.ValidateEnrollmentDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ValidateEnrollmentDTO
public class UpdateEnrollmentInDTO {
    private Long userId;
    private Long groupId;
    private Long courseId;
    private Long bundleId;
    @NotNull(message = "Manager ID cannot be empty")
    @Positive(message = "Valid manager ID required")
    private Long managerId;
    private String status;
    private LocalDateTime deadline;
}
