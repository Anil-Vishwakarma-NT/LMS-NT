package com.nt.LMS.dto.outDTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EnrolledCoursesOutDTO {
    private Long courseId;
    private String courseName;
    private Float progress;
    private LocalDateTime enrollmentDate;
    private LocalDateTime deadline;
}
