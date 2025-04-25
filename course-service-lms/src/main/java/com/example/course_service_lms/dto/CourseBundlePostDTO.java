package com.example.course_service_lms.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.example.course_service_lms.constants.CourseBundleConstants.BUNDLE_ID_NOT_NULL;
import static com.example.course_service_lms.constants.CourseBundleConstants.BUNDLE_ID_POSITIVE;
import static com.example.course_service_lms.constants.CourseBundleConstants.COURSE_ID_NOT_NULL;
import static com.example.course_service_lms.constants.CourseBundleConstants.COURSE_ID_POSITIVE;

/**
 * CourseBundlePostDTO is a lightweight Data Transfer Object used specifically for creating
 * or updating the relationship between a course and a bundle in the LMS system.
 *
 * <p>This DTO omits the name fields and focuses only on ID references,
 * making it suitable for POST/PUT operations where only IDs are needed.</p>
 *
 * <p><b>Validation Rules:</b></p>
 * <ul>
 *     <li>{@code bundleId} - Must be non-null and positive</li>
 *     <li>{@code courseId} - Must be non-null and positive</li>
 * </ul>
 */
@Data
public class CourseBundlePostDTO {

    /**
     * Unique identifier for the course-bundle relationship.
     * <p>This ID is optional for POST requests and typically auto-generated.</p>
     */
    private long courseBundleId;

    /**
     * The ID of the bundle this course is being added to.
     * <p>Must be a positive non-null value.</p>
     */
    @NotNull(message = BUNDLE_ID_NOT_NULL)
    @Positive(message = BUNDLE_ID_POSITIVE)
    private Long bundleId;

    /**
     * The ID of the course being added to the bundle.
     * <p>Must be a positive non-null value.</p>
     */
    @NotNull(message = COURSE_ID_NOT_NULL)
    @Positive(message = COURSE_ID_POSITIVE)
    private Long courseId;

    @NotNull(message = "Is Active field is required")
    private boolean isActive;

    /**
     * Default constructor for frameworks that require no-args constructor.
     */
    public CourseBundlePostDTO() {
    }

    /**
     * All-args constructor for manual DTO instantiation.
     *
     * @param courseBundleId unique ID of the course-bundle mapping
     * @param bundleId       ID of the bundle
     * @param courseId       ID of the course
     */

    public CourseBundlePostDTO(long courseBundleId, Long bundleId, Long courseId, boolean isActive) {
        this.courseBundleId = courseBundleId;
        this.bundleId = bundleId;
        this.courseId = courseId;
        this.isActive = isActive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseBundlePostDTO that = (CourseBundlePostDTO) o;
        return courseBundleId == that.courseBundleId && isActive == that.isActive && Objects.equals(bundleId, that.bundleId) && Objects.equals(courseId, that.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseBundleId, bundleId, courseId, isActive);
    }
}
