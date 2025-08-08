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
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

import static com.nt.course_service_lms.constants.CourseContentConstants.*;

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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseContentInDTO {

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
     * The title of the course content.
     * <p>Required field with a maximum of 100 characters.</p>
     */
    @NotBlank(message = CONTENT_TYPE_NOT_BLANK)
    private String contentType;

    /**
     * The optional URL to additional learning resources.
     * <p>If provided, must be a valid HTTP, HTTPS, or FTP link.</p>
     */
    @NotNull(message = "Resource file should not be empty")
    private MultipartFile file;

    @NotNull(message = "Is Active field is required")
    private boolean isActive;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CourseContentInDTO that = (CourseContentInDTO) o;
        return courseId == that.courseId && isActive == that.isActive && Objects.equals(title, that.title) && Objects.equals(description, that.description) && Objects.equals(contentType, that.contentType) && Objects.equals(file, that.file);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, title, description,contentType, file, isActive);
    }
}
