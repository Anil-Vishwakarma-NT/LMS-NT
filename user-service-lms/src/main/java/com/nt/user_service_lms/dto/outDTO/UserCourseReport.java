package com.nt.user_service_lms.dto.outDTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserCourseReport {
    private String courseName;
    private String courseDescription;
    private String courseLevel;
    private Double courseCompletionPercentage;
    private LocalDateTime lastViewed;
    private String assignedBy;
    private LocalDateTime assignedAt;
    private LocalDateTime deadline;
    private String status;
    private LocalDateTime firstCompletedAt;
}
