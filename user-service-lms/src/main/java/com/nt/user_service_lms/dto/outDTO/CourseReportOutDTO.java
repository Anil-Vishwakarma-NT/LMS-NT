package com.nt.user_service_lms.dto.outDTO;

import lombok.Data;

@Data
public class CourseReportOutDTO {

    private Long courseId;
    private String courseTitle;
    private String courseDescription;
    private String courseLevel;

    // Individual user-level metrics
    private Integer individualEnrollments;
    private Double individualAvgCompletionPercentage;
    private Double individualHighestCompletionPercentage;
    private Double individualLowestCompletionPercentage;

    // Group-level aggregated metrics (one per group)
    private Integer groupsEnrolled;
    private Double groupAvgCompletionPercentage;
    private Double groupHighestCompletionPercentage;
    private Double groupLowestCompletionPercentage;

    private Integer bundlesCourseIsPartOf;         // No. of bundles this course belongs to
    private Integer bundleIndividualEnrollments;   // Users who got this course via bundle
    private Integer bundleGroupEnrollments;

    // Total user enrollments (individual + group users)
    private Integer totalEnrollments;

    // Status breakdowns (user-level)
    private Integer completed;
    private Integer inProgress;
    private Integer notStarted;

    // Adherence breakdowns (user-level)
    private Integer onTime;
    private Integer late;
    private Integer onTrack;
    private Integer behindSchedule;
    private Integer notYetStarted;
    private Integer deadlineMissed;
}
