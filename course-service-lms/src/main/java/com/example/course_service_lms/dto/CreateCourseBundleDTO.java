package com.example.course_service_lms.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import static com.example.course_service_lms.constants.CourseBundleConstants.*;
import static com.example.course_service_lms.constants.CourseBundleConstants.COURSE_ID_POSITIVE;

@Data
public class CreateCourseBundleDTO {

    @NotNull(message = BUNDLE_ID_NOT_NULL)
    @Positive(message = BUNDLE_ID_POSITIVE)
    private Long bundleId;

    @NotNull(message = COURSE_ID_NOT_NULL)
    @Positive(message = COURSE_ID_POSITIVE)
    private Long courseId;
}
