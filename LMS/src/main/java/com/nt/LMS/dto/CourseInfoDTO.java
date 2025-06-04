package com.nt.LMS.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseInfoDTO {

    private String title;

    private Long ownerId;

    private Long courseId;

    private String description;

    private String courseLevel;

    private boolean isActive;

    private LocalDateTime updatedAt;
}