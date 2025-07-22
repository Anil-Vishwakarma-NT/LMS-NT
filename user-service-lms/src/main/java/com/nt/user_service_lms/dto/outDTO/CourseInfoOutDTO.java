package com.nt.user_service_lms.dtoTest.outDTO;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for course information output.
 */
@Data
public class CourseInfoOutDTO {

    /**
     * The title of the course.
     */
    private String title;

    /**
     * The ID of the course owner.
     */
    private Long ownerId;

    /**
     * The unique ID of the course.
     */
    private Long courseId;

    /**
     * The description of the course.
     */
    private String description;

    /**
     * The level of the course.
     */
    private String courseLevel;

    /**
     * Indicates if the course is active.
     */
    private boolean isActive;

    /**
     * The last updated timestamp of the course.
     */
    private LocalDateTime updatedAt;
}
