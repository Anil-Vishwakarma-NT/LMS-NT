package com.example.course_service_lms.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Bundle entity responses.
 * <p>
 * This DTO is used to transfer Bundle data from the service layer to the presentation layer,
 * ensuring that only necessary information is exposed to the client.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BundleOutDTO {

    /**
     * Unique identifier for the bundle.
     */
    private Long bundleId;

    /**
     * Name of the bundle.
     */
    private String bundleName;

    /**
     * Indicates whether the bundle is active or not.
     */
    private boolean isActive;

    /**
     * Timestamp when the bundle was created.
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp when the bundle was last updated.
     */
    private LocalDateTime updatedAt;
}