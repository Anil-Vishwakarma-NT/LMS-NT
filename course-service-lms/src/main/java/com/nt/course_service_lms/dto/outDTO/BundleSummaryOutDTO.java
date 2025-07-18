package com.nt.course_service_lms.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing summary information of a course bundle.
 * <p>
 * This DTO is used to provide high-level information about a bundle,
 * such as its name, number of associated courses, and metadata timestamps.
 * Typically used in bundle listings or overview sections.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BundleSummaryOutDTO {

    /**
     * Unique identifier for the course bundle.
     */
    private Long bundleId;

    /**
     * Name or title of the bundle.
     */
    private String bundleName;

    /**
     * Total number of courses included in the bundle.
     */
    private Long courseCount;

    /**
     * Timestamp indicating when the bundle was created.
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp indicating when the bundle details were last updated.
     */
    private LocalDateTime updatedAt;
}
