package com.example.course_service_lms.service;


import com.example.course_service_lms.dto.CourseContentDTO;
import com.example.course_service_lms.entity.CourseContent;

import java.util.List;
import java.util.Optional;

public interface CourseContentService {

    CourseContent createCourseContent(CourseContentDTO courseContentDTO);
    List<CourseContent> getAllCourseContents();
    Optional<CourseContent> getCourseContentById(Long courseContentId);
    String deleteCourseContent(Long courseContentId);
    String updateCourseContent(Long courseId, CourseContentDTO courseContentDTO);

}
