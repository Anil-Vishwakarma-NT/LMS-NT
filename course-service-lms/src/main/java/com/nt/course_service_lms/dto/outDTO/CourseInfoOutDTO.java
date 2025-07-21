package com.nt.course_service_lms.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing basic course information.
 * <p>
 * This DTO is typically used to display a summarized view of a course,
 * such as in listings or search results, including metadata like the title,
 * owner, level, and last update timestamp.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseInfoOutDTO {

    /**
     * The title or name of the course.
     */
    private String title;

    /**
     * Unique identifier of the course.
     */
    private Long courseId;

    /**
     * Unique identifier of the course owner or creator.
     */
    private Long ownerId;

    /**
     * A brief description summarizing what the course is about.
     */
    private String description;

    /**
     * The difficulty level of the course.
     * <p>
     * Examples: "Beginner", "Intermediate", "Advanced".
     * </p>
     */
    private String courseLevel;

    /**
     * Indicates whether the course is currently active or published.
     */
    private boolean isActive;

    /**
     * Timestamp representing the last time the course information was updated.
     */
    private LocalDateTime updatedAt;
}
