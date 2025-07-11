package com.nt.user_service_lms.dto.outDTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BundleInfoOutDTO {
    /**
     * Unique identifier for the bundle.
     */
    private Long bundleId;
    /**
     * Name of the bundle.
     */
    private String bundleName;
    /**
     * Total number of courses in the bundle.
     */
    private Long totalCourses;
    /**
     * Indicates if the bundle is active.
     */
    private boolean isActive;
    /**
     * Date and time when the bundle was created.
     */
    private LocalDateTime createdAt;
    /**
     * Date and time when the bundle was last updated.
     */
    private LocalDateTime updatedAt;
}

