package com.nt.LMS.repository;

import com.nt.LMS.entities.UserBundleEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBundleEnrollmentRepository extends JpaRepository<UserBundleEnrollment, Long> {
}
