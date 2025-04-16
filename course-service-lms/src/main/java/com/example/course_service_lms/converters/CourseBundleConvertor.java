package com.example.course_service_lms.converters;

import com.example.course_service_lms.dto.CourseBundleDTO;
import com.example.course_service_lms.dto.CourseBundlePostDTO;
import com.example.course_service_lms.entity.CourseBundle;

/**
 * Utility class responsible for converting between CourseBundle entity and its corresponding DTOs.
 * Provides conversion methods for both general-purpose and post-specific DTO mappings.
 */
public final class CourseBundleConvertor {

    private CourseBundleConvertor() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    /**
     * Converts a {@link CourseBundleDTO} to a {@link CourseBundle} entity.
     * Typically used for general data representation or update operations.
     *
     * @param courseBundleDTO the DTO to convert
     * @return the corresponding CourseBundle entity
     */
    public static CourseBundle convertDTOToEntity(final CourseBundleDTO courseBundleDTO) {
        CourseBundle courseBundle = new CourseBundle();
        courseBundle.setCourseBundleId(courseBundleDTO.getCourseBundleId());
        courseBundle.setBundleId(courseBundleDTO.getBundleId());
        courseBundle.setCourseId(courseBundleDTO.getCourseId());
        return courseBundle;
    }

    /**
     * Converts a {@link CourseBundlePostDTO} to a {@link CourseBundle} entity.
     * Used specifically during creation (POST) requests.
     *
     * @param courseBundlePostDTO the post DTO to convert
     * @return the corresponding CourseBundle entity
     */
    public static CourseBundle convertDTOToEntityPost(final CourseBundlePostDTO courseBundlePostDTO) {
        CourseBundle courseBundle = new CourseBundle();
        courseBundle.setCourseBundleId(courseBundlePostDTO.getCourseBundleId());
        courseBundle.setBundleId(courseBundlePostDTO.getBundleId());
        courseBundle.setCourseId(courseBundlePostDTO.getCourseId());
        return courseBundle;
    }

    /**
     * Converts a {@link CourseBundle} entity to a {@link CourseBundleDTO}.
     * Typically used to send full course-bundle data in responses.
     *
     * @param courseBundle the entity to convert
     * @return the corresponding CourseBundleDTO
     */
    public static CourseBundleDTO convertEntityToDTO(final CourseBundle courseBundle) {
        CourseBundleDTO courseBundleDTO = new CourseBundleDTO();
        courseBundleDTO.setCourseBundleId(courseBundle.getCourseBundleId());
        courseBundleDTO.setBundleId(courseBundle.getBundleId());
        courseBundleDTO.setCourseId(courseBundle.getCourseId());
        return courseBundleDTO;
    }

    /**
     * Converts a {@link CourseBundle} entity to a {@link CourseBundlePostDTO}.
     * Used specifically for returning data after creation (POST).
     *
     * @param courseBundle the entity to convert
     * @return the corresponding CourseBundlePostDTO
     */
    public static CourseBundlePostDTO convertEntityToDTOPost(final CourseBundle courseBundle) {
        CourseBundlePostDTO courseBundlePostDTO = new CourseBundlePostDTO();
        courseBundlePostDTO.setCourseBundleId(courseBundle.getCourseBundleId());
        courseBundlePostDTO.setBundleId(courseBundle.getBundleId());
        courseBundlePostDTO.setCourseId(courseBundle.getCourseId());
        return courseBundlePostDTO;
    }
}
