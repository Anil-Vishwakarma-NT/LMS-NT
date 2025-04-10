package com.example.course_service_lms.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
public class CourseBundleDTO {
    private long courseBundleId;

    @NotNull(message = "Bundle ID cannot be null")
    @Positive(message = "Bundle ID must be a positive number")
    private Long bundleId;

    @NotNull
    private String bundleName;

    @NotNull(message = "Course ID cannot be null")
    @Positive(message = "Course ID must be a positive number")
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
