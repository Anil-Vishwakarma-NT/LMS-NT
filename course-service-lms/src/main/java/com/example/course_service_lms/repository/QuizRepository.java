package com.example.course_service_lms.repository;

import com.example.course_service_lms.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Quiz entity operations.
 *
 * <p>This interface provides CRUD operations and custom query methods
 * for managing quizzes in the database.</p>
 */
@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    /**
     * Find all active quizzes.
     *
     * @return List of active quizzes
     */
    List<Quiz> findByIsActiveTrue();

    /**
     * Find all quizzes by parent type and parent ID (for active quizzes).
     *
     * @param parentType The parent type (course, bundle, course-content)
     * @param parentId The parent ID
     * @return List of quizzes for the given parent
     */
    List<Quiz> findByParentTypeAndParentIdAndIsActiveTrue(String parentType, Long parentId);

    /**
     * Check if a quiz with the same title exists for the same parent.
     *
     * @param title The quiz title
     * @param parentType The parent type
     * @param parentId The parent ID
     * @return true if quiz with same title exists for the parent
     */
    boolean existsByTitleAndParentTypeAndParentId(String title, String parentType, Long parentId);
}

