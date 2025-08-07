package com.nt.course_service_lms.dto.inDTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

import static com.nt.course_service_lms.constants.CourseContentConstants.COURSE_ID_NOT_NULL;
import static com.nt.course_service_lms.constants.CourseContentConstants.COURSE_ID_VALID;
import static com.nt.course_service_lms.constants.CourseContentConstants.DESCRIPTION_NOT_BLANK;
import static com.nt.course_service_lms.constants.CourseContentConstants.DESCRIPTION_SIZE_EXCEED;
import static com.nt.course_service_lms.constants.CourseContentConstants.DESCRIPTION_SIZE_EXCEED_VALUE;
import static com.nt.course_service_lms.constants.CourseContentConstants.RESOURCE_LINK_INVALID;
import static com.nt.course_service_lms.constants.CourseContentConstants.TITLE_NOT_BLANK;
import static com.nt.course_service_lms.constants.CourseContentConstants.TITLE_SIZE_EXCEED;
import static com.nt.course_service_lms.constants.CourseContentConstants.TITLE_SIZE_EXCEED_VALUE;

/**
 * Data Transfer Object (DTO) for transferring course content data between
 * the controller and service layers in the LMS.
 *
 * <p>This DTO is used when adding new course content such as video lectures,
 * summaries, and additional resource links to a course.</p>
 *
 * <p><b>Validation Constraints:</b></p>
 * <ul>
 *     <li><b>courseId:</b> Required. Must be ≥ 0.</li>
 *     <li><b>title:</b> Required. Maximum 100 characters.</li>
 *     <li><b>description:</b> Required. Maximum 1000 characters.</li>
 *     <li><b>resourceLink:</b> Optional. If present, must be a valid HTTP, HTTPS, or FTP URL.</li>
 *     <li><b>isActive:</b> Required. Indicates whether the content is active.</li>
 * </ul>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseContentInDTO {

    /**
     * The ID of the course this content belongs to.
     *
     * <p>Must not be null and must be zero or positive.</p>
     */
    @NotNull(message = COURSE_ID_NOT_NULL)
    @Min(value = 0, message = COURSE_ID_VALID)
    private long courseId;

    /**
     * The title of the course content.
     *
     * <p>This field is required and must not be blank.
     */
    @NotBlank(message = TITLE_NOT_BLANK)
    @Size(max = TITLE_SIZE_EXCEED_VALUE, message = TITLE_SIZE_EXCEED)
    private String title;

    /**
     * The description of the course content.
     *
     * <p>This field is required and must not be blank.
     */
    @NotBlank(message = DESCRIPTION_NOT_BLANK)
    @Size(max = DESCRIPTION_SIZE_EXCEED_VALUE, message = DESCRIPTION_SIZE_EXCEED)
    private String description;

    /**
     * A link to additional learning resources for the content.
     *
     * <p>This field is optional, but if provided, must be a valid URL
     * starting with http, https, or ftp.</p>
     */
    @NotNull(message = "Resource Link should not be empty")
    @Pattern(
            regexp = "^$|^(https?|ftp)://.*$",
            message = RESOURCE_LINK_INVALID
    )
    private String resourceLink;

    /**
     * Indicates whether the course content is currently active.
     *
     * <p>This field is required.</p>
     */
    @NotNull(message = "Is Active field is required")
    private boolean isActive;

    /**
     * Custom equality check based on content fields.
     *
     * @param o the other object
     * @return true if all fields match
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CourseContentInDTO that)) {
            return false;
        }
        return courseId == that.courseId
                && isActive == that.isActive
                && Objects.equals(title, that.title)
                && Objects.equals(description, that.description)
                && Objects.equals(resourceLink, that.resourceLink);
    }

    /**
     * Generates a consistent hash code based on field values.
     *
     * @return hash code for the object
     */
    @Override
    public int hashCode() {
        return Objects.hash(courseId, title, description, resourceLink, isActive);
    }
}
