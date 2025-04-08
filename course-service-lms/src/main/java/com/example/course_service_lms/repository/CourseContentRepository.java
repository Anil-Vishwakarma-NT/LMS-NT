package com.example.course_service_lms.repository;

import com.example.course_service_lms.entity.CourseContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseContentRepository extends JpaRepository<CourseContent, Long> {
    Optional<CourseContent> findByTitleIgnoreCaseAndCourseId(String title, Long courseId);
}
