package com.example.course_service_lms.converters;

import com.example.course_service_lms.dto.UserProgressDTO;
import com.example.course_service_lms.entity.UserProgress;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserProgressConverter {

    public UserProgressDTO toDTO(UserProgress entity) {
        return UserProgressDTO.builder()
                .userId(entity.getUserId())
                .contentId(entity.getContentId())
                .courseId(entity.getCourseId()) // Course ID retained
                .contentType(entity.getContentType())
                .lastPosition(entity.getLastPosition())
                .contentCompletionPercentage(entity.getContentCompletionPercentage()) // Only content percentage, backend will calculate course progress
                .lastUpdated(entity.getLastUpdated()) // Direct LocalDateTime mapping
                .build();
    }

    public UserProgress toEntity(UserProgressDTO dto) {
        return UserProgress.builder()
                .userId(dto.getUserId())
                .contentId(dto.getContentId())
                .courseId(dto.getCourseId()) // Added courseId
                .contentType(dto.getContentType())
                .lastPosition(dto.getLastPosition())
                .contentCompletionPercentage(dto.getContentCompletionPercentage()) // Only individual content tracking
                .lastUpdated(LocalDateTime.now()) // Ensure timestamp consistency
                .build();
    }
}