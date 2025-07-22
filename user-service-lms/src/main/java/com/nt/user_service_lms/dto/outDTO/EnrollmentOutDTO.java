package com.nt.user_service_lms.dtoTest.outDTO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Enrollment output.
 * Contains enrollment details such as user, group, course, status, and timestamps.
 */
@Data
public class EnrollmentOutDTO {

    /**
     * Unique identifier for the enrollment.
     */
    private Long enrollmentId;

    /**
     * Identifier of the user associated with the enrollment.
     */
    private Long userId;

    /**
     * Identifier of the group associated with the enrollment.
     */
    private Long groupId;

    /**
     * Identifier of the course associated with the enrollment.
     */
    private Long courseId;

    /**
     * Identifier of the bundle associated with the enrollment.
     */
    private Long bundleId;

    /**
     * Identifier of the user who assigned the enrollment.
     */
    private Long assignedBy;

    /**
     * Timestamp when the enrollment was assigned.
     */
    private LocalDateTime assignedAt;

    /**
     * Deadline for the enrollment.
     */
    private LocalDateTime deadline;

    /**
     * Status of the enrollment.
     */
    private String status;

    /**
     * Source of the enrollment.
     */
    private String enrollmentSource;

    /**
     * Identifier of the parent enrollment, if any.
     */
    private Long parentEnrollmentId;

    /**
     * Timestamp when the enrollment was started.
     */
    private LocalDateTime startedAt;

    /**
     * Timestamp when the enrollment was completed.
     */
    private LocalDateTime completedAt;

    /**
     * Progress percentage of the enrollment.
     */
    private BigDecimal progressPercentage;

    /**
     * Timestamp when the enrollment was created.
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp when the enrollment was last updated.
     */
    private LocalDateTime updatedAt;

    /**
     * Indicates if the enrollment is active.
     */
    private Boolean isActive;
}
