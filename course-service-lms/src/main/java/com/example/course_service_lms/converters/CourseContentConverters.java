package com.example.course_service_lms.converters;

import com.example.course_service_lms.dto.CourseContentDTO;
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
     * Converts a {@link CourseContentDTO} to a {@link CourseContent} entity.
     * This method is typically used when creating or updating course content.
     *
     * @param courseContentDTO the DTO object containing course content data
     * @return the corresponding CourseContent entity
     */
    public static CourseContent courseContentDtoToCourseContent(final CourseContentDTO courseContentDTO) {
        CourseContent courseContent = new CourseContent();
        courseContent.setCourseId(courseContentDTO.getCourseId());
        courseContent.setTitle(courseContentDTO.getTitle());
        courseContent.setDescription(courseContentDTO.getDescription());
        courseContent.setVideoLink(courseContentDTO.getVideoLink());
        courseContent.setResourceLink(courseContentDTO.getResourceLink());
        return courseContent;
    }
}
