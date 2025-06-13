package com.example.course_service_lms.dto.inDTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Objects;

import static com.example.course_service_lms.constants.BundleConstants.INT_VALUE_3;
import static com.example.course_service_lms.constants.CourseConstants.COURSE_LEVEL_REQUIRED;
import static com.example.course_service_lms.constants.CourseConstants.DESCRIPTION_BLANK;
import static com.example.course_service_lms.constants.CourseConstants.DESCRIPTION_MIN_LENGTH;
import static com.example.course_service_lms.constants.CourseConstants.OWNER_ID_BLANK;
import static com.example.course_service_lms.constants.CourseConstants.OWNER_ID_INVALID;
import static com.example.course_service_lms.constants.CourseConstants.TITLE_BLANK;
import static com.example.course_service_lms.constants.CourseConstants.TITLE_MIN_LENGTH;

/**
 * Data Transfer Object (DTO) for transferring course data between
 * the controller and service layers of the LMS system.
 *
 * <p>This DTO is typically used when creating or updating a course.</p>
 *
 * <p><b>Validation Constraints:</b></p>
 * <ul>
 *     <li>{@code title} - Required, minimum 3 characters</li>
 *     <li>{@code ownerId} - Required, must be ≥ 0</li>
 *     <li>{@code description} - Required, minimum 3 characters</li>
 *     <li>{@code courseLevel} - Required (e.g., BEGINNER, INTERMEDIATE, ADVANCED)</li>
 *     <li>{@code image} - Optional (URL or file path of the course thumbnail)</li>
 * </ul>
 */
@Data
public class CourseInDTO {

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

    /**
     * All-args constructor for initializing all fields.
     *
     * @param title        Title of the course
     * @param ownerId      ID of the course owner
     * @param description  Description of the course
     * @param courseLevel  Level of the course (e.g., BEGINNER)
     */
    public CourseInDTO(String title, Long ownerId, String description, String courseLevel, boolean isActive) {
        this.title = title;
        this.ownerId = ownerId;
        this.description = description;
        this.courseLevel = courseLevel;
        this.isActive = isActive;
    }

    /**
     * No-args constructor required by serialization frameworks.
     */
    public CourseInDTO() {
    }

    /**
     * Custom equality logic comparing all relevant fields.
     *
     * @param o The object to compare
     * @return true if all fields are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseInDTO courseInDTO = (CourseInDTO) o;
        return isActive == courseInDTO.isActive && Objects.equals(title, courseInDTO.title) && Objects.equals(ownerId, courseInDTO.ownerId) && Objects.equals(description, courseInDTO.description) && Objects.equals(courseLevel, courseInDTO.courseLevel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, ownerId, description, courseLevel, isActive);
    }
}
