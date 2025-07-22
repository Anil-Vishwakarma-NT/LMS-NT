package com.nt.user_service_lms.dtoTest.outDTO;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing enrolled courses for a user.
 */
@Data
public class EnrolledCoursesOutDTO {

    /**
     * The unique identifier of the course.
     */
    private Long courseId;

    /**
     * The name of the course.
     */
    private String courseName;

    /**
     * The progress of the user in the course.
     */
    private Float progress;

    /**
     * The date and time when the user enrolled in the course.
     */
    private LocalDateTime enrollmentDate;

    /**
     * The deadline for the course enrollment.
     */
    private LocalDateTime deadline;
}
