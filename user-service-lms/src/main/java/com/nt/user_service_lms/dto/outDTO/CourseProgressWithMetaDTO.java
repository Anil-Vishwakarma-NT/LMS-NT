package com.nt.user_service_lms.dtoTest.outDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO representing course progress with metadata.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseProgressWithMetaDTO {
    /**
     * The percentage of course completion.
     */
    private double courseCompletionPercentage;

    /**
     * The timestamp when the course was first completed.
     */
    private LocalDateTime firstCompletedAt;
}
