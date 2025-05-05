package com.nt.LMS.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProgressDTO {
    private int userId;
    private int contentId;
    private int courseId; // Added courseId to match entity
    private String contentType;
    private double lastPosition;
    private double contentCompletionPercentage;
    private double courseCompletionPercentage;
    private boolean courseCompleted;
    private LocalDateTime lastUpdated; // Changed to LocalDateTime for consistency
}