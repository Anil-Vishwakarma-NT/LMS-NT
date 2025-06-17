package com.example.course_service_lms.repository;

import com.example.course_service_lms.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for QuizQuestion entity operations.
 *
 * <p>This interface provides CRUD operations and custom query methods
 * for managing quiz questions in the database.</p>
 */
@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Integer> {
    /**
     * Find all questions for a specific quiz, ordered by position.
     *
     * @param quizId the quiz ID
     * @return list of questions ordered by position
     */
    List<QuizQuestion> findByQuizIdOrderByPosition(Integer quizId);

    /**
     * Find a question by quiz ID and position.
     *
     * @param quizId the quiz ID
     * @param position the question position
     * @return optional question
     */
    Optional<QuizQuestion> findByQuizIdAndPosition(Integer quizId, Integer position);

    /**
     * Count questions for a specific quiz.
     *
     * @param quizId the quiz ID
     * @return number of questions in the quiz
     */
    long countByQuizId(Integer quizId);

    /**
     * Delete all questions for a specific quiz.
     *
     * @param quizId the quiz ID
     */
    void deleteByQuizId(Integer quizId);

    /**
     * Find questions by quiz ID and question type.
     *
     * @param quizId the quiz ID
     * @param questionType the question type
     * @return list of questions matching the criteria
     */
    List<QuizQuestion> findByQuizIdAndQuestionType(Integer quizId, String questionType);

    /**
     * Check if a question exists at a specific position in a quiz.
     *
     * @param quizId the quiz ID
     * @param position the position to check
     * @return true if a question exists at that position
     */
    boolean existsByQuizIdAndPosition(Integer quizId, Integer position);

    /**
     * Get the maximum position for questions in a quiz.
     *
     * @param quizId the quiz ID
     * @return the maximum position, or null if no questions exist
     */
    @Query("SELECT MAX(q.position) FROM QuizQuestion q WHERE q.quizId = :quizId")
    Integer findMaxPositionByQuizId(@Param("quizId") Integer quizId);
}

