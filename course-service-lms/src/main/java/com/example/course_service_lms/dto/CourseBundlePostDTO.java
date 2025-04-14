package com.example.course_service_lms.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

import static com.example.course_service_lms.constants.CourseBundleConstants.*;

@Data
public class CourseBundlePostDTO {
    private long courseBundleId;

    @NotNull(message = BUNDLE_ID_NOT_NULL)
    @Positive(message = BUNDLE_ID_POSITIVE)
    private Long bundleId;

    @NotNull(message = COURSE_ID_NOT_NULL)
    @Positive(message = COURSE_ID_POSITIVE)
    private Long courseId;

    public CourseBundlePostDTO() {
    }

    public CourseBundlePostDTO(long courseBundleId, Long bundleId, Long courseId) {
        this.courseBundleId = courseBundleId;
        this.bundleId = bundleId;
        this.courseId = courseId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CourseBundlePostDTO that = (CourseBundlePostDTO) o;
        return courseBundleId == that.courseBundleId && Objects.equals(bundleId, that.bundleId) && Objects.equals(courseId, that.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseBundleId, bundleId, courseId);
    }
}
