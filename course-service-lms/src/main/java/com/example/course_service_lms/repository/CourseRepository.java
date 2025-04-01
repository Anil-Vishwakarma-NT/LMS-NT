package com.example.course_service_lms.repository;

import com.example.course_service_lms.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
