package com.nt.user_service_lms.dto.outDTO;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO representing user enrollment details for a course.
 */
@Data
public class UserEnrollDetailsOutDTO {
    /**
     * The unique identifier of the course.
     */
    private Long courseId;

    /**
     * The unique identifier of the user who assigned the course.
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





