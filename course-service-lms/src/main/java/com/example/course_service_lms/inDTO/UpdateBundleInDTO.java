package com.example.course_service_lms.inDTO;

import jakarta.validation.constraints.*;
import lombok.Data;

import static com.example.course_service_lms.constants.BundleConstants.*;
import static com.example.course_service_lms.constants.BundleConstants.BUNDLE_NAME_INVALID;

@Data
public class UpdateBundleInDTO {
    @NotBlank(message = BUNDLE_NAME_NOT_BLANK)
    @Size(min = INT_VALUE_3, message = BUNDLE_NAME_MIN_LENGTH)
    @Pattern(
            regexp = "^(?!\\d)(?!\\s)[A-Za-z][A-Za-z0-9]*(?<!\\s)$",
            message = BUNDLE_NAME_INVALID
    )
    private String bundleName;

    @NotNull(message = "Is Active field is required")
    private boolean isActive;

}
