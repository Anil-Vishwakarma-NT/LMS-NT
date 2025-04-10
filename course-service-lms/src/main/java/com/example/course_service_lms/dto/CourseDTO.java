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

import static com.example.course_service_lms.constants.CourseConstants.*;

@Data


public class CourseDTO {

    @NotBlank(message = TITLE_BLANK)
    @Size(min = 3, message = TITLE_MIN_LENGTH)
    private String title;

    @NotNull(message = OWNER_ID_BLANK)
    @Min(value = 0, message = OWNER_ID_INVALID)
    private Long ownerId;

    @NotBlank(message = DESCRIPTION_BLANK)
    @Size(min = 3, message = DESCRIPTION_MIN_LENGTH)
    private String description;

    @NotNull(message = COURSE_LEVEL_REQUIRED)
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
