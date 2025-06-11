package com.example.course_service_lms.outDTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BundleInfoOutDTO {
    private Long BundleId;
    private String bundleName;
    private Long totalCourses;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
