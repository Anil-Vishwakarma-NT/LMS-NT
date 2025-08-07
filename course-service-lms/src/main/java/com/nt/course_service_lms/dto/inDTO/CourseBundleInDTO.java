package com.nt.course_service_lms.dto.inDTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

import static com.nt.course_service_lms.constants.CourseBundleConstants.BUNDLE_ID_NOT_NULL;
import static com.nt.course_service_lms.constants.CourseBundleConstants.BUNDLE_ID_POSITIVE;
import static com.nt.course_service_lms.constants.CourseBundleConstants.COURSE_ID_NOT_NULL;
import static com.nt.course_service_lms.constants.CourseBundleConstants.COURSE_ID_POSITIVE;

/**
 * Data Transfer Object for representing the association between a course and a bundle
 * within the Learning Management System (LMS).
 *
 * <p>This DTO is intended for POST and PUT requests to add or update course-bundle mappings.</p>
 *
 * <p><b>Validation:</b></p>
 * <ul>
 *     <li>{@code bundleId} - Must be non-null and positive</li>
 *     <li>{@code courseId} - Must be non-null and positive</li>
 *     <li>{@code isActive} - Must be non-null (indicates if the relationship is currently active)</li>
 * </ul>
 *
 * <p><b>Note:</b> The {@code courseBundleId} field is optional and usually managed by the system.</p>
 *
 * @author
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseBundleInDTO {

    /**
     * Unique identifier for the course-bundle mapping.
     * <p>This is typically auto-generated and used for internal tracking.</p>
     */
    private long courseBundleId;

    /**
     * The ID of the bundle to which the course is being associated.
     * <p>Must be a positive non-null value, validated at the API level.</p>
     */
    @NotNull(message = BUNDLE_ID_NOT_NULL)
    @Positive(message = BUNDLE_ID_POSITIVE)
    private Long bundleId;

    /**
     * The ID of the course that is being added to the bundle.
     * <p>Must be a positive non-null value, validated at the API level.</p>
     */
    @NotNull(message = COURSE_ID_NOT_NULL)
    @Positive(message = COURSE_ID_POSITIVE)
    private Long courseId;

    /**
     * Indicates whether the course-bundle association is active.
     * <p>If false, the course may not be accessible from the bundle.</p>
     */
    @NotNull(message = "Is Active field is required")
    private boolean isActive;

    /**
     * Custom equality check based on all fields of the DTO.
     *
     * @param o the object to compare with
     * @return {@code true} if all properties match, else {@code false}
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CourseBundleInDTO that = (CourseBundleInDTO) o;
        return courseBundleId == that.courseBundleId
                && isActive == that.isActive
                && Objects.equals(bundleId, that.bundleId)
                && Objects.equals(courseId, that.courseId);
    }

    /**
     * Computes the hash code based on all fields.
     *
     * @return hash code as integer
     */
    @Override
    public int hashCode() {
        return Objects.hash(courseBundleId, bundleId, courseId, isActive);
    }
}
