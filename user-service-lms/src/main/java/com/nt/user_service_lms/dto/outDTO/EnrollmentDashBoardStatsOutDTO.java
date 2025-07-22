package com.nt.user_service_lms.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Data Transfer Object for Enrollment Dashboard statistics.
 * Contains various enrollment counts, completion rates, and distributions.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentDashBoardStatsOutDTO {

    /**
     * Total number of enrollments.
     */
    private Long totalEnrollments;

    /**
     * Number of active enrollments.
     */
    private Long activeEnrollments;

    /**
     * Number of inactive enrollments.
     */
    private Long inactiveEnrollments;

    /**
     * Number of pending enrollments.
     */
    private Long pendingEnrollments;

    /**
     * Number of enrollments in progress.
     */
    private Long inProgressEnrollments;

    /**
     * Number of completed enrollments.
     */
    private Long completedEnrollments;

    /**
     * Number of expired enrollments.
     */
    private Long expiredEnrollments;

    /**
     * Number of individual enrollments.
     */
    private Long individualEnrollments;

    /**
     * Number of group enrollments.
     */
    private Long groupEnrollments;

    /**
     * Number of bundle enrollments.
     */
    private Long bundleEnrollments;

    /**
     * Number of group bundle enrollments.
     */
    private Long groupBundleEnrollments;

    /**
     * Total number of unique users.
     */
    private Long totalUniqueUsers;

    /**
     * Total number of unique courses.
     */
    private Long totalUniqueCourses;

    /**
     * Total number of unique bundles.
     */
    private Long totalUniqueBundles;

    /**
     * Total number of unique groups.
     */
    private Long totalUniqueGroups;

    /**
     * Overall completion rate.
     */
    private BigDecimal overallCompletionRate;

    /**
     * Completion rate for individual enrollments.
     */
    private BigDecimal individualCompletionRate;

    /**
     * Completion rate for group enrollments.
     */
    private BigDecimal groupCompletionRate;

    /**
     * Completion rate for bundle enrollments.
     */
    private BigDecimal bundleCompletionRate;

    /**
     * Number of recent enrollments in the last 30 days.
     */
    private Long recentEnrollments;

    /**
     * Number of recent completions in the last 30 days.
     */
    private Long recentCompletions;

    /**
     * Status distribution as percentages.
     */
    private Map<String, BigDecimal> statusDistribution;

    /**
     * Source distribution as percentages.
     */
    private Map<String, BigDecimal> sourceDistribution;
}
