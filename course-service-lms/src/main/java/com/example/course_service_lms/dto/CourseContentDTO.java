package com.example.course_service_lms.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Objects;

@Data
public class CourseContentDTO {

    @NotNull(message = "Course ID cannot be blank")
    @Min(value = 0, message = "Valid Course ID required")
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
            regexp = "^$|^(https?|ftp)://.*$",
            message = "Resource link must be a valid URL"
    )
    private String resourceLink;

    public CourseContentDTO(long courseId, String title, String description, String videoLink, String resourceLink) {
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.videoLink = videoLink;
        this.resourceLink = resourceLink;
    }

    public CourseContentDTO() {
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CourseContentDTO that = (CourseContentDTO) o;
        return courseId == that.courseId && Objects.equals(title, that.title) && Objects.equals(description, that.description) && Objects.equals(videoLink, that.videoLink) && Objects.equals(resourceLink, that.resourceLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, title, description, videoLink, resourceLink);
    }
}
