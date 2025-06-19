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
}

