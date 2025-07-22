package com.nt.course_service_lms.dto.inDTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.nt.course_service_lms.constants.BundleConstants.*;
import static com.nt.course_service_lms.constants.BundleConstants.BUNDLE_NAME_INVALID;

@Data
public class UpdateBundleInDTO {
    @NotBlank(message = BUNDLE_NAME_NOT_BLANK)
    @Size(min = INT_VALUE_3, message = BUNDLE_NAME_MIN_LENGTH)
    @Pattern(
            regexp = "^(?!\\d)(?!\\s)[A-Za-z][A-Za-z0-9 ]*(?<!\\s)$",
            message = BUNDLE_NAME_INVALID
    )
    private String bundleName;

    @NotNull(message = "Is Active field is required")
    @JsonProperty("isActive")
    private boolean isActive;

    public UpdateBundleInDTO() {
    }

    public UpdateBundleInDTO(String bundleName, boolean isActive) {
        this.bundleName = bundleName;
        this.isActive = isActive;
    }
}
