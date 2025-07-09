package com.nt.user_service_lms.repository;

import com.nt.user_service_lms.entities.Enrollment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByUserIdAndGroupIdAndCourseIdAndBundleIdAndEnrollmentSourceAndIsActive(
            Long userId, Long groupId, Long courseId, Long bundleId, String enrollmentSource, Boolean isActive);
    List<Enrollment> findByUserIdAndIsActiveTrue(Long userId);
    List<Enrollment> findByEnrollmentSourceAndIsActiveTrueAndBundleIdIsNull(String enrollmentSource);
    List<Enrollment> findByEnrollmentSourceAndIsActiveTrueAndBundleIdIsNotNull(String enrollmentSource);

    List<Enrollment> findByIsActiveTrue();
    List<Enrollment> findByGroupId(Long groupId);

    List<Enrollment> findByGroupIdAndCourseId(Long groupId,Long courseId);

    List<Enrollment> findByGroupIdAndUserId(Long groupId,Long userId);

    List<Enrollment> findByUserId(Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE Enrollment ug SET ug.isActive = false WHERE ug.groupId = :groupId")
    void softDeleteByGroupId(Long groupId);

@Query("SELECT Count(DISTINCT ug.courseId) FROM Enrollment ug WHERE ug.userId = :userId")
Long getUserTotalEnrollments(Long userId);


    @Modifying
    @Transactional
    @Query("UPDATE Enrollment ug SET ug.isActive = false WHERE ug.groupId = :groupId AND ug.userId = :userId")
    void softDeleteByGroupIdAndUserId(Long groupId , Long userId);

    Optional<Enrollment> findByGroupIdAndUserIdAndCourseId(Long groupId,Long userId , Long courseId);

}
