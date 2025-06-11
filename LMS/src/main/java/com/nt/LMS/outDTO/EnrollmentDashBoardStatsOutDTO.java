package com.nt.LMS.outDTO;

import lombok.Data;

@Data
public class EnrollmentDashBoardStatsOutDTO {
    private Long totalEnrollments;
    private Long usersEnrolled;
    private Long groupsEnrolled;
    private Long completionRate;
    private String topEnrolledCourse;
    private Long upcomingDeadlines;
    private Long courseCompletions;
}
