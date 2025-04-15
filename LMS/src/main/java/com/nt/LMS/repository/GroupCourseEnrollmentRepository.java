package com.nt.LMS.repository;

import com.nt.LMS.entities.GroupCourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupCourseEnrollmentRepository extends JpaRepository<GroupCourseEnrollment, Long> {
}
