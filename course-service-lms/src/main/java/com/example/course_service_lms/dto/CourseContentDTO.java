package com.example.course_service_lms.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Objects;

import static com.example.course_service_lms.constants.CourseContentConstants.COURSE_ID_NOT_NULL;
import static com.example.course_service_lms.constants.CourseContentConstants.COURSE_ID_VALID;
import static com.example.course_service_lms.constants.CourseContentConstants.DESCRIPTION_NOT_BLANK;
import static com.example.course_service_lms.constants.CourseContentConstants.DESCRIPTION_SIZE_EXCEED;
import static com.example.course_service_lms.constants.CourseContentConstants.DESCRIPTION_SIZE_EXCEED_VALUE;
import static com.example.course_service_lms.constants.CourseContentConstants.RESOURCE_LINK_INVALID;
import static com.example.course_service_lms.constants.CourseContentConstants.TITLE_NOT_BLANK;
import static com.example.course_service_lms.constants.CourseContentConstants.TITLE_SIZE_EXCEED;
import static com.example.course_service_lms.constants.CourseContentConstants.TITLE_SIZE_EXCEED_VALUE;
import static com.example.course_service_lms.constants.CourseContentConstants.VIDEO_LINK_INVALID;
import static com.example.course_service_lms.constants.CourseContentConstants.VIDEO_LINK_NOT_BLANK;

/**
 * Data Transfer Object (DTO) for transferring course content data between the
 * controller and service layers in the LMS.
 *
 * <p>This DTO is used when adding or retrieving course content (such as
 * video lectures, descriptions, and resource links) for a specific course.</p>
 *
 * <p><b>Validation Rules:</b></p>
 * <ul>
 *     <li>{@code courseId} - Must be non-null and ≥ 0</li>
 *     <li>{@code title} - Required, max 100 characters</li>
 *     <li>{@code description} - Required, max 1000 characters</li>
 *     <li>{@code videoLink} - Must be a valid URL (http, https, ftp)</li>
 *     <li>{@code resourceLink} - Optional, but if provided must be a valid URL</li>
 * </ul>
 */
@Data
public class CourseContentDTO {

    /**
     * The ID of the course to which this content belongs.
     * <p>Must be non-null and greater than or equal to 0.</p>
     */
    @NotNull(message = COURSE_ID_NOT_NULL)
    @Min(value = 0, message = COURSE_ID_VALID)
    private long courseId;

    /**
     * The title of the course content.
     * <p>Required field with a maximum of 100 characters.</p>
     */
    @NotBlank(message = TITLE_NOT_BLANK)
    @Size(max = TITLE_SIZE_EXCEED_VALUE, message = TITLE_SIZE_EXCEED)
    private String title;

    /**
     * The description or summary of the content.
     * <p>Required field with a maximum of 1000 characters.</p>
     */
    @NotBlank(message = DESCRIPTION_NOT_BLANK)
    @Size(max = DESCRIPTION_SIZE_EXCEED_VALUE, message = DESCRIPTION_SIZE_EXCEED)
    private String description;

    /**
     * The URL of the video lecture associated with this content.
     * <p>Must be a valid HTTP, HTTPS, or FTP link.</p>
     */
    @NotBlank(message = VIDEO_LINK_NOT_BLANK)
    @Pattern(
            regexp = "^(https?|ftp)://.*$",
            message = VIDEO_LINK_INVALID
    )
    private String videoLink;

    /**
     * The optional URL to additional learning resources.
     * <p>If provided, must be a valid HTTP, HTTPS, or FTP link.</p>
     */
    @Pattern(
            regexp = "^$|^(https?|ftp)://.*$",
            message = RESOURCE_LINK_INVALID
    )
    private String resourceLink;
    /**
     * All-arguments constructor for initializing all fields.
     *
     * @param courseId      ID of the course
     * @param title         Title of the content
     * @param description   Description of the content
     * @param videoLink     URL to the video lecture
     * @param resourceLink  URL to additional resources
     */
    public CourseContentDTO(final long courseId, final String title, final String description,
                            final String videoLink, final String resourceLink) {
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.videoLink = videoLink;
        this.resourceLink = resourceLink;
    }

    /**
     * No-args constructor for frameworks that require default instantiation.
     */
    public CourseContentDTO() {
    }

    /**
     * Custom equality check based on all fields.
     *
     * @param o Object to compare
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CourseContentDTO that = (CourseContentDTO) o;
        return courseId == that.courseId
                && Objects.equals(title, that.title)
                && Objects.equals(description, that.description)
                && Objects.equals(videoLink, that.videoLink)
                && Objects.equals(resourceLink, that.resourceLink);
    }

    /**
     * Custom hash code generation based on all fields.
     *
     * @return computed hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(courseId, title, description, videoLink, resourceLink);
    }
}
