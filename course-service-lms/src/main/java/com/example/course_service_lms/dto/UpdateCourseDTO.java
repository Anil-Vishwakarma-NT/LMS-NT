package com.example.course_service_lms.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.example.course_service_lms.constants.BundleConstants.INT_VALUE_3;
import static com.example.course_service_lms.constants.CourseConstants.*;

@Data
public class UpdateCourseDTO {

    /**
     * The title of the course.
     * <p>Must be non-blank and at least 3 characters long.</p>
     */
    @NotBlank(message = TITLE_BLANK)
    @Size(min = INT_VALUE_3, message = TITLE_MIN_LENGTH)
    private String title;

    /**
     * The ID of the user who owns or created the course.
     * <p>Must be non-null and greater than or equal to 0.</p>
     */
    @NotNull(message = OWNER_ID_BLANK)
    @Min(value = 0, message = OWNER_ID_INVALID)
    private Long ownerId;

    /**
     * A brief description of the course.
     * <p>Must be non-blank and at least 3 characters long.</p>
     */
    @NotBlank(message = DESCRIPTION_BLANK)
    @Size(min = INT_VALUE_3, message = DESCRIPTION_MIN_LENGTH)
    private String description;

    /**
     * The difficulty level of the course.
     * <p>Examples: BEGINNER, INTERMEDIATE, ADVANCED. Must be non-null.</p>
     */
    @NotNull(message = COURSE_LEVEL_REQUIRED)
    private String courseLevel;

    @NotNull(message = "Is Active field is required")
    private boolean isActive;

    public UpdateCourseDTO(String title, Long ownerId, String description, String courseLevel, String image, boolean isActive) {
        this.title = title;
        this.ownerId = ownerId;
        this.description = description;
        this.courseLevel = courseLevel;
        this.isActive = isActive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateCourseDTO that = (UpdateCourseDTO) o;
        return isActive == that.isActive && Objects.equals(title, that.title) && Objects.equals(ownerId, that.ownerId) && Objects.equals(description, that.description) && Objects.equals(courseLevel, that.courseLevel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, ownerId, description, courseLevel, isActive);
    }
}
