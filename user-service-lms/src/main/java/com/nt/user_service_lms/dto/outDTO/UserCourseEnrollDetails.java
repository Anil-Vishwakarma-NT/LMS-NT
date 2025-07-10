package com.nt.user_service_lms.dto.outDTO;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO representing user course enrollment details.
 */
@Data
public class UserCourseEnrollDetails {
    /**
     * The ID of the course.
     */
    private Long courseId;

    /**
     * The ID of the user who assigned the course.
     */
    private Long assignedById;

    /**
     * The date and time when the enrollment occurred.
     */
    private LocalDateTime enrollmentDate;

    /**
     * The deadline for the course enrollment.
     */
    private LocalDateTime deadline;
}
