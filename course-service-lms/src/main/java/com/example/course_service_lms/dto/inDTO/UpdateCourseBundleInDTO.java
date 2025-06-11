package com.example.course_service_lms.dto.inDTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.Objects;

import static com.example.course_service_lms.constants.CourseBundleConstants.*;
import static com.example.course_service_lms.constants.CourseBundleConstants.COURSE_ID_POSITIVE;

@Data
public class UpdateCourseBundleInDTO {

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


    public UpdateCourseBundleInDTO(Long bundleId, Long courseId, boolean isActive) {
        this.bundleId = bundleId;
        this.courseId = courseId;
        this.isActive = isActive;
    }

    public UpdateCourseBundleInDTO() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateCourseBundleInDTO that = (UpdateCourseBundleInDTO) o;
        return isActive == that.isActive && Objects.equals(bundleId, that.bundleId) && Objects.equals(courseId, that.courseId) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bundleId, courseId, isActive);
    }
}
