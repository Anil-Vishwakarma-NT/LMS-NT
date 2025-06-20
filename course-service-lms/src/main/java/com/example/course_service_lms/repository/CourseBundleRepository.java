package com.example.course_service_lms.repository;

import com.example.course_service_lms.entity.CourseBundle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing {@link CourseBundle} entities.
 * <p>
 * Provides CRUD operations and custom queries for handling the association between courses and bundles.
 * Extends {@link JpaRepository} for built-in database interactions.
 * </p>
 */
@Repository
public interface CourseBundleRepository extends JpaRepository<CourseBundle, Long> {

    /**
     * Checks whether a specific course is already associated with a specific bundle.
     *
     * @param bundleId the ID of the bundle
     * @param courseId the ID of the course
     * @return {@code true} if the course is already added to the bundle, {@code false} otherwise
     */
    boolean existsByBundleIdAndCourseId(Long bundleId, Long courseId);

    /**
     * Retrieves all {@link CourseBundle} records associated with a specific bundle.
     *
     * @param bundleId the ID of the bundle
     * @return a list of {@link CourseBundle} entries belonging to the specified bundle
     */
    List<CourseBundle> findByBundleId(Long bundleId);

    long countByBundleId(Long bundleId);

    // Assuming you have a bundle_course junction table
    @Query("SELECT bc.courseId FROM CourseBundle bc WHERE bc.bundleId = :bundleId")
    List<Long> findCourseIdsByBundleId(@Param("bundleId") Long bundleId);
}
