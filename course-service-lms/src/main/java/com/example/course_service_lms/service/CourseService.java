package com.example.course_service_lms.service;

import com.example.course_service_lms.dto.CourseDTO;
import com.example.course_service_lms.entity.Course;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface CourseService {
    Course createCourse(CourseDTO courseDTO);
    List<Course> getAllCourses();
    Optional<Course> getCourseById(Long courseId);
    String deleteCourse(Long courseId);
    String updateCourse(Long courseId, CourseDTO courseDTO);
    boolean courseExistsById(Long courseId);
    long countCourses();
}
