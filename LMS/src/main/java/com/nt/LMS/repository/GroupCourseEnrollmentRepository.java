package com.nt.LMS.repository;

import com.nt.LMS.entities.GroupCourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupCourseEnrollmentRepository extends JpaRepository<GroupCourseEnrollment, Long> {
    Optional<GroupCourseEnrollment> findByGroupIdAndCourseIdAndStatusNotIn(Long groupId, Long courseId, List<String> statuses);
}
