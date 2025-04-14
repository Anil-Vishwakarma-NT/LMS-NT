package com.nt.LMS.repository;

import com.nt.LMS.entities.GroupEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupEnrollmentRepository extends JpaRepository<GroupEnrollment, Long> {
}
