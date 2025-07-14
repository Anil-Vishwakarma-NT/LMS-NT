package com.nt.course_service_lms.dto.inDTO;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Objects;

import static com.nt.course_service_lms.constants.CourseContentConstants.*;

@Data
public class UpdateCourseContentInDTO {

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
     * The optional URL to additional learning resources.
     * <p>If provided, must be a valid HTTP, HTTPS, or FTP link.</p>
     */
    @Pattern(
            regexp = "^$|^(https?|ftp)://.*$",
            message = RESOURCE_LINK_INVALID
    )
    private String resourceLink;

    @NotNull(message = "Is Active field is required")
    private boolean isActive;

    public UpdateCourseContentInDTO() {
    }

    public UpdateCourseContentInDTO(long courseId, String title, String description, String resourceLink, boolean isActive) {
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.resourceLink = resourceLink;
        this.isActive = isActive;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UpdateCourseContentInDTO that = (UpdateCourseContentInDTO) o;
        return courseId == that.courseId && isActive == that.isActive && Objects.equals(title, that.title) && Objects.equals(description, that.description) && Objects.equals(resourceLink, that.resourceLink);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, title, description, resourceLink, isActive);
    }
}
