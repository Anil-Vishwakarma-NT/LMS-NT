package com.example.course_service_lms.inDTO;

import jakarta.validation.constraints.*;
import lombok.Data;

import static com.example.course_service_lms.constants.CourseContentConstants.*;

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

}
