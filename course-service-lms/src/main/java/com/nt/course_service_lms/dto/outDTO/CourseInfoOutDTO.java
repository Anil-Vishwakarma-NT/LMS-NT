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
public class CourseInfoOutDTO {

    private String title;

    private Long courseId;

    private Long ownerId;

    private String description;

    private String courseLevel;

    private boolean isActive;

    private LocalDateTime updatedAt;
}
