package com.example.course_service_lms.outDTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseInfoOutDTO {

    private String title;

    private Long courseId;

    private Long ownerId;

    private String description;

    private String courseLevel;

    private boolean isActive;

    private LocalDateTime updatedAt;
}
