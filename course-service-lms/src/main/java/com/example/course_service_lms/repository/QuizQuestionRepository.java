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
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {
    /**
     * Find all questions for a specific quiz, ordered by position.
     *
     * @param quizId the quiz ID
     * @return list of questions ordered by position
     */
    List<QuizQuestion> findByQuizIdOrderByPosition(Long quizId);

    /**
     * Find a question by quiz ID and position.
     *
     * @param quizId the quiz ID
     * @param position the question position
     * @return optional question
     */
    Optional<QuizQuestion> findByQuizIdAndPosition(Long quizId, Integer position);

    /**
     * Count total questions for a specific quiz.
     *
     * @param quizId the quiz ID
     * @return count of questions
     */
    long countByQuizId(Long quizId);

    /**
     * Find questions by quiz ID and position range.
     *
     * @param quizId the quiz ID
     * @param startPosition start position (inclusive)
     * @param endPosition end position (inclusive)
     * @return list of questions in the position range
     */
    @Query("SELECT q FROM QuizQuestion q WHERE q.quizId = :quizId AND q.position BETWEEN :startPosition AND :endPosition ORDER BY q.position")
    List<QuizQuestion> findByQuizIdAndPositionBetween(@Param("quizId") Long quizId,
                                                      @Param("startPosition") Integer startPosition,
                                                      @Param("endPosition") Integer endPosition);

    /**
     * Find questions by quiz ID with position greater than specified position.
     *
     * @param quizId the quiz ID
     * @param position the position threshold
     * @return list of questions with position greater than specified
     */
    List<QuizQuestion> findByQuizIdAndPositionGreaterThan(Long quizId, Integer position);

    /**
     * Find questions by quiz ID with position greater than or equal to specified position.
     *
     * @param quizId the quiz ID
     * @param position the position threshold
     * @return list of questions with position greater than or equal to specified
     */
    List<QuizQuestion> findByQuizIdAndPositionGreaterThanEqual(Long quizId, Integer position);
}
