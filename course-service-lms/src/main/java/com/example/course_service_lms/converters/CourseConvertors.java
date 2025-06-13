package com.example.course_service_lms.converters;

import com.example.course_service_lms.dto.inDTO.CourseInDTO;
import com.example.course_service_lms.dto.inDTO.UpdateCourseInDTO;
import com.example.course_service_lms.dto.outDTO.CourseInfoOutDTO;
import com.example.course_service_lms.dto.outDTO.CourseOutDTO;
import com.example.course_service_lms.dto.outDTO.CourseSummaryOutDTO;
import com.example.course_service_lms.entity.Course;
import com.example.course_service_lms.utils.StringUtils;

/**
 * Utility class for converting between DTOs and Course entity.
 * Provides comprehensive mapping methods for all course-related conversions.
 */
public final class CourseConvertors {

    private CourseConvertors() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Converts a {@link CourseInDTO} to a {@link Course} entity.
     * Formats title and description to proper case and maps all fields.
     *
     * @param courseInDTO the input DTO containing course data
     * @return the constructed {@link Course} entity
     */
    public static Course courseInDTOToCourse(final CourseInDTO courseInDTO) {
        Course course = new Course();
        course.setTitle(StringUtils.toProperCase(courseInDTO.getTitle()));
        course.setOwnerId(courseInDTO.getOwnerId());
        course.setDescription(StringUtils.toProperCase(courseInDTO.getDescription()));
        course.setLevel(courseInDTO.getCourseLevel());
        course.setActive(courseInDTO.isActive());
        return course;
    }

    public static CourseOutDTO courseToCourseOutDTO(final Course course) {
        CourseOutDTO courseOutDTO = new CourseOutDTO();
        courseOutDTO.setCourseId(course.getCourseId());
        courseOutDTO.setTitle(StringUtils.toProperCase(course.getTitle()));
        courseOutDTO.setOwnerId(course.getOwnerId());
        courseOutDTO.setDescription(StringUtils.toProperCase(course.getDescription()));
        courseOutDTO.setLevel(course.getLevel());
        courseOutDTO.setActive(course.isActive());
        return courseOutDTO;
    }


    /**
     * Updates an existing {@link Course} entity with data from {@link UpdateCourseInDTO}.
     * This method modifies the existing course instance rather than creating a new one.
     *
     * @param existingCourse the course entity to update
     * @param updateDTO the DTO containing updated course data
     */
    public static void updateCourseFromDTO(final Course existingCourse, final UpdateCourseInDTO updateDTO) {
        existingCourse.setTitle(StringUtils.toProperCase(updateDTO.getTitle()));
        existingCourse.setDescription(StringUtils.toProperCase(updateDTO.getDescription()));
        existingCourse.setLevel(updateDTO.getCourseLevel());
        existingCourse.setOwnerId(updateDTO.getOwnerId());
        existingCourse.setActive(updateDTO.isActive());
    }

    /**
     * Converts a {@link Course} entity to {@link CourseInfoOutDTO}.
     * Maps all relevant fields for course information display.
     *
     * @param course the course entity to convert
     * @return the constructed {@link CourseInfoOutDTO}
     */
    public static CourseInfoOutDTO courseToCourseInfoOutDTO(final Course course) {
        CourseInfoOutDTO courseInfoDTO = new CourseInfoOutDTO();
        courseInfoDTO.setCourseId(course.getCourseId());
        courseInfoDTO.setTitle(course.getTitle());
        courseInfoDTO.setDescription(course.getDescription());
        courseInfoDTO.setOwnerId(course.getOwnerId());
        courseInfoDTO.setCourseLevel(course.getLevel());
        courseInfoDTO.setActive(course.isActive());
        courseInfoDTO.setUpdatedAt(course.getUpdatedAt());
        return courseInfoDTO;
    }

    /**
     * Converts a {@link Course} entity to {@link CourseSummaryOutDTO}.
     * Maps essential fields for course summary display.
     *
     * @param course the course entity to convert
     * @return the constructed {@link CourseSummaryOutDTO}
     */
    public static CourseSummaryOutDTO courseToCourseSummaryOutDTO(final Course course) {
        return new CourseSummaryOutDTO(
                course.getTitle(),
                course.getDescription(),
                course.getLevel(),
                course.getCreatedAt(),
                course.getUpdatedAt()
        );
    }

    /**
     * Converts a {@link Course} entity to a basic {@link CourseInDTO}.
     * Useful for cases where you need to convert an entity back to input format.
     *
     * @param course the course entity to convert
     * @return the constructed {@link CourseInDTO}
     */
    public static CourseInDTO courseToCourseInDTO(final Course course) {
        return new CourseInDTO(
                course.getTitle(),
                course.getOwnerId(),
                course.getDescription(),
                course.getLevel(),
                course.isActive()
        );
    }

    /**
     * Converts a {@link Course} entity to {@link UpdateCourseInDTO}.
     * Useful for pre-populating update forms with existing course data.
     *
     * @param course the course entity to convert
     * @return the constructed {@link UpdateCourseInDTO}
     */
    public static UpdateCourseInDTO courseToUpdateCourseInDTO(final Course course) {
        return new UpdateCourseInDTO(
                course.getTitle(),
                course.getOwnerId(),
                course.getDescription(),
                course.getLevel(),
                null, // Assuming image parameter in constructor
                course.isActive()
        );
    }
}