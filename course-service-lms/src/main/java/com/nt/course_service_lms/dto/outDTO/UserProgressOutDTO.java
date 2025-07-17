package com.nt.course_service_lms.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProgressOutDTO {
    private Long userId;
    private Long contentId;
    private Long courseId; // Course ID included for tracking
    private String contentType; // 'video' or 'pdf'
    private double lastPosition; // Timestamp or page number
    private double contentCompletionPercentage; // Individual content progress
    private LocalDateTime lastUpdated; // Timestamp for tracking updates
    private LocalDateTime firstCompletedAt;
}