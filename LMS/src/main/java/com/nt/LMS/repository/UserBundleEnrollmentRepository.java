package com.nt.LMS.repository;

import com.nt.LMS.entities.UserBundleEnrollment;
import com.nt.LMS.entities.UserCourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserBundleEnrollmentRepository extends JpaRepository<UserBundleEnrollment, Long> {

    Optional<UserBundleEnrollment> findByUserIdAndBundleIdAndStatusNotIn(Long userId, Long bundleId, List<String> statuses);
    List<UserBundleEnrollment> findByBundleId(Long bundleId);
    List<UserBundleEnrollment> findByUserId(Long userId);
    long countByStatusNotIn(List<String> statuses);
    long countByBundleIdAndStatusNotIn(Long bundleId, List<String> statuses);
    long countByUserIdAndStatusNotIn(Long userId, List<String> statuses);
    long countByDeadlineBetween(LocalDateTime before, LocalDateTime after);
}
