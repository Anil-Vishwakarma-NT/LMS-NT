package com.example.course_service_lms.converters;

import com.example.course_service_lms.dto.inDTO.CourseContentInDTO;
import com.example.course_service_lms.dto.inDTO.UpdateCourseContentInDTO;
import com.example.course_service_lms.dto.outDTO.CourseContentOutDTO;
import com.example.course_service_lms.entity.CourseContent;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for converting between CourseContent DTOs and entity.
 * Contains helper methods to map data for service and controller layers.
 */
public final class CourseContentConverters {

    private CourseContentConverters() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Converts a {@link CourseContentInDTO} to a {@link CourseContent} entity.
     * This method is typically used when creating course content.
     *
     * @param courseContentInDTO the DTO object containing course content data
     * @return the corresponding CourseContent entity
     */
    public static CourseContent courseContentInDtoToEntity(final CourseContentInDTO courseContentInDTO) {
        if (courseContentInDTO == null) {
            return null;
        }

        CourseContent courseContent = new CourseContent();
        courseContent.setCourseId(courseContentInDTO.getCourseId());
        courseContent.setTitle(courseContentInDTO.getTitle());
        courseContent.setDescription(courseContentInDTO.getDescription());
        courseContent.setResourceLink(courseContentInDTO.getResourceLink());
        courseContent.setActive(courseContentInDTO.isActive());
        return courseContent;
    }

    /**
     * Converts a {@link CourseContent} entity to a {@link CourseContentOutDTO}.
     * This method is typically used when returning course content data.
     *
     * @param courseContent the CourseContent entity
     * @return the corresponding CourseContentOutDTO
     */
    public static CourseContentOutDTO entityToOutDto(final CourseContent courseContent) {
        if (courseContent == null) {
            return null;
        }

        CourseContentOutDTO outDTO = new CourseContentOutDTO();
        outDTO.setCourseContentId(courseContent.getCourseContentId());
        outDTO.setCourseId(courseContent.getCourseId());
        outDTO.setTitle(courseContent.getTitle());
        outDTO.setDescription(courseContent.getDescription());
        outDTO.setResourceLink(courseContent.getResourceLink());
        outDTO.setActive(courseContent.isActive());
        outDTO.setCreatedAt(courseContent.getCreatedAt());
        outDTO.setUpdatedAt(courseContent.getUpdatedAt());
        return outDTO;
    }

    /**
     * Converts a list of {@link CourseContent} entities to a list of {@link CourseContentOutDTO}.
     *
     * @param courseContents the list of CourseContent entities
     * @return the list of corresponding CourseContentOutDTO objects
     */
    public static List<CourseContentOutDTO> entityListToOutDtoList(final List<CourseContent> courseContents) {
        if (courseContents == null) {
            return null;
        }

        return courseContents.stream()
                .map(CourseContentConverters::entityToOutDto)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing {@link CourseContent} entity with data from {@link UpdateCourseContentInDTO}.
     * This method is used during update operations.
     *
     * @param existingEntity the existing CourseContent entity to update
     * @param updateDTO the DTO containing updated data
     */
    public static void updateEntityFromDto(final CourseContent existingEntity,
                                           final UpdateCourseContentInDTO updateDTO) {
        if (existingEntity == null || updateDTO == null) {
            return;
        }

        existingEntity.setCourseId(updateDTO.getCourseId());
        existingEntity.setTitle(updateDTO.getTitle());
        existingEntity.setDescription(updateDTO.getDescription());
        existingEntity.setResourceLink(updateDTO.getResourceLink());
        existingEntity.setActive(updateDTO.isActive());
    }

    /**
     * Converts an {@link UpdateCourseContentInDTO} to a {@link CourseContent} entity.
     * This is a convenience method for scenarios where a new entity needs to be created from update DTO.
     *
     * @param updateDTO the update DTO
     * @return the corresponding CourseContent entity
     */
    public static CourseContent updateDtoToEntity(final UpdateCourseContentInDTO updateDTO) {
        if (updateDTO == null) {
            return null;
        }

        CourseContent courseContent = new CourseContent();
        courseContent.setCourseId(updateDTO.getCourseId());
        courseContent.setTitle(updateDTO.getTitle());
        courseContent.setDescription(updateDTO.getDescription());
        courseContent.setResourceLink(updateDTO.getResourceLink());
        courseContent.setActive(updateDTO.isActive());
        return courseContent;
    }

    /**
     * Legacy method for backward compatibility.
     * @deprecated Use {@link #courseContentInDtoToEntity(CourseContentInDTO)} instead
     */
    @Deprecated
    public static CourseContent courseContentDtoToCourseContent(final CourseContentInDTO courseContentInDTO) {
        return courseContentInDtoToEntity(courseContentInDTO);
    }
}