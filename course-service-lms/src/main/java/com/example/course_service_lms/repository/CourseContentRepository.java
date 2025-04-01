package com.example.course_service_lms.repository;

import com.example.course_service_lms.entity.CourseContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseContentRepository extends JpaRepository<CourseContent, Long> {
}
