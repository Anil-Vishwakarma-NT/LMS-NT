package com.example.course_service_lms.service;

import com.example.course_service_lms.dto.CourseBundleDTO;
import com.example.course_service_lms.dto.CreateCourseBundleDTO;
import com.example.course_service_lms.entity.CourseBundle;

import java.util.List;

public interface CourseBundleService {
    List<CourseBundleDTO> getAllCourseBundles();

    CourseBundleDTO getCourseBundleById(Long courseBundleId);

    void deleteCourseBundle(Long courseBundleId);

    String updateCourseBundle(Long courseBundleId, CreateCourseBundleDTO createCourseBundleDTO);

    CourseBundle createCourseBundle(CreateCourseBundleDTO createCourseBundleDTO);
}
