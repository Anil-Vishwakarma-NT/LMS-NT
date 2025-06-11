package com.nt.LMS.outDTO;

import lombok.Data;

import java.util.List;

@Data
public class UserEnrollmentsOutDTO {
    private Long userId;
    private String userName;
    private Long courseEnrollments;
    private Long bundleEnrollments;
    private Long totalCourses;
    private Float averageCompletion;
    private Integer upcomingDeadlines;
    private boolean status;
    private List<EnrolledCoursesOutDTO> enrolledCoursesList;
    private List<EnrolledBundlesOutDTO> enrolledBundlesList;
}
