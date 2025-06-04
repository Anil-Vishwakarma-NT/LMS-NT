package com.nt.LMS.repository;

import com.nt.LMS.entities.UserCourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserCourseEnrollmentRepository extends JpaRepository<UserCourseEnrollment, Long> {

    Optional<UserCourseEnrollment> findByUserIdAndCourseIdAndStatusNotIn(Long userId, Long courseId, List<String> statuses);
    List<UserCourseEnrollment> findByCourseId(Long courseId);
    List<UserCourseEnrollment> findByUserId(Long userId);
    long countByStatusNotIn(List<String> statuses);
    long countByCourseIdAndStatusNotIn(Long courseId, List<String> statuses);
    long countByUserIdAndStatusNotIn(Long userId, List<String> statuses);
}
