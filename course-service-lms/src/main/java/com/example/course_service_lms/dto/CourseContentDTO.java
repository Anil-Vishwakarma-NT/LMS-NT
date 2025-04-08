package com.example.course_service_lms.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CourseContentDTO {

    @NotBlank(message = "Course ID cannot be blank")
    @Min(value = 0)
    private long courseId;

    @NotBlank(message = "Title cannot be blank")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;

    @NotBlank(message = "Description cannot be blank")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotBlank(message = "video link cannot be blank")
    @Pattern(
            regexp = "^(https?|ftp)://.*$",
            message = "Video link must be a valid URL"
    )
    private String videoLink;

    @Pattern(
            regexp = "^(https?|ftp)://.*$",
            message = "Resource link must be a valid URL"
    )
    private String resourceLink;
}
