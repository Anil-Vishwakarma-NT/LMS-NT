package com.example.course_service_lms.converters;

import com.example.course_service_lms.dto.CourseDTO;
import com.example.course_service_lms.entity.Course;
import com.example.course_service_lms.entity.CourseLevel;
import com.example.course_service_lms.utils.StringUtils;

import java.time.LocalDateTime;
import java.util.Locale;

/**
 * Utility class for converting between {@link CourseDTO} and {@link Course} entity.
 * Provides helper methods for mapping data from the DTO layer to the entity layer.
 */
public final class CourseConvertors {

    private CourseConvertors() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    /**
     * Converts a {@link CourseDTO} to a {@link Course} entity.
     * This method formats the title and description to proper case,
     * parses the course level enum from string (case-insensitive),
     * and maps all relevant fields.
     *
     * @param courseDTO the data transfer object containing course input data
     * @return the constructed {@link Course} entity
     */
    public static Course courseDtoToCourse(final CourseDTO courseDTO) {
        Course course = new Course();
        course.setTitle(StringUtils.toProperCase(courseDTO.getTitle()));
        course.setOwnerId(courseDTO.getOwnerId());
        course.setDescription(StringUtils.toProperCase(courseDTO.getDescription()));
        course.setLevel(CourseLevel.valueOf(courseDTO.getCourseLevel().toUpperCase(Locale.ROOT)));
        course.setActive(courseDTO.isActive());
        return course;
    }
}

