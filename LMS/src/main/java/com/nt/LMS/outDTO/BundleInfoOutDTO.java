package com.nt.LMS.outDTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BundleInfoOutDTO {
    private Long bundleId;
    private String bundleName;
    private Long totalCourses;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

