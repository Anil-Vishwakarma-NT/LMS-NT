package com.nt.LMS.converter;

import com.nt.LMS.dto.UserProgressDTO;
import com.nt.LMS.entities.UserProgress;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserProgressConverter {

    public UserProgressDTO toDTO(UserProgress entity) {
        return UserProgressDTO.builder()
                .userId(entity.getUserId())
                .contentId(entity.getContentId())
                .courseId(entity.getCourseId()) // Added courseId
                .contentType(entity.getContentType())
                .lastPosition(entity.getLastPosition())
                .contentCompletionPercentage(entity.getContentCompletionPercentage())
                .courseCompletionPercentage(entity.getCourseCompletionPercentage())
                .courseCompleted(entity.isCourseCompleted())
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
                .contentCompletionPercentage(dto.getContentCompletionPercentage())
                .courseCompletionPercentage(dto.getCourseCompletionPercentage())
                .courseCompleted(dto.isCourseCompleted())
                .lastUpdated(LocalDateTime.now()) // Set current timestamp on entity creation
                .build();
    }
}