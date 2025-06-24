package com.nt.LMS.repository;

import com.nt.LMS.entities.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
