package com.example.course_service_lms.converters;

import com.example.course_service_lms.dto.CourseBundleDTO;
import com.example.course_service_lms.dto.CourseBundlePostDTO;
import com.example.course_service_lms.entity.CourseBundle;

public class CourseBundleConvertor {
    // Helper Method: DTO to Entity For Post
    public static CourseBundle convertDTOToEntity(CourseBundleDTO courseBundleDTO) {
        CourseBundle courseBundle = new CourseBundle();
        courseBundle.setCourseBundleId(courseBundleDTO.getCourseBundleId());
        courseBundle.setBundleId(courseBundleDTO.getBundleId());
        courseBundle.setCourseId(courseBundleDTO.getCourseId());

        return courseBundle;
    }

    // Helper Method: DTO to Entity For Post
    public static CourseBundle convertDTOToEntityPost(CourseBundlePostDTO courseBundlePostDTO) {
        CourseBundle courseBundle = new CourseBundle();
        courseBundle.setCourseBundleId(courseBundlePostDTO.getCourseBundleId());
        courseBundle.setBundleId(courseBundlePostDTO.getBundleId());
        courseBundle.setCourseId(courseBundlePostDTO.getCourseId());

        return courseBundle;
    }

    // Helper Method: Entity to DTO
    public static CourseBundleDTO convertEntityToDTO(CourseBundle courseBundle) {
        CourseBundleDTO courseBundleDTO = new CourseBundleDTO();
        courseBundleDTO.setCourseBundleId(courseBundle.getCourseBundleId());
        courseBundleDTO.setBundleId(courseBundle.getBundleId());
        courseBundleDTO.setCourseId(courseBundle.getCourseId());

        return courseBundleDTO;
    }

    // Helper Method: Entity to DTO For Post
    public static CourseBundlePostDTO convertEntityToDTOPost(CourseBundle courseBundle) {
        CourseBundlePostDTO courseBundlePostDTO = new CourseBundlePostDTO();
        courseBundlePostDTO.setCourseBundleId(courseBundle.getCourseBundleId());
        courseBundlePostDTO.setBundleId(courseBundle.getBundleId());
        courseBundlePostDTO.setCourseId(courseBundle.getCourseId());

        return courseBundlePostDTO;
    }

}
