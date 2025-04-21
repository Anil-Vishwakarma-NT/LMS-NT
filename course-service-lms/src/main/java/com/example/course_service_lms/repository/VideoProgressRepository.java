package com.example.course_service_lms.repository;

import com.example.course_service_lms.entity.VideoProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VideoProgressRepository extends JpaRepository<VideoProgress, Long> {

    // Find progress by scalar fields courseId and courseContentId
    VideoProgress findByCourseIdAndCourseContentId(Long courseId, Long courseContentId);

    // Fetch all rows for the courseId to update course progress
    List<VideoProgress> findAllByCourseId(Long courseId);
}




