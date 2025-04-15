package com.nt.LMS.repository;

import com.nt.LMS.entities.GroupBundleEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupBundleEnrollmentRepository extends JpaRepository <GroupBundleEnrollment, Long> {
}
