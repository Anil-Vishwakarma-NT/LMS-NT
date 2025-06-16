package com.nt.LMS.repository;

import com.nt.LMS.entities.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    @Query("SELECT e FROM Enrollment e WHERE e.userId = :userId AND e.courseId = :courseId AND e.isActive = true AND e.status NOT IN ('COMPLETED', 'EXPIRED', 'UNENROLLED')")
    Optional<Enrollment> findActiveEnrollmentByUserAndCourse(@Param("userId") Long userId, @Param("courseId") Long courseId);

    @Query("SELECT e FROM Enrollment e WHERE e.userId = :userId AND e.bundleId = :bundleId AND e.isActive = true AND e.status NOT IN ('COMPLETED', 'EXPIRED', 'UNENROLLED')")
    Optional<Enrollment> findActiveEnrollmentByUserAndBundle(@Param("userId") Long userId, @Param("bundleId") Long bundleId);

    @Query("SELECT e FROM Enrollment e WHERE e.groupId = :groupId AND e.courseId = :courseId AND e.isActive = true AND e.status NOT IN ('COMPLETED', 'EXPIRED', 'UNENROLLED')")
    Optional<Enrollment> findActiveEnrollmentByGroupAndCourse(@Param("groupId") Long groupId, @Param("courseId") Long courseId);

    @Query("SELECT e FROM Enrollment e WHERE e.groupId = :groupId AND e.bundleId = :bundleId AND e.isActive = true AND e.status NOT IN ('COMPLETED', 'EXPIRED', 'UNENROLLED')")
    Optional<Enrollment> findActiveEnrollmentByGroupAndBundle(@Param("groupId") Long groupId, @Param("bundleId") Long bundleId);

    // ✅ Corrected JPQL - use entity name and field name
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.isActive = true")
    Long countTotalActiveEnrollments();

    @Query("SELECT COUNT(DISTINCT e.userId) FROM Enrollment e WHERE e.isActive = true AND e.userId IS NOT NULL")
    Long countDistinctUsersEnrolled();

    @Query("SELECT COUNT(DISTINCT e.groupId) FROM Enrollment e WHERE e.isActive = true AND e.groupId IS NOT NULL")
    Long countDistinctGroupsEnrolled();

    @Query("SELECT COUNT(DISTINCT e.bundleId) FROM Enrollment e WHERE e.isActive = true AND e.bundleId IS NOT NULL")
    Long countDistinctBundlesEnrolled();

    // ✅ Native SQL for top enrolled courses
    @Query(value = """
        SELECT course_id, COUNT(*) as enrollment_count
        FROM enrollments
        WHERE is_active = true AND course_id IS NOT NULL
        GROUP BY course_id
        ORDER BY enrollment_count DESC
    """, nativeQuery = true)
    List<Object[]> findTopEnrolledCourses();

    @Query("SELECT AVG(e.progressPercentage) FROM Enrollment e WHERE e.isActive = true AND e.progressPercentage IS NOT NULL")
    BigDecimal findAverageProgressPercentage();

    // ✅ Tracking record query - fixed parameter names
    @Query("SELECT e FROM Enrollment e WHERE e.parentEnrollmentId = :parentId AND e.enrollmentSource = 'TRACKING_RECORD' AND e.isActive = true")
    Optional<Enrollment> findTrackingRecord(@Param("childId") Long childId, @Param("parentId") Long parentId);
}
