package com.nt.LMS.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EnrolledCoursesDTO {
    private Long courseId;
    private String courseName;
    private Float progress;
    private LocalDateTime enrollmentDate;
    private LocalDateTime deadline;
}
