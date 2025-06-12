package com.example.course_service_lms.dto.outDTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseOutDTO {
    private long courseId;
    private long ownerId;
    private String title;
    private String description;
    private String level;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
