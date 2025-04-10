package com.example.course_service_lms.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Data;

import static com.example.course_service_lms.constants.CourseContentConstants.*;

@Data
public class CourseContentDTO {

    @NotNull(message =  COURSE_ID_NOT_NULL)
    @Min(value = 0, message = COURSE_ID_VALID)
    private long courseId;

    @NotBlank(message = TITLE_NOT_BLANK)
    @Size(max = 100, message = TITLE_SIZE_EXCEED)
    private String title;

    @NotBlank(message = DESCRIPTION_NOT_BLANK)
    @Size(max = 1000, message =  DESCRIPTION_SIZE_EXCEED)
    private String description;

    @NotBlank(message = VIDEO_LINK_NOT_BLANK)
    @Pattern(
            regexp = "^(https?|ftp)://.*$",
            message = VIDEO_LINK_INVALID
    )
    private String videoLink;

    @Pattern(
            regexp = "^$|^(https?|ftp)://.*$",
            message = RESOURCE_LINK_INVALID
    )
    private String resourceLink;

}
