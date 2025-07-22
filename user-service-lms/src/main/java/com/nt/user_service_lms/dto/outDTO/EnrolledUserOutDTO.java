package com.nt.user_service_lms.dto.outDTO;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing an enrolled user with progress and enrollment details.
 */
@Data
public class EnrolledUserOutDTO {

    /**
     * The unique identifier of the user.
     */
    private Long userId;

    /**
     * The name of the person who assigned the enrollment.
     */
    private String assignedByName;

    /**
     * The name of the enrolled user.
     */
    private String userName;

    /**
     * The progress of the user in the course.
     */
    private Double progress;

    /**
     * The date and time when the user was enrolled.
     */
    private LocalDateTime enrollmentDate;

    /**
     * The deadline for the user's enrollment.
     */
    private LocalDateTime deadline;
}
