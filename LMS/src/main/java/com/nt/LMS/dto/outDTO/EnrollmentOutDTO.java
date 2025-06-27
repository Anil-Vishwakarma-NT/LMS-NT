package com.nt.LMS.dto.outDTO;

import jakarta.persistence.Column;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EnrollmentOutDTO {
    private Long enrollmentId;

    private Long userId;

    private Long groupId;

    private Long courseId;

    private Long bundleId;

    private Long assignedBy;

    private LocalDateTime assignedAt;

    private LocalDateTime deadline;

    private String status;

    private String enrollmentSource;

    private Long parentEnrollmentId;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    private BigDecimal progressPercentage;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Boolean isActive;
}
