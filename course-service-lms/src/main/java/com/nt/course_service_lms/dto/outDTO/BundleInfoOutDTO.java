package com.nt.course_service_lms.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing detailed information about a course bundle.
 * <p>
 * This DTO is used to share bundle-related information with clients,
 * such as in bundle listings, details view, or administrative interfaces.
 * It includes metadata like total courses, activation status, and timestamps.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BundleInfoOutDTO {

    /**
     * Unique identifier of the bundle.
     */
    private Long bundleId;

    /**
     * Name or title of the bundle.
     */
    private String bundleName;

    /**
     * Total number of courses included in the bundle.
     */
    private Long totalCourses;

    /**
     * Indicates whether the bundle is currently active or published.
     */
    private boolean isActive;

    /**
     * Timestamp indicating when the bundle was created.
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp indicating when the bundle was last updated.
     */
    private LocalDateTime updatedAt;
}
