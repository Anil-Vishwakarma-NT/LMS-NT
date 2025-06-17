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
public interface QuizRepository extends JpaRepository<Quiz, Integer> {
    /**
     * Find all active quizzes.
     *
     * @return List of active quizzes
     */
    List<Quiz> findByIsActiveTrue();

    /**
     * Find active quiz by ID.
     *
     * @param quizId The quiz ID
     * @return Optional Quiz if found and active
     */
    Optional<Quiz> findByQuizIdAndIsActiveTrue(Integer quizId);

    /**
     * Find all quizzes by parent type and parent ID (for active quizzes).
     *
     * @param parentType The parent type (course, bundle, course-content)
     * @param parentId The parent ID
     * @return List of quizzes for the given parent
     */
    List<Quiz> findByParentTypeAndParentIdAndIsActiveTrue(String parentType, Integer parentId);

    /**
     * Check if a quiz with the same title exists for the same parent.
     *
     * @param title The quiz title
     * @param parentType The parent type
     * @param parentId The parent ID
     * @return true if quiz with same title exists for the parent
     */
    boolean existsByTitleAndParentTypeAndParentId(String title, String parentType, Integer parentId);

    /**
     * Find all quizzes for a specific course (parent_type = 'course').
     *
     * @param courseId The course ID
     * @return List of quizzes for the course
     */
    @Query("SELECT q FROM Quiz q WHERE q.parentType = 'course' AND q.parentId = :courseId AND q.isActive = true")
    List<Quiz> findByCourseId(@Param("courseId") Integer courseId);

    /**
     * Find all quizzes for a specific course content (parent_type = 'course-content').
     *
     * @param courseContentId The course content ID
     * @return List of quizzes for the course content
     */
    @Query("SELECT q FROM Quiz q WHERE q.parentType = 'course-content' AND q.parentId = :courseContentId AND q.isActive = true")
    List<Quiz> findByCourseContentId(@Param("courseContentId") Integer courseContentId);
}

