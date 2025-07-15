package com.nt.course_service_lms.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseOutDTO {
    private long courseId;
    private long ownerId;
    private String title;
    private String description;
    private String level;
    private boolean Active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
