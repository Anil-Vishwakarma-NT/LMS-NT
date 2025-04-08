package com.example.course_service_lms.dto;

import com.example.course_service_lms.Enum.CourseLevel;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data


public class CourseDTO {

    @NotBlank(message = "Title cannot be blank")
    @Size(min = 3, message = "Title must be at least 3 characters long")
    private String title;

    @NotNull(message = "OwnerId cannot be blank")
    @Min(value = 0, message = "Valid Owner ID required")
    private Long ownerId;

    @NotBlank(message = "Description cannot be blank")
    @Size(min = 3, message = "Description must be at least 3 characters long")
    private String description;

    @NotNull(message = "Course level is required")
    private String courseLevel;

    private String image;

    public CourseDTO(String title, long ownerId, String description, String courseLevel, String image) {
        this.title = title;
        this.ownerId = ownerId;
        this.description = description;
        this.courseLevel = courseLevel;
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CourseDTO courseDTO = (CourseDTO) o;
        return ownerId == courseDTO.ownerId && Objects.equals(title, courseDTO.title) && Objects.equals(description, courseDTO.description) && Objects.equals(courseLevel, courseDTO.courseLevel) && Objects.equals(image, courseDTO.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, ownerId, description, courseLevel, image);
    }

    public CourseDTO() {
    }
}
