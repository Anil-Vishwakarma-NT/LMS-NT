package com.example.course_service_lms.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

}
