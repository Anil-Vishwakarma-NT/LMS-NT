package com.example.course_service_lms.dto;

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
public class CourseDTO {

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

    /**
     * A string representing the course's image.
     * <p>This can be a URL or a path to a local file (optional).</p>
     */
    private String image;

    /**
     * All-args constructor for initializing all fields.
     *
     * @param title        Title of the course
     * @param ownerId      ID of the course owner
     * @param description  Description of the course
     * @param courseLevel  Level of the course (e.g., BEGINNER)
     * @param image        Course image or thumbnail (optional)
     */
    public CourseDTO(final String title, final long ownerId, final String description,
                     final String courseLevel, final String image) {
        this.title = title;
        this.ownerId = ownerId;
        this.description = description;
        this.courseLevel = courseLevel;
        this.image = image;
    }

    /**
     * No-args constructor required by serialization frameworks.
     */
    public CourseDTO() {
    }

    /**
     * Custom equality logic comparing all relevant fields.
     *
     * @param o The object to compare
     * @return true if all fields are equal, false otherwise
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CourseDTO courseDTO = (CourseDTO) o;
        return ownerId == courseDTO.ownerId
                && Objects.equals(title, courseDTO.title)
                && Objects.equals(description, courseDTO.description)
                && Objects.equals(courseLevel, courseDTO.courseLevel)
                && Objects.equals(image, courseDTO.image);
    }

    /**
     * Custom hash code based on all fields.
     *
     * @return hash code for the object
     */
    @Override
    public int hashCode() {
        return Objects.hash(title, ownerId, description, courseLevel, image);
    }
}
