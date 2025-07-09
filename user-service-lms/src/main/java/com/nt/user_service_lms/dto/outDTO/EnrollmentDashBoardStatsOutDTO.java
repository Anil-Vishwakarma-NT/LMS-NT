package com.nt.user_service_lms.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentDashBoardStatsOutDTO {

    // Total counts
    private Long totalEnrollments;
    private Long activeEnrollments;
    private Long inactiveEnrollments;

    // Status-based counts
    private Long pendingEnrollments;
    private Long inProgressEnrollments;
    private Long completedEnrollments;
    private Long expiredEnrollments;

    // Source-based counts
    private Long individualEnrollments;
    private Long groupEnrollments;
    private Long bundleEnrollments;
    private Long groupBundleEnrollments;

    // User engagement
    private Long totalUniqueUsers;
    private Long totalUniqueCourses;
    private Long totalUniqueBundles;
    private Long totalUniqueGroups;

    // Completion rates
    private BigDecimal overallCompletionRate;
    private BigDecimal individualCompletionRate;
    private BigDecimal groupCompletionRate;
    private BigDecimal bundleCompletionRate;

    // Recent activity (last 30 days)
    private Long recentEnrollments;
    private Long recentCompletions;

    // Status distribution as percentages
    private Map<String, BigDecimal> statusDistribution;

    // Source distribution as percentages
    private Map<String, BigDecimal> sourceDistribution;
}