package com.nt.user_service_lms.dtoTest.outDTO;

import lombok.Data;

import java.util.List;

/**
 * Data Transfer Object representing the enrollment details of a user in a bundle.
 */
@Data
public class UserBundleEnrollmentOutDTO {

    /**
     * The unique identifier of the bundle.
     */
    private Long bundleId;

    /**
     * The name of the bundle.
     */
    private String bundleName;

    /**
     * The total number of courses in the bundle.
     */
    private Long totalCourses;

    /**
     * The number of individual enrollments in the bundle.
     */
    private Long individualEnrollments;

    /**
     * The average completion percentage of the bundle.
     */
    private Float averageCompletion;

    /**
     * Indicates if the bundle is active.
     */
    private boolean isActive;

    /**
     * The list of enrolled users in the bundle.
     */
    private List<EnrolledUserOutDTO> enrolledUserOutDTOList;
}
