package com.nt.LMS.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserEnrollmentsDTO {
    private Long userId;
    private String userName;
    private Long courseEnrollments;
    private Long bundleEnrollments;
    private Long totalCourses;
    private Float averageCompletion;
    private Integer upcomingDeadlines;
    private boolean status;
    private List<EnrolledCoursesDTO> enrolledCoursesList;
    private List<EnrolledBundlesDTO> enrolledBundlesList;
}
