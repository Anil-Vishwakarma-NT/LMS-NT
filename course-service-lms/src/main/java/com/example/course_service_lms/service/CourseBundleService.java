package com.example.course_service_lms.service;

import com.example.course_service_lms.dto.CourseBundleDTO;
import java.util.List;

public interface CourseBundleService {
    List<CourseBundleDTO> getAllCourseBundles();

    CourseBundleDTO getCourseBundleById(Long courseBundleId);

    void deleteCourseBundle(Long courseBundleId);

    CourseBundleDTO updateCourseBundle(Long courseBundleId, CourseBundleDTO courseBundleDTO);

    CourseBundleDTO createCourseBundle(CourseBundleDTO courseBundleDTO);
}
