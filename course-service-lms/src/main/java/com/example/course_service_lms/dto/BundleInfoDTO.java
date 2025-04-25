package com.example.course_service_lms.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BundleInfoDTO {
    private Long BundleId;
    private String bundleName;
    private Long totalCourses;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
