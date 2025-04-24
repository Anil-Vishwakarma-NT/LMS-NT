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
public class CourseBundleDTO {

    /**
     * Unique identifier for the course-bundle relationship entry.
     */
    private long courseBundleId;

    /**
     * ID of the bundle this course belongs to.
     * <p>Must be a positive non-null value.</p>
     */
    @NotNull(message = BUNDLE_ID_NOT_NULL)
    @Positive(message = BUNDLE_ID_POSITIVE)
    private Long bundleId;

    /**
     * Human-readable name of the bundle.
     * <p>Used for UI representation or search filtering.</p>
     */
    @NotNull
    private String bundleName;

    /**
     * ID of the course that is part of the bundle.
     * <p>Must be a positive non-null value.</p>
     */
    @NotNull(message = COURSE_ID_NOT_NULL)
    @Positive(message = COURSE_ID_POSITIVE)
    private Long courseId;

    /**
     * Human-readable name of the course.
     * <p>Used for display or reference purposes.</p>
     */
    @NotNull(message = "Course name is required")
    private String courseName;

    /**
     * Default constructor.
     */
    public CourseBundleDTO() {
    }

    /**
     * All-args constructor for manually creating a CourseBundleDTO.
     *
     * @param courseBundleId unique course-bundle relationship ID
     * @param bundleId       ID of the associated bundle
     * @param bundleName     name of the bundle
     * @param courseId       ID of the associated course
     * @param courseName     name of the course
     */
    public CourseBundleDTO(long courseBundleId, Long bundleId, String bundleName, Long courseId, String courseName) {
        this.courseBundleId = courseBundleId;
        this.bundleId = bundleId;
        this.bundleName = bundleName;
        this.courseId = courseId;
        this.courseName = courseName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseBundleDTO that = (CourseBundleDTO) o;
        return courseBundleId == that.courseBundleId && Objects.equals(bundleId, that.bundleId) && Objects.equals(bundleName, that.bundleName) && Objects.equals(courseId, that.courseId) && Objects.equals(courseName, that.courseName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseBundleId, bundleId, bundleName, courseId, courseName);
    }
}
