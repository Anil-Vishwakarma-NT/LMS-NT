package com.nt.course_service_lms.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) representing user progress information for course content.
 * This class is used to transfer user progress data from the service layer to external consumers,
 * providing detailed tracking information about a user's interaction with course materials.
 *
 * <p>The DTO supports tracking progress for different types of content including videos and PDFs,
 * with position-based tracking (timestamps for videos, page numbers for PDFs) and completion
 * percentage calculation.</p>
 *
 * @author Course Service Team
 * @version 1.0
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProgressOutDTO {

    /**
     * The unique identifier of the user whose progress is being tracked.
     * This corresponds to the user ID in the user management system.
     */
    private Long userId;

    /**
     * The unique identifier of the specific content item being tracked.
     * This could reference a video, PDF, or other course material.
     */
    private Long contentId;

    /**
     * The unique identifier of the course that contains the tracked content.
     * Used for organizing and filtering progress data by course.
     */
    private Long courseId;

    /**
     * The type of content being tracked.
     * Supported values include 'video' for video content and 'pdf' for PDF documents.
     * This determines how the lastPosition field should be interpreted.
     */
    private String contentType;

    /**
     * The user's last known position within the content.
     * For video content, this represents the timestamp (in seconds) where the user last stopped.
     * For PDF content, this represents the page number the user last viewed.
     */
    private double lastPosition;

    /**
     * The completion percentage for this specific content item.
     * Represented as a value between 0.0 and 100.0, where 100.0 indicates
     * the content has been fully completed by the user.
     */
    private double contentCompletionPercentage;

    /**
     * The timestamp when this progress record was last updated.
     * Used for tracking the most recent user interaction with the content
     * and for data synchronization purposes.
     */
    private LocalDateTime lastUpdated;

    /**
     * The timestamp when the user first completed this content item.
     * This field is null if the content has not been completed yet.
     * Used for completion tracking and analytics purposes.
     */
    private LocalDateTime firstCompletedAt;
}
