package com.example.course_service_lms.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.Objects;

import static com.example.course_service_lms.constants.CourseBundleConstants.BUNDLE_ID_NOT_NULL;
import static com.example.course_service_lms.constants.CourseBundleConstants.BUNDLE_ID_POSITIVE;
import static com.example.course_service_lms.constants.CourseBundleConstants.COURSE_ID_NOT_NULL;
import static com.example.course_service_lms.constants.CourseBundleConstants.COURSE_ID_POSITIVE;

/**
 * Data Transfer Object (DTO) for creating a relationship between a course and a bundle.
 *
 * <p>This DTO is typically used in POST requests to associate a specific course
 * with a bundle in the Course-Bundle mapping table.</p>
 *
 * <p><b>Validation Constraints:</b></p>
 * <ul>
 *     <li>{@code bundleId} - Required, must be a positive number</li>
 *     <li>{@code courseId} - Required, must be a positive number</li>
 * </ul>
 */
@Data
public class CreateCourseBundleDTO {

    /**
     * The ID of the bundle to which the course should be added.
     * <p>Must be non-null and a positive number.</p>
     */
    @NotNull(message = BUNDLE_ID_NOT_NULL)
    @Positive(message = BUNDLE_ID_POSITIVE)
    private Long bundleId;

    /**
     * The ID of the course to associate with the bundle.
     * <p>Must be non-null and a positive number.</p>
     */
    @NotNull(message = COURSE_ID_NOT_NULL)
    @Positive(message = COURSE_ID_POSITIVE)
    private Long courseId;

    /**
     * Equality check for two {@code CreateCourseBundleDTO} objects.
     *
     * @param o Object to compare with
     * @return true if {@code bundleId} and {@code courseId} are equal
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CreateCourseBundleDTO that = (CreateCourseBundleDTO) o;
        return Objects.equals(bundleId, that.bundleId) && Objects.equals(courseId, that.courseId);
    }

    /**
     * Generates a hash code based on {@code bundleId} and {@code courseId}.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(bundleId, courseId);
    }
}
