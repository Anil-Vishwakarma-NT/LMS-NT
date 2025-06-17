package com.example.course_service_lms.repository;

import com.example.course_service_lms.entity.QuestionOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for QuestionOption entity operations.
 *
 * <p>This interface provides CRUD operations and custom query methods
 * for managing question options in the database.</p>
 */
@Repository
public interface QuestionOptionRepository extends JpaRepository<QuestionOption, Integer> {
}