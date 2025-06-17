package com.example.course_service_lms.repository;

import com.example.course_service_lms.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for QuizQuestion entity operations.
 *
 * <p>This interface provides CRUD operations and custom query methods
 * for managing quiz questions in the database.</p>
 */
@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Integer> {
    /**
     * Finds all questions for a specific quiz ordered by position.
     *
     * @param quizId The ID of the quiz
     * @return List of questions ordered by position
     */
    List<QuizQuestion> findByQuizIdOrderByPosition(Integer quizId);

    /**
     * Checks if a question exists with the given quiz ID and position.
     * Used for validation during question creation.
     *
     * @param quizId The ID of the quiz
     * @param position The position to check
     * @return true if a question exists with the given position in the quiz
     */
    boolean existsByQuizIdAndPosition(Integer quizId, Integer position);

    /**
     * Checks if a question exists with the given quiz ID and position,
     * excluding a specific question ID.
     * Used for validation during question updates.
     *
     * @param quizId The ID of the quiz
     * @param position The position to check
     * @param questionId The question ID to exclude from the check
     * @return true if another question exists with the given position in the quiz
     */
    boolean existsByQuizIdAndPositionAndQuestionIdNot(Integer quizId, Integer position, Integer questionId);

    /**
     * Counts the number of questions in a specific quiz.
     *
     * @param quizId The ID of the quiz
     * @return The number of questions in the quiz
     */
    long countByQuizId(Integer quizId);

    /**
     * Deletes all questions for a specific quiz.
     * Useful for cascade deletion when a quiz is deleted.
     *
     * @param quizId The ID of the quiz
     */
    void deleteByQuizId(Integer quizId);
}

