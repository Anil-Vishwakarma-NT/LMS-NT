package com.example.course_service_lms.repository;

import com.example.course_service_lms.entity.QuestionResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for QuestionResponse entity operations.
 *
 * <p>This interface provides CRUD operations and custom query methods
 * for managing question responses in the database.</p>
 */
@Repository
public interface QuestionResponseRepository extends JpaRepository<QuestionResponse, Integer> {
}

