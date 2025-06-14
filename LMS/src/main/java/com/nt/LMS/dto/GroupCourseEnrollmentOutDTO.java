package com.nt.LMS.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GroupCourseEnrollmentOutDTO {
    private Long groupId;
    private Long courseId;
    private Long assignedById;
    private String groupName;
    private String courseName;
    private String assignedByName;
    private LocalDateTime deadline;
}
