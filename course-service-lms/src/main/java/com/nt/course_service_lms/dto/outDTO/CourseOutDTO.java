package com.nt.course_service_lms.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing detailed information about a course.
 * <p>
 * This DTO is used to expose course data to clients, including ownership,
 * metadata, and publication status. It is typically returned in course detail
 * views for admin or instructor dashboards.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseOutDTO {

    /**
     * Unique identifier for the course.
     */
    private long courseId;

    /**
     * Unique identifier of the user who owns or created the course.
     */
    private long ownerId;

    /**
     * The title or name of the course.
     */
    private String title;

    /**
     * A brief description summarizing the course content.
     */
    private String description;

    /**
     * The difficulty level of the course.
     * <p>
     * Examples: "Beginner", "Intermediate", "Advanced".
     * </p>
     */
    private String level;

    /**
     * Flag indicating whether the course is currently active (published and visible).
     */
    private boolean active;

    /**
     * Timestamp indicating when the course was created.
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp indicating the last time the course was updated.
     */
    private LocalDateTime updatedAt;
}
