package com.nt.course_service_lms.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing summary information of a course.
 * <p>
 * This DTO is used to send basic course details to the client, such as title,
 * description, level, and timestamps. It's typically used in course listings
 * or overview pages where full course content is not required.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseSummaryOutDTO {
    /**
     * The title or name of the course.
     */
    private Long courseId;


    /**
     * The title or name of the course.
     */
    private String title;

    /**
     * A short description providing an overview of the course content.
     */
    private String description;

    /**
     * The difficulty or proficiency level of the course.
     * <p>
     * Examples: "Beginner", "Intermediate", "Advanced".
     * </p>
     */
    private String level;

    /**
     * The timestamp when the course was created.
     */
    private LocalDateTime createdAt;

    /**
     * The timestamp when the course details were last updated.
     */
    private LocalDateTime updatedAt;
}
