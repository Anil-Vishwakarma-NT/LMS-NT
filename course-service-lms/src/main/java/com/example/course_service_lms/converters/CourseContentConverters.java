package com.example.course_service_lms.converters;

import com.example.course_service_lms.dto.inDTO.CourseContentInDTO;
import com.example.course_service_lms.entity.CourseContent;

/**
 * Utility class for converting between CourseContentDTO and CourseContent entity.
 * Contains helper methods to map data for service and controller layers.
 */
public final class CourseContentConverters {

    private CourseContentConverters() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    /**
     * Converts a {@link CourseContentInDTO} to a {@link CourseContent} entity.
     * This method is typically used when creating or updating course content.
     *
     * @param courseContentInDTO the DTO object containing course content data
     * @return the corresponding CourseContent entity
     */
    public static CourseContent courseContentDtoToCourseContent(final CourseContentInDTO courseContentInDTO) {
        CourseContent courseContent = new CourseContent();
        courseContent.setCourseId(courseContentInDTO.getCourseId());
        courseContent.setTitle(courseContentInDTO.getTitle());
        courseContent.setDescription(courseContentInDTO.getDescription());
        courseContent.setResourceLink(courseContentInDTO.getResourceLink());
        courseContent.setActive(courseContentInDTO.isActive());
        return courseContent;
    }
}
