package com.example.course_service_lms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO representing bundle summary with course count information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BundleSummaryDTO {
    private Long bundleId;
    private String bundleName;
    private Long courseCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
