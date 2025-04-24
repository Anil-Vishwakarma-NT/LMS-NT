package com.example.course_service_lms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.example.course_service_lms.constants.BundleConstants.BUNDLE_NAME_INVALID;
import static com.example.course_service_lms.constants.BundleConstants.BUNDLE_NAME_MIN_LENGTH;
import static com.example.course_service_lms.constants.BundleConstants.INT_VALUE_3;
import static com.example.course_service_lms.constants.BundleConstants.BUNDLE_NAME_NOT_BLANK;

/**
 * BundleDTO is a Data Transfer Object used for creating or updating Bundle entities.
 * <p>
 * This class enforces validation rules on the bundle name input and acts as an abstraction layer
 * to decouple the internal Bundle entity structure from external clients.
 * </p>
 *
 * <p><b>Validation Rules:</b></p>
 * <ul>
 *     <li>{@code @NotBlank} - bundle name must not be empty or null</li>
 *     <li>{@code @Size(min = 3)} - must have at least 3 characters</li>
 *     <li>{@code @Pattern} - must start with an alphabet character,
 *      cannot start or end with whitespace or a digit, and must be alphanumeric</li>
 * </ul>
 */
@Data
public class BundleDTO {

    /**
     * The name of the bundle.
     * <p>
     * This field must:
     * <ul>
     *     <li>Not be blank</li>
     *     <li>Have a minimum of 3 characters</li>
     *     <li>Start with a letter and contain only alphanumeric characters</li>
     *     <li>Not start or end with a whitespace or a digit</li>
     * </ul>
     * </p>
     */
    @NotBlank(message = BUNDLE_NAME_NOT_BLANK)
    @Size(min = INT_VALUE_3, message = BUNDLE_NAME_MIN_LENGTH)
    @Pattern(
            regexp = "^(?!\\d)(?!\\s)[A-Za-z][A-Za-z0-9]*(?<!\\s)$",
            message = BUNDLE_NAME_INVALID
    )
    private String bundleName;

    @NotNull(message = "Is Active field is required")
    private boolean isActive;

    public BundleDTO(String bundleName, boolean isActive) {
        this.bundleName = bundleName;
        this.isActive = isActive;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BundleDTO bundleDTO = (BundleDTO) o;
        return isActive == bundleDTO.isActive && Objects.equals(bundleName, bundleDTO.bundleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bundleName, isActive);
    }

    /**
     * No-args constructor for frameworks and serialization tools.
     */
    public BundleDTO() {
    }
}
