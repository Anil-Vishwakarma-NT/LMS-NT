package com.example.course_service_lms.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

import static com.example.course_service_lms.constants.CourseBundleConstants.*;

@Data
public class CourseBundleDTO {
    private long courseBundleId;

    @NotNull(message = BUNDLE_ID_NOT_NULL)
    @Positive(message = BUNDLE_ID_POSITIVE)
    private Long bundleId;

    @NotNull
    private String bundleName;

    @NotNull(message = COURSE_ID_NOT_NULL)
    @Positive(message = COURSE_ID_POSITIVE)
    private Long courseId;

    @NotNull
    private String courseName;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CourseBundleDTO that = (CourseBundleDTO) o;
        return courseBundleId == that.courseBundleId && Objects.equals(bundleId, that.bundleId) && Objects.equals(bundleName, that.bundleName) && Objects.equals(courseId, that.courseId) && Objects.equals(courseName, that.courseName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseBundleId, bundleId, bundleName, courseId, courseName);
    }

    public CourseBundleDTO() {
    }

    public CourseBundleDTO(long courseBundleId, Long bundleId, String bundleName, Long courseId, String courseName) {
        this.courseBundleId = courseBundleId;
        this.bundleId = bundleId;
        this.bundleName = bundleName;
        this.courseId = courseId;
        this.courseName = courseName;
    }
}
