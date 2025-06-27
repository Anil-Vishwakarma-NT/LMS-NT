package com.nt.LMS.repository;

import com.nt.LMS.entities.Enrollment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    @Modifying
    @Transactional
    @Query("UPDATE Enrollment ug SET ug.isActive = false WHERE ug.groupId = :groupId")
    void softDeleteByGroupId(Long groupId);


    @Query("SELECT ug FROM Enrollment ug WHERE ug.userId = :userId AND  ug.groupId = NULL")
    List<Enrollment> getIndividualUserEnrollments(Long userId);

}
