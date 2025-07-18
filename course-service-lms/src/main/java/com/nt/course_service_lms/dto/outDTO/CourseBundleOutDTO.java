package com.nt.course_service_lms.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * CourseBundleDTO is a Data Transfer Object used to transfer course-bundle relationship data
 * between layers or microservices in the LMS system.
 *
 * <p>This DTO encapsulates a mapping between a course and a bundle,
 * while also providing relevant names for better display and identification.</p>
 *
 * <p><b>Validation Rules:</b></p>
 * <ul>
 *     <li>{@code bundleId} - Must be non-null and positive</li>
 *     <li>{@code courseId} - Must be non-null and positive</li>
 *     <li>{@code bundleName} - Must not be null</li>
 *     <li>{@code courseName} - Must not be null</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseBundleOutDTO {

    /**
     * Unique identifier for the course-bundle relationship entry.
     */
    private long courseBundleId;

    /**
     * ID of the bundle this course belongs to.
     * <p>Must be a positive non-null value.</p>
     */

    private Long bundleId;

    /**
     * Human-readable name of the bundle.
     * <p>Used for UI representation or search filtering.</p>
     */

    private String bundleName;

    /**
     * ID of the course that is part of the bundle.
     * <p>Must be a positive non-null value.</p>
     */

    private Long courseId;

    /**
     * Human-readable name of the course.
     * <p>Used for display or reference purposes.</p>
     */

    private String courseName;

    /**
     * Compares this course bundle out dto to another object for equality.
     *
     * @param o the object to compare with
     * @return {@code true} if the bundles are equal; {@code false} otherwise
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CourseBundleOutDTO that = (CourseBundleOutDTO) o;
        return courseBundleId == that.courseBundleId
                && Objects.equals(bundleId, that.bundleId)
                && Objects.equals(bundleName, that.bundleName)
                && Objects.equals(courseId, that.courseId)
                && Objects.equals(courseName, that.courseName);
    }

    /**
     * Generates a hash code consistent with {@link #equals(Object)}.
     *
     * @return hash code based on all persistent fields
     */
    @Override
    public int hashCode() {
        return Objects.hash(courseBundleId, bundleId, bundleName, courseId, courseName);
    }
}
