package com.nt.user_service_lms.dto.outDTO;

import lombok.Data;

/**
 * Data Transfer Object for representing a course bundle output.
 */
@Data
public class CourseBundleOutDTO {

    /**
     * Unique identifier for the course bundle.
     */
    private long courseBundleId;

    /**
     * Identifier for the bundle.
     */
    private long bundleId;

    /**
     * Identifier for the course.
     */
    private long courseId;
}
