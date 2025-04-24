package com.nt.LMS.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseEnrolledUserDTO {
    private Long userId;
    private String userName;
    private Long progress;
    private LocalDateTime enrollmentDate;
    private LocalDateTime deadline;
}
