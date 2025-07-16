package com.nt.course_service_lms.converters;

import com.nt.course_service_lms.dto.outDTO.UserProgressOutDTO;
import com.nt.course_service_lms.entity.UserProgress;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserProgressConverter {

    /**
     * Converts a UserProgress entity to a UserProgressOutDTO.
     * This method is typically used for retrieving user progress data.
     *
     * @param entity the UserProgress entity to convert
     * @return the corresponding UserProgressOutDTO
     */
    public UserProgressOutDTO toDTO(final UserProgress entity) {
        return UserProgressOutDTO.builder()
                .userId(entity.getUserId())
                .contentId(entity.getContentId())
                .courseId(entity.getCourseId())
                .contentType(entity.getContentType())
                .lastPosition(entity.getLastPosition())
                .contentCompletionPercentage(entity.getContentCompletionPercentage())
                .lastUpdated(entity.getLastUpdated())
                .build();
    }

    /**
     * Converts a UserProgressOutDTO to a UserProgress entity.
     * This method is typically used when updating user progress.
     *
     * @param dto the DTO object containing user progress data
     * @return the corresponding UserProgress entity
     */
    public UserProgress toEntity(final UserProgressOutDTO dto) {
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
