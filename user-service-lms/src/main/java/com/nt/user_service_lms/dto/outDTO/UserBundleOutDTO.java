package com.nt.user_service_lms.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing bundle details for a user.
 * <p>
 * This DTO includes basic information about a course bundle such as the bundle ID,
 * name, total number of courses, and user's progress in percentage.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBundleOutDTO {

    /**
     * The unique identifier of the bundle.
     */
    private long bundleId;

    /**
     * The name of the course bundle.
     */
    private String bundleName;

    /**
     * The number of courses included in the bundle.
     */
    private long numberOfCourses;

    /**
     * The user's progress in the bundle represented as a percentage (0.0 to 100.0).
     */
    private double progress;

}
