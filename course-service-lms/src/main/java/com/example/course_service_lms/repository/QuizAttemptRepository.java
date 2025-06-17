package com.example.course_service_lms.repository;

import com.example.course_service_lms.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for QuizAttempt entity operations.
 *
 * <p>This interface provides CRUD operations and custom query methods
 * for managing quiz attempts in the database.</p>
 */
@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Integer> {
}

