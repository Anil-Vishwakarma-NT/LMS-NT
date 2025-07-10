package com.nt.user_service_lms.dto.inDTO;

import com.nt.user_service_lms.validation.ValidateEnrollmentDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for updating enrollment information.
 * Represents the input data for modifying existing enrollment records
 * in the learning management system. Supports updates for both individual
 * users and groups enrolled in courses or bundles.
 *
 * @author Generated
 * @version 1.0
 * @since 1.0
 */
@Data
@ValidateEnrollmentDTO
public class UpdateEnrollmentInDTO {

    /**
     * The unique identifier of the user whose enrollment is being updated.
     * Used when updating individual user enrollments.
     */
    private Long userId;

    /**
     * The unique identifier of the group whose enrollment is being updated.
     * Used when updating group-based enrollments.
     */
    private Long groupId;

    /**
     * The unique identifier of the course in the enrollment.
     * Used when the enrollment is for a specific course.
     */
    private Long courseId;

    /**
     * The unique identifier of the bundle in the enrollment.
     * Used when the enrollment is for a course bundle.
     */
    private Long bundleId;

    /**
     * The unique identifier of the manager performing the enrollment update.
     * This field is required and must be a positive number.
     * Represents the user who has authority to modify the enrollment.
     */
    @NotNull(message = "Manager ID cannot be empty")
    @Positive(message = "Valid manager ID required")
    private Long managerId;

    /**
     * The updated status of the enrollment.
     * Represents the current state of the enrollment (e.g., ACTIVE, INACTIVE, COMPLETED).
     */
    private String status;

    /**
     * The updated deadline for completing the enrollment.
     * Represents the new date and time by which the enrollment should be completed.
     */
    private LocalDateTime deadline;
}
