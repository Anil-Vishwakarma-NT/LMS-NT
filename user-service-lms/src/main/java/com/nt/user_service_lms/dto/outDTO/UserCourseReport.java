package com.nt.user_service_lms.dto.outDTO;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO representing a report of a user's course progress and assignment details.
 */
@Data
public class UserCourseReport {

    /**
     * Name of the course.
     */
    private String courseName;

    /**
     * Description of the course.
     */
    private String courseDescription;

    /**
     * Level of the course (e.g., Beginner, Intermediate, Advanced).
     */
    private String courseLevel;

    /**
     * Percentage of the course completed by the user.
     */
    private Double courseCompletionPercentage;

    /**
     * The last time the user viewed the course.
     */
    private LocalDateTime lastViewed;

    /**
     * Name of the person who assigned the course.
     */
    private String assignedBy;

    /**
     * Date and time when the course was assigned.
     */
    private LocalDateTime assignedAt;

    /**
     * Deadline for course completion.
     */
    private LocalDateTime deadline;

    /**
     * Current status of the course for the user.
     */
    private String status;

    /**
     * Date and time when the course was first completed.
     */
    private LocalDateTime firstCompletedAt;
}
