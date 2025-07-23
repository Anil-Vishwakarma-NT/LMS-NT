package com.nt.course_service_lms.converters;

import com.nt.course_service_lms.dto.inDTO.UserResponseInDTO;
import com.nt.course_service_lms.dto.inDTO.UserResponseUpdateInDTO;
import com.nt.course_service_lms.dto.outDTO.UserResponseOutDTO;
import com.nt.course_service_lms.entity.UserResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Converter class for UserResponse entity and DTOs.
 * Handles conversion between entity and data transfer objects.
 */
@Component
public class UserResponseConverter {

    /**
     * Converts UserResponseInDTO to UserResponse entity.
     *
     * @param inDTO the input DTO
     * @return UserResponse entity
     */
    public UserResponse convertToEntity(final UserResponseInDTO inDTO) {
        if (inDTO == null) {
            return null;
        }

        UserResponse entity = new UserResponse();
        entity.setUserId(inDTO.getUserId());
        entity.setQuizId(inDTO.getQuizId());
        entity.setQuestionId(inDTO.getQuestionId());
        entity.setAttempt(inDTO.getAttempt());
        entity.setUserAnswer(inDTO.getUserAnswer());
        entity.setAnsweredAt(inDTO.getAnsweredAt() != null ? inDTO.getAnsweredAt() : LocalDateTime.now());

        return entity;
    }

    /**
     * Converts UserResponse entity to UserResponseOutDTO.
     *
     * @param entity the UserResponse entity
     * @return UserResponseOutDTO
     */
    public UserResponseOutDTO convertToOutDTO(final UserResponse entity) {
        if (entity == null) {
            return null;
        }

        return new UserResponseOutDTO(
                entity.getResponseId(),
                entity.getUserId(),
                entity.getQuizId(),
                entity.getQuestionId(),
                entity.getAttempt(),
                entity.getUserAnswer(),
                entity.getIsCorrect(),
                entity.getPointsEarned(),
                entity.getAnsweredAt()
        );
    }

    /**
     * Updates UserResponse entity with data from UserResponseUpdateInDTO.
     *
     * @param entity    the existing entity
     * @param updateDTO the update DTO
     * @return updated UserResponse entity
     */
    public UserResponse updateEntityFromDTO(final UserResponse entity, final UserResponseUpdateInDTO updateDTO) {
        if (entity == null || updateDTO == null) {
            return entity;
        }

        entity.setUserAnswer(updateDTO.getUserAnswer());
        entity.setIsCorrect(updateDTO.getIsCorrect());
        entity.setPointsEarned(updateDTO.getPointsEarned());

        if (updateDTO.getAnsweredAt() != null) {
            entity.setAnsweredAt(updateDTO.getAnsweredAt());
        }

        return entity;
    }

    /**
     * Converts list of UserResponse entities to list of UserResponseOutDTOs.
     *
     * @param entities list of UserResponse entities
     * @return list of UserResponseOutDTOs
     */
    public List<UserResponseOutDTO> convertToOutDTOList(final List<UserResponse> entities) {
        if (entities == null) {
            return null;
        }

        return entities.stream()
                .map(this::convertToOutDTO)
                .collect(Collectors.toList());
    }
}
