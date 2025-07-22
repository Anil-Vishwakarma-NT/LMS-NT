package com.nt.user_service_lms.dto.outDTO;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object representing an enrolled bundle with its details and enrolled courses.
 */
@Data
public class EnrolledBundlesOutDTO {

    /**
     * The unique identifier of the bundle.
     */
    private Long bundleId;

    /**
     * The name of the bundle.
     */
    private String bundleName;

    /**
     * The progress percentage of the bundle.
     */
    private Float progress;

    /**
     * The date and time when the bundle was enrolled.
     */
    private LocalDateTime enrollmentDate;

    /**
     * The deadline for the bundle.
     */
    private LocalDateTime deadline;

    /**
     * The list of enrolled courses in the bundle.
     */
    private List<EnrolledCoursesOutDTO> enrolledCoursesList;
}
