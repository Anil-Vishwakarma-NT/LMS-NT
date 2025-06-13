package com.example.course_service_lms.dto.outDTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseContentOutDTO {
    private long courseContentId;
    private long courseId;
    private String title;
    private String description;
    private String resourceLink;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
