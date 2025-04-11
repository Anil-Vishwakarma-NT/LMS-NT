package com.example.course_service_lms.service;

import com.example.course_service_lms.dto.CourseBundleDTO;
import com.example.course_service_lms.dto.CourseBundlePostDTO;

import java.util.List;

public interface CourseBundleService {
    List<CourseBundleDTO> getAllCourseBundles();

    CourseBundleDTO getCourseBundleById(Long courseBundleId);

    void deleteCourseBundle(Long courseBundleId);

    CourseBundlePostDTO updateCourseBundle(Long courseBundleId, CourseBundlePostDTO courseBundlePostDTO);

    CourseBundlePostDTO createCourseBundle(CourseBundlePostDTO courseBundlePostDTO);
}
