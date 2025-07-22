package com.nt.user_service_lms.dto.outDTO;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for group course enrollment details.
 */
@Data
public class GroupCourseEnrollmentOutDTO {

    /**
     * The unique identifier of the group.
     */
    private Long groupId;

    /**
     * The unique identifier of the course.
     */
    private Long courseId;

    /**
     * The unique identifier of the user who assigned the course.
     */
    private Long assignedById;

    /**
     * The name of the group.
     */
    private String groupName;

    /**
     * The name of the course.
     */
    private String courseName;

    /**
     * The name of the user who assigned the course.
     */
    private String assignedByName;

    /**
     * The deadline for the course enrollment.
     */
    private LocalDateTime deadline;
}
