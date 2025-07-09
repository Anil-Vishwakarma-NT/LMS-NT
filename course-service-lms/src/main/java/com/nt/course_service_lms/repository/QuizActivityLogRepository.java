package com.nt.course_service_lms.repository;

import com.nt.course_service_lms.entity.QuizActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for QuizActivityLog entity operations.
 *
 * <p>This interface provides CRUD operations and custom query methods
 * for managing quiz activity logs in the database.</p>
 */
@Repository
public interface QuizActivityLogRepository extends JpaRepository<QuizActivityLog, Integer> {
}

