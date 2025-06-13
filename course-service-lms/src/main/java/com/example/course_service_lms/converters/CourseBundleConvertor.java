package com.example.course_service_lms.converters;

import com.example.course_service_lms.dto.outDTO.CourseBundleOutDTO;
import com.example.course_service_lms.dto.inDTO.CourseBundleInDTO;
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
     * Converts a {@link CourseBundleOutDTO} to a {@link CourseBundle} entity.
     * Typically used for general data representation or update operations.
     *
     * @param courseBundleOutDTO the DTO to convert
     * @return the corresponding CourseBundle entity
     */
    public static CourseBundle convertDTOToEntity(final CourseBundleOutDTO courseBundleOutDTO) {
        CourseBundle courseBundle = new CourseBundle();
        courseBundle.setCourseBundleId(courseBundleOutDTO.getCourseBundleId());
        courseBundle.setBundleId(courseBundleOutDTO.getBundleId());
        courseBundle.setCourseId(courseBundleOutDTO.getCourseId());
        return courseBundle;
    }

    /**
     * Converts a {@link CourseBundleInDTO} to a {@link CourseBundle} entity.
     * Used specifically during creation (POST) requests.
     *
     * @param courseBundleInDTO the post DTO to convert
     * @return the corresponding CourseBundle entity
     */
    public static CourseBundle convertDTOToEntityPost(final CourseBundleInDTO courseBundleInDTO) {
        CourseBundle courseBundle = new CourseBundle();
        courseBundle.setCourseBundleId(courseBundleInDTO.getCourseBundleId());
        courseBundle.setBundleId(courseBundleInDTO.getBundleId());
        courseBundle.setCourseId(courseBundleInDTO.getCourseId());
        courseBundle.setActive(courseBundleInDTO.isActive());
        return courseBundle;
    }

    /**
     * Converts a {@link CourseBundle} entity to a {@link CourseBundleOutDTO}.
     * Typically used to send full course-bundle data in responses.
     *
     * @param courseBundle the entity to convert
     * @return the corresponding CourseBundleDTO
     */
    public static CourseBundleOutDTO convertEntityToDTO(final CourseBundle courseBundle) {
        CourseBundleOutDTO courseBundleOutDTO = new CourseBundleOutDTO();
        courseBundleOutDTO.setCourseBundleId(courseBundle.getCourseBundleId());
        courseBundleOutDTO.setBundleId(courseBundle.getBundleId());
        courseBundleOutDTO.setCourseId(courseBundle.getCourseId());
        return courseBundleOutDTO;
    }

    /**
     * Converts a {@link CourseBundle} entity to a {@link CourseBundleInDTO}.
     * Used specifically for returning data after creation (POST).
     *
     * @param courseBundle the entity to convert
     * @return the corresponding CourseBundlePostDTO
     */
    public static CourseBundleInDTO convertEntityToDTOPost(final CourseBundle courseBundle) {
        CourseBundleInDTO courseBundleInDTO = new CourseBundleInDTO();
        courseBundleInDTO.setCourseBundleId(courseBundle.getCourseBundleId());
        courseBundleInDTO.setBundleId(courseBundle.getBundleId());
        courseBundleInDTO.setCourseId(courseBundle.getCourseId());
        return courseBundleInDTO;
    }
}
