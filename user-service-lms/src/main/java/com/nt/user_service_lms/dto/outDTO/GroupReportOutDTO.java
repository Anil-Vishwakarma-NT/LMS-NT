package com.nt.user_service_lms.dto.outDTO;

import lombok.Data;

@Data
public class GroupReportOutDTO {

    private Long groupId;
    private String groupName;

    private Integer individualCoursesEnrolled;
    private Double individualCoursesAvgCompletionPercentage;
    private Double individualCoursesHighestCompletionPercentage;
    private Double individualCoursesLowestCompletionPercentage;

    private Integer bundlesEnrolled;
    private Double bundleAvgCompletionPercentage;
    private Double bundleHighestCompletionPercentage;
    private Double bundleLowestCompletionPercentage;

    private Integer totalCoursesEnrolled;
    private Integer totalUsersInGroup;
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
