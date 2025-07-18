package com.nt.course_service_lms.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing the progress of a course along with metadata.
 * <p>
 * This DTO is used to send course progress information for a user,
 * including the percentage of course completion and the timestamp of the first completion.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseProgressWithMetaDTO {

    /**
     * The percentage of the course completed by the user.
     * <p>
     * Represented as a value between 0.0 and 100.0.
     * </p>
     */
    private double courseCompletionPercentage;

    /**
     * Timestamp indicating when the course was first completed by the user.
     * <p>
     * If the course has not yet been completed, this may be {@code null}.
     * </p>
     */
    private LocalDateTime firstCompletedAt;
}
