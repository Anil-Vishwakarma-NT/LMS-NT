package com.nt.LMS.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserCourseEnrollmentOutDTO {
    private Long userId;
    private Long courseId;
    private Long assignedById;
    private String userName;
    private String courseName;
    private String assignedByName;
    private LocalDateTime deadline;
}
