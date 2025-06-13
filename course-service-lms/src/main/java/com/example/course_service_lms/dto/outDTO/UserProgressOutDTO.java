package com.example.course_service_lms.dto.outDTO;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProgressOutDTO {
    private int userId;
    private int contentId;
    private int courseId; // Course ID included for tracking
    private String contentType; // 'video' or 'pdf'
    private double lastPosition; // Timestamp or page number
    private double contentCompletionPercentage; // Individual content progress
    private LocalDateTime lastUpdated; // Timestamp for tracking updates
}