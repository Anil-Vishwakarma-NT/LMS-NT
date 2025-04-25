package com.nt.LMS.dto;

import lombok.Data;

@Data
public class EnrollmentDashBoardStatsDTO {
    private Long totalEnrollments;
    private Long usersEnrolled;
    private Long groupsEnrolled;
    private Long completionRate;
    private String topEnrolledCourse;
    private Long upcomingDeadlines;
    private Long courseCompletions;
}
