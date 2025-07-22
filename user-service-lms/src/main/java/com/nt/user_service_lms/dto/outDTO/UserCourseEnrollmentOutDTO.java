package com.nt.user_service_lms.dto.outDTO;

import lombok.Data;

import java.util.List;

/**
 * Data Transfer Object for user course enrollment output.
 */
@Data
public class UserCourseEnrollmentOutDTO {
    /**
     * The unique identifier of the course.
     */
    private Long courseId;

    /**
     * The unique identifier of the course owner.
     */
    private Long ownerId;

    /**
     * The number of individual enrollments in the course.
     */
    private Long individualEnrollments;

    /**
     * The name of the course.
     */
    private String courseName;

    /**
     * The name of the course owner.
     */
    private String ownerName;

    /**
     * Indicates if the course is active.
     */
    private boolean isActive;

    /**
     * List of enrolled user output DTOs.
     */
    private List<EnrolledUserOutDTO> enrolledUserOutDTOList;
}
