package com.nt.user_service_lms.repository;

import com.nt.user_service_lms.entities.Enrollment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing {@link Enrollment} entities.
 */
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    /**
     * Checks if an enrollment exists for the given parameters.
     *
     * @param userId the user ID
     * @param groupId the group ID
     * @param courseId the course ID
     * @param bundleId the bundle ID
     * @param enrollmentSource the enrollment source
     * @param isActive the active status
     * @return true if such enrollment exists, false otherwise
     */
    boolean existsByUserIdAndGroupIdAndCourseIdAndBundleIdAndEnrollmentSourceAndIsActive(
            Long userId, Long groupId, Long courseId, Long bundleId, String enrollmentSource, Boolean isActive);

    /**
     * Finds all active enrollments for a user.
     *
     * @param userId the user ID
     * @return list of active enrollments
     */
    List<Enrollment> findByUserIdAndIsActiveTrue(Long userId);

    /**
     * Finds all active enrollments by enrollment source where bundle ID is null.
     *
     * @param enrollmentSource the enrollment source
     * @return list of enrollments
     */
    List<Enrollment> findByEnrollmentSourceAndIsActiveTrueAndBundleIdIsNull(String enrollmentSource);

    /**
     * Finds all active enrollments by enrollment source where bundle ID is not null.
     *
     * @param enrollmentSource the enrollment source
     * @return list of enrollments
     */
    List<Enrollment> findByEnrollmentSourceAndIsActiveTrueAndBundleIdIsNotNull(String enrollmentSource);

    /**
     * Finds all active enrollments.
     *
     * @return list of active enrollments
     */
    List<Enrollment> findByIsActiveTrue();

    /**
     * Finds all enrollments by group ID.
     *
     * @param groupId the group ID
     * @return list of enrollments
     */
    List<Enrollment> findByGroupId(Long groupId);

    /**
     * Finds all enrollments by group ID and course ID.
     *
     * @param groupId the group ID
     * @param courseId the course ID
     * @return list of enrollments
     */
    List<Enrollment> findByGroupIdAndCourseId(Long groupId, Long courseId);

    /**
     * Finds all enrollments by group ID and user ID.
     *
     * @param groupId the group ID
     * @param userId the user ID
     * @return list of enrollments
     */
    List<Enrollment> findByGroupIdAndUserId(Long groupId, Long userId);

    /**
     * Finds all enrollments by user ID.
     *
     * @param userId the user ID
     * @return list of enrollments
     */
    List<Enrollment> findByUserId(Long userId);

    /**
     * Soft deletes all enrollments by group ID.
     *
     * @param groupId the group ID
     */
    @Modifying
    @Transactional
    @Query("UPDATE Enrollment ug SET ug.isActive = false WHERE ug.groupId = :groupId")
    void softDeleteByGroupId(Long groupId);

    /**
     * Gets the total number of distinct course enrollments for a user.
     *
     * @param userId the user ID
     * @return total number of enrollments
     */
@Query("SELECT Count(DISTINCT ug.courseId) FROM Enrollment ug WHERE ug.userId = :userId AND ug.isActive = TRUE")
Long getUserTotalEnrollments(Long userId);

    /**
     * Soft deletes an enrollment by group ID and user ID.
     *
     * @param groupId the group ID
     * @param userId the user ID
     */
    @Modifying
    @Transactional
    @Query("UPDATE Enrollment ug SET ug.isActive = false WHERE ug.groupId = :groupId AND ug.userId = :userId")
    void softDeleteByGroupIdAndUserId(Long groupId, Long userId);

    /**
     * Finds an enrollment by group ID, user ID, and course ID.
     *
     * @param groupId the group ID
     * @param userId the user ID
     * @param courseId the course ID
     * @return optional enrollment
     */
    Optional<Enrollment> findByGroupIdAndUserIdAndCourseId(Long groupId, Long userId, Long courseId);

}
