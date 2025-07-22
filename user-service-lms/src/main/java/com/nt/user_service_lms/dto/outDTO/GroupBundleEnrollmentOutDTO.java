package com.nt.user_service_lms.dtoTest.outDTO;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for group bundle enrollment details.
 */
@Data
public class GroupBundleEnrollmentOutDTO {
    /**
     * The unique identifier of the group.
     */
    private Long groupId;

    /**
     * The unique identifier of the bundle.
     */
    private Long bundleId;

    /**
     * The ID of the user who assigned the bundle.
     */
    private Long assignedById;

    /**
     * The name of the group.
     */
    private String groupName;

    /**
     * The name of the bundle.
     */
    private String bundleName;

    /**
     * The name of the user who assigned the bundle.
     */
    private String assignedByName;

    /**
     * The deadline for the bundle assignment.
     */
    private LocalDateTime deadline;
}
