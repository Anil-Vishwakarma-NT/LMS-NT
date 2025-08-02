package com.nt.user_service_lms.dto.outDTO;

import lombok.Data;

@Data
public class BundleReportOutDTO {

    private Long bundleId;
    private String bundleName;

    private Integer individualUsersEnrolled;
    private Long individualHighestCompletion;
    private Long individualLowestCompletion;
    private Double individualAvgCompletion;

    private Integer groupsEnrolled;
    private Double groupHighestCompletion;
    private Double groupLowestCompletion;
    private Double groupAvgCompletion;

    private Integer totalUsersEnrolled;
    private Integer totalCourses;
    private Integer totalEnrollments;

    private Integer coursesCompleted;
    private Integer coursesInProgress;
    private Integer coursesNotStarted;

    private Integer coursesCompletedOnTime;
    private Integer coursesCompletedLate;
    private Integer coursesOnTrack;
    private Integer coursesBehindSchedule;
    private Integer coursesNotDueYet;
    private Integer coursesOverdue;
}
