package com.nt.LMS.repository;

import com.nt.LMS.entities.EnrollmentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentHistoryRepository extends JpaRepository<EnrollmentHistory, Long> {
    long countByStatusIn(List<String> statuses);
}
