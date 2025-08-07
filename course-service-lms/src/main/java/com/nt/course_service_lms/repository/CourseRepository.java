package com.nt.course_service_lms.repository;

import com.nt.course_service_lms.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link Course} entities.
 * <p>
 * Provides methods to interact with the course data, such as checking for existence
 * and retrieving courses by specific fields.
 * Extends {@link JpaRepository} to inherit standard CRUD operations.
 * </p>
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * Finds a course by its title (case-insensitive) and the owner's ID.
     *
     * @param title   the title of the course
     * @param ownerId the ID of the course owner
     * @return an {@link Optional} containing the matching {@link Course}, or empty if none found
     */
    Optional<Course> findByTitleIgnoreCaseAndOwnerId(String title, Long ownerId);

    /**
     * Checks whether a course exists with the given ID.
     *
     * @param id the course ID to check
     * @return {@code true} if a course with the ID exists, {@code false} otherwise
     */
    boolean existsById(Long id);

    /**
     * Checks whether a course exists with the given title.
     *
     * @return {@code true} if a course with the title exists, {@code false} otherwise
     */
    List<Course> findTop5ByOrderByCreatedAtDesc();

    /**
     * Retrieves a list of course IDs that exist in the database.
     *
     * @param courseIds the list of course IDs to check
     * @return a list of existing course IDs
     */
    @Query("SELECT c.courseId FROM Course c WHERE c.courseId IN :courseIds")
    List<Long> findExistingIds(@Param("courseIds") List<Long> courseIds);

    /**
     * find recent data for dashboard.
     * @return recent courses and bundles
     */
    @Query(value = """
            SELECT * FROM (
                SELECT
                    'COURSE' as type,
                    c.course_id as id,
                    c.title as name,
                    c.description,
                    c.level,
                    c.created_at,
                    c.updated_at,
                    0 as course_count
                FROM course c
                WHERE c.is_active = true
                ORDER BY c.created_at DESC
                LIMIT 5
            ) courses
            UNION ALL
            SELECT * FROM (
                SELECT
                    'BUNDLE' as type,
                    b.bundle_id as id,
                    b.bundle_name as name,
                    '' as description,
                    '' as level,
                    b.created_at,
                    b.updated_at,
                    COALESCE(cb.course_count, 0) as course_count
                FROM bundle b
                LEFT JOIN (
                    SELECT bundle_id, COUNT(*) as course_count
                    FROM course_bundle
                    GROUP BY bundle_id
                ) cb ON b.bundle_id = cb.bundle_id
                WHERE b.is_active = true
                ORDER BY b.created_at DESC
                LIMIT 5
            ) bundles
            ORDER BY created_at DESC
            """, nativeQuery = true)
    List<Object[]> findRecentDashboardData();


    /**
     * fina existing courses by Ids.
     * @param courseIds
     * @return course of given Ids
     */
    List<Course> findByCourseIdIn(List<Long> courseIds);

}
