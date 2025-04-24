package com.example.course_service_lms.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

import static com.example.course_service_lms.constants.BundleConstants.INT_VALUE_3;
import static com.example.course_service_lms.constants.CourseConstants.*;

@Data
public class CourseInfoDTO {

    private String title;

    private Long courseId;

    private Long ownerId;

    private String description;

    private String courseLevel;

    private String image;

    private boolean isActive;

    private LocalDateTime updatedAt;
}
