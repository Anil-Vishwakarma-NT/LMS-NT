package com.nt.course_service_lms.dto.inDTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

import static com.nt.course_service_lms.constants.BundleConstants.INT_VALUE_3;
import static com.nt.course_service_lms.constants.CourseConstants.COURSE_LEVEL_REQUIRED;
import static com.nt.course_service_lms.constants.CourseConstants.DESCRIPTION_BLANK;
import static com.nt.course_service_lms.constants.CourseConstants.DESCRIPTION_MIN_LENGTH;
import static com.nt.course_service_lms.constants.CourseConstants.OWNER_ID_BLANK;
import static com.nt.course_service_lms.constants.CourseConstants.OWNER_ID_INVALID;
import static com.nt.course_service_lms.constants.CourseConstants.TITLE_BLANK;
import static com.nt.course_service_lms.constants.CourseConstants.TITLE_MIN_LENGTH;

/**
 * DTO for creating or updating course information.
 *
 * <p>Used by the controller and service layers to transfer course data.</p>
 *
 * <p><b>Validation Rules:</b></p>
 * <ul>
 *     <li>{@code title} - Required, minimum 3 characters</li>
 *     <li>{@code ownerId} - Required, must be non-null and ≥ 0</li>
 *     <li>{@code description} - Required, minimum 3 characters</li>
 *     <li>{@code courseLevel} - Required, must not be null</li>
 *     <li>{@code isActive} - Required, indicates if course is active</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseInDTO {

    /**
     * Title of the course.
     * <p>Must be non-blank and at least 3 characters long.</p>
     */
    @NotBlank(message = TITLE_BLANK)
    @Size(min = INT_VALUE_3, message = TITLE_MIN_LENGTH)
    private String title;

    /**
     * ID of the user who owns or created the course.
     * <p>Must be non-null and greater than or equal to 0.</p>
     */
    @NotNull(message = OWNER_ID_BLANK)
    @Min(value = 0, message = OWNER_ID_INVALID)
    private Long ownerId;

    /**
     * Brief description of the course.
     * <p>Must be non-blank and at least 3 characters long.</p>
     */
    @NotBlank(message = DESCRIPTION_BLANK)
    @Size(min = INT_VALUE_3, message = DESCRIPTION_MIN_LENGTH)
    private String description;

    /**
     * Difficulty level of the course.
     * <p>Examples: BEGINNER, INTERMEDIATE, ADVANCED.</p>
     * <p>Must be non-null.</p>
     */
    @NotNull(message = COURSE_LEVEL_REQUIRED)
    private String courseLevel;

    /**
     * Indicates whether the course is active.
     * <p>Must be non-null.</p>
     */
    @NotNull(message = "Is Active field is required")
    private Boolean isActive;

    /**
     * Checks if two CourseInDTO objects are equal based on their properties.
     *
     * @param o the object to compare
     * @return true if all fields match, false otherwise
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CourseInDTO that)) {
            return false;
        }
        return Objects.equals(title, that.title)
                && Objects.equals(ownerId, that.ownerId)
                && Objects.equals(description, that.description)
                && Objects.equals(courseLevel, that.courseLevel)
                && Objects.equals(isActive, that.isActive);
    }

    /**
     * Generates hash code based on all fields.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(title, ownerId, description, courseLevel, isActive);
    }
}
