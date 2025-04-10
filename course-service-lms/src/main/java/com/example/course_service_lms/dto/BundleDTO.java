package com.example.course_service_lms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import static com.example.course_service_lms.constants.BundleConstants.*;

@Data


public class BundleDTO {
    @NotBlank(message =  BUNDLE_NAME_NOT_BLANK)
    @Size(min = 3, message =  BUNDLE_NAME_MIN_LENGTH)
    @Pattern(
            regexp = "^(?!\\d)(?!\\s)[A-Za-z][A-Za-z0-9]*(?<!\\s)$",
            message = BUNDLE_NAME_INVALID
    )
    private String bundleName;

    public BundleDTO(String bundleName) {
        this.bundleName = bundleName;
    }

    public BundleDTO() {
    }
}


