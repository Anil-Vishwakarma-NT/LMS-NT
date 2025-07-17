package com.nt.course_service_lms.dto.inDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

import static com.nt.course_service_lms.constants.BundleConstants.BUNDLE_NAME_INVALID;
import static com.nt.course_service_lms.constants.BundleConstants.BUNDLE_NAME_MIN_LENGTH;
import static com.nt.course_service_lms.constants.BundleConstants.BUNDLE_NAME_NOT_BLANK;
import static com.nt.course_service_lms.constants.BundleConstants.INT_VALUE_3;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UpdateBundleInDTO that = (UpdateBundleInDTO) o;
        return isActive == that.isActive && Objects.equals(bundleName, that.bundleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bundleName, isActive);
    }
}
