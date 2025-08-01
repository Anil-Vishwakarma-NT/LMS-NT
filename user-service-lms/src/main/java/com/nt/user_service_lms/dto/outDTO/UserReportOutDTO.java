package com.nt.user_service_lms.dto.outDTO;

import lombok.Data;

@Data
public class UserReportOutDTO {

    private Long userId;
    private String fullName;
    private String email;
    private String role;
    private String manager;

    private Integer individualEnrollments;
    private Double individualAvgCompletionPercentage;
    private Double individualHighestCompletionPercentage;
    private Double individualLowestCompletionPercentage;

    private Integer bundlesEnrolled;
    private Double bundleAvgCompletionPercentage;
    private Double bundleHighestCompletionPercentage;
    private Double bundleLowestCompletionPercentage;

    private Integer groupsPartOf;
    private Integer groupCourseEnrollments;
    private Integer groupBundleEnrollments;

    private Integer totalEnrollments;

    private Integer coursesCompleted;
    private Integer coursesInProgress;
    private Integer coursesNotStarted;

    private Integer coursesCompletedOnTime;
    private Integer courseCompletedLate;
    private Integer coursesOnTrack;
    private Integer coursesNotOnTrack;
    private Integer coursesYetToStart;
    private Integer deadlineMissedCourses;
}
