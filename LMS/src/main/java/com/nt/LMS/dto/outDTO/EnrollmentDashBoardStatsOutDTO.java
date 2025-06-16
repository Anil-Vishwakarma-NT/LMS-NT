package com.nt.LMS.dto.outDTO;

import lombok.Data;

@Data
public class EnrollmentDashBoardStatsOutDTO {
    private Long totalEnrollments;
    private Long individualUsersEnrolled;
    private Long groupsEnrolled;
    private Long bundlesEnrolled;
    private String topEnrolledCourse;
    private Long averageProgressPercentage;
    private Long dueDeadlines;
}
