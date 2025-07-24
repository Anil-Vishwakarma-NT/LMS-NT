package com.nt.user_service_lms.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupBundleOutDTO {


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
    /**
     * Progress of the bundle.
     */
    private double progress;

    /**
     * enrollments in the bundle.
     */
    private Long enrols;
}
