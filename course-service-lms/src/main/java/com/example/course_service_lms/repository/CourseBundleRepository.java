package com.example.course_service_lms.repository;

import com.example.course_service_lms.entity.CourseBundle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseBundleRepository extends JpaRepository<CourseBundle,Long> {

}
