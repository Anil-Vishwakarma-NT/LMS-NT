package com.nt.user_service_lms.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing statistics for the Admin Dashboard.
 * <p>
 * This DTO includes aggregated counts for users, groups, courses, bundles,
 * and total enrollments within the LMS system.
 * </p>
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AdminDashboardStatsOutDTO {

    /**
     * Total number of users registered in the system.
     */
    private Long userCount;

    /**
     * Total number of groups created in the system.
     */
    private Long groupCount;

    /**
     * Total number of courses available in the system.
     */
    private Long courseCount;

    /**
     * Total number of course bundles available in the system.
     */
    private Long bundleCount;

    /**
     * Total number of course enrollments made by users.
     */
    private Long totalEnrollments;
}
