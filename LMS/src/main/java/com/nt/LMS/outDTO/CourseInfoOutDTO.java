package com.nt.LMS.outDTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseInfoOutDTO {

    private String title;

    private Long ownerId;

    private Long courseId;

    private String description;

    private String courseLevel;

    private boolean isActive;

    private LocalDateTime updatedAt;
}