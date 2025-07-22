package com.nt.user_service_lms.dtoTest.outDTO;


import lombok.Data;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing course deadlines.
 */
@Data
public class CourseDeadlinesDTO {

    /**
     * The title of the course.
     */
    private String title;

    /**
     * The ID of the course owner.
     */
    private Long ownerId;

    /**
     * The ID of the course.
     */
    private Long courseId;

    /**
     * The deadline for the course.
     */
    private LocalDateTime deadline;
}
