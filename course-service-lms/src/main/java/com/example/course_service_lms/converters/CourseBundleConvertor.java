package com.example.course_service_lms.converters;

import com.example.course_service_lms.dto.CourseBundleDTO;
import com.example.course_service_lms.entity.CourseBundle;

public class CourseBundleConvertor {
    public static CourseBundle convertDTOToEntity(CourseBundleDTO courseBundleDTO) {
        CourseBundle courseBundle = new CourseBundle();
        courseBundle.setCourseBundleId(courseBundleDTO.getCourseBundleId());
        courseBundle.setBundleId(courseBundleDTO.getBundleId());
        courseBundle.setCourseId(courseBundleDTO.getCourseId());

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
}
