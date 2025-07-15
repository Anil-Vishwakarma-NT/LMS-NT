package com.nt.course_service_lms.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO representing summary information of a course.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseSummaryOutDTO {
    private String title;
    private String description;
    private String level;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
