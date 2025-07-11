package com.nt.user_service_lms.dto.outDTO;

import lombok.Data;

import java.util.List;

/**
 * Data Transfer Object representing user enrollments and related statistics.
 */
@Data
public class UserEnrollmentsOutDTO {

    /**
     * The unique identifier of the user.
     */
    private Long userId;

    /**
     * The name of the user.
     */
    private String userName;

    /**
     * The number of course enrollments for the user.
     */
    private Long courseEnrollments;

    /**
     * The number of bundle enrollments for the user.
     */
    private Long bundleEnrollments;

    /**
     * The total number of courses available to the user.
     */
    private Long totalCourses;

    /**
     * The average completion percentage of the user's courses.
     */
    private Float averageCompletion;

    /**
     * The number of upcoming deadlines for the user.
     */
    private Integer upcomingDeadlines;

    /**
     * The status of the user's enrollments.
     */
    private boolean status;

    /**
     * The list of enrolled courses for the user.
     */
    private List<EnrolledCoursesOutDTO> enrolledCoursesList;

    /**
     * The list of enrolled bundles for the user.
     */
    private List<EnrolledBundlesOutDTO> enrolledBundlesList;
}
