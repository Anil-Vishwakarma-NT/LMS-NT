package com.nt.LMS.repository;

import com.nt.LMS.entities.Enrollment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    // Count total active enrollments
    Long countByIsActiveTrue();

    // Count distinct users with active enrollments
    @Query("SELECT COUNT(DISTINCT e.userId) FROM Enrollment e WHERE e.isActive = true AND e.userId IS NOT NULL")
    Long countDistinctUsersByIsActiveTrue();

    // Count distinct groups with active enrollments
    @Query("SELECT COUNT(DISTINCT e.groupId) FROM Enrollment e WHERE e.isActive = true AND e.groupId IS NOT NULL")
    Long countDistinctGroupsByIsActiveTrue();

    // Count distinct bundles with active enrollments
    @Query("SELECT COUNT(DISTINCT e.bundleId) FROM Enrollment e WHERE e.isActive = true AND e.bundleId IS NOT NULL")
    Long countDistinctBundlesByIsActiveTrue();

    // Find course with most active enrollments
    @Query("SELECT e.courseId FROM Enrollment e WHERE e.isActive = true AND e.courseId IS NOT NULL " +
            "GROUP BY e.courseId ORDER BY COUNT(e.courseId) DESC LIMIT 1")
    Long findTopEnrolledCourse();

    // Count enrollments with due deadlines
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.isActive = true AND e.deadline IS NOT NULL AND e.deadline < :currentTime")
    Long countEnrollmentsWithDueDeadlines(@Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT AVG(e.progressPercentage) FROM Enrollment e WHERE e.isActive = true AND e.progressPercentage IS NOT NULL")
    BigDecimal findAverageProgressPercentage();

    /**
     * Find active enrollment by user and course with specific enrollment source
     */
    @Query("SELECT e FROM Enrollment e WHERE e.userId = :userId AND e.courseId = :courseId " +
            "AND e.enrollmentSource = :enrollmentSource AND e.isActive = true")
    Optional<Enrollment> findActiveEnrollmentByUserCourseAndSource(
            @Param("userId") Long userId,
            @Param("courseId") Long courseId,
            @Param("enrollmentSource") String enrollmentSource);

    /**
     * Find active enrollment by user and bundle with specific enrollment source
     */
    @Query("SELECT e FROM Enrollment e WHERE e.userId = :userId AND e.bundleId = :bundleId " +
            "AND e.enrollmentSource = :enrollmentSource AND e.isActive = true")
    Optional<Enrollment> findActiveEnrollmentByUserBundleAndSource(
            @Param("userId") Long userId,
            @Param("bundleId") Long bundleId,
            @Param("enrollmentSource") String enrollmentSource);

    /**
     * Find active enrollment by group and course with specific enrollment source
     */
    @Query("SELECT e FROM Enrollment e WHERE e.groupId = :groupId AND e.courseId = :courseId " +
            "AND e.enrollmentSource = :enrollmentSource AND e.isActive = true")
    Optional<Enrollment> findActiveEnrollmentByGroupCourseAndSource(
            @Param("groupId") Long groupId,
            @Param("courseId") Long courseId,
            @Param("enrollmentSource") String enrollmentSource);

    /**
     * Find active enrollment by group and bundle with specific enrollment source
     */
    @Query("SELECT e FROM Enrollment e WHERE e.groupId = :groupId AND e.bundleId = :bundleId " +
            "AND e.enrollmentSource = :enrollmentSource AND e.isActive = true")
    Optional<Enrollment> findActiveEnrollmentByGroupBundleAndSource(
            @Param("groupId") Long groupId,
            @Param("bundleId") Long bundleId,
            @Param("enrollmentSource") String enrollmentSource);

    /**
     * Find active enrollment by user, course, source, and parent enrollment
     * Used to prevent duplicate bundle expansions and group member enrollments
     */
    @Query("SELECT e FROM Enrollment e WHERE e.userId = :userId AND e.courseId = :courseId " +
            "AND e.enrollmentSource = :enrollmentSource AND e.parentEnrollmentId = :parentEnrollmentId " +
            "AND e.isActive = true")
    Optional<Enrollment> findActiveEnrollmentByUserCourseSourceAndParent(
            @Param("userId") Long userId,
            @Param("courseId") Long courseId,
            @Param("enrollmentSource") String enrollmentSource,
            @Param("parentEnrollmentId") Long parentEnrollmentId);

    // Add this method to your EnrollmentRepository interface
    @Query("SELECT e FROM Enrollment e WHERE e.userId = :userId AND e.isActive = true")
    List<Enrollment> findActiveEnrollmentsByUserId(@Param("userId") Long userId);

    @Query("SELECT DISTINCT e.userId FROM Enrollment e WHERE e.isActive = true AND e.userId IS NOT NULL")
    List<Long> findDistinctUserIdsWithActiveEnrollments();

    List<Enrollment> findActiveEnrollmentsByUserIdAndParentEnrollmentId(Long userId, Long parentEnrollmentId);

    /**
     * Find all active individual course enrollments (not from bundles)
     * @return List of individual course enrollments
     */
    @Query("SELECT e FROM Enrollment e WHERE e.isActive = true " +
            "AND e.courseId IS NOT NULL " +
            "AND e.bundleId IS NULL " +
            "AND e.enrollmentSource = 'INDIVIDUAL' " +
            "ORDER BY e.courseId, e.assignedAt")
    List<Enrollment> findAllActiveIndividualCourseEnrollments();

    @Query("SELECT e FROM Enrollment e WHERE e.isActive = true " +
            "AND e.bundleId IS NOT NULL " +
            "AND e.courseId IS NULL " +
            "AND e.enrollmentSource = 'INDIVIDUAL' " +
            "ORDER BY e.bundleId, e.assignedAt")
    List<Enrollment> findAllActiveIndividualBundleEnrollments();



    List<Enrollment> findByGroupId(Long groupId);

    List<Enrollment> findByGroupIdAndCourseId(Long groupId,Long courseId);

    List<Enrollment> findByGroupIdAndUserId(Long groupId,Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE Enrollment ug SET ug.isActive = false WHERE ug.groupId = :groupId")
    void softDeleteByGroupId(Long groupId);

}
