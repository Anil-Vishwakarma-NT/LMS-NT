package com.nt.LMS.repository;

import com.nt.LMS.entities.UserCourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCourseEnrollmentRepository extends JpaRepository<UserCourseEnrollment, Long> {
}
