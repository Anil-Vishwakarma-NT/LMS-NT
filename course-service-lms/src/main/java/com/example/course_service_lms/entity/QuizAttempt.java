package com.example.course_service_lms.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing a user's attempt at taking a quiz.
 *
 * <p>This entity tracks individual quiz attempts including timing, scoring,
 * and completion status for each user's quiz session.</p>
 *
 * <p>This class maps to the {@code quiz_attempt} table in the database.</p>
 */
@Entity
@Table(name = "quiz_attempt")
@Data
public class QuizAttempt {

    /**
     * Unique identifier for the quiz attempt.
     * <p>This is the primary key and is auto-generated.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attempt_id")
    private Integer attemptId;

    /**
     * Identifier for the quiz being attempted.
     */
    @Column(name = "quiz_id", nullable = false)
    private Integer quizId;

    /**
     * Identifier for the user taking the quiz.
     */
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    /**
     * Timestamp when the quiz attempt was started.
     */
    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt = LocalDateTime.now();

    /**
     * Timestamp when the quiz attempt was finished.
     * <p>Null if the attempt is still in progress.</p>
     */
    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    /**
     * Score achieved by the user.
     * <p>Raw score based on correct answers and point values.</p>
     */
    @Column(name = "score", precision = 5, scale = 2)
    private BigDecimal score;

    /**
     * Maximum possible score for this quiz attempt.
     */
    @Column(name = "max_score", precision = 5, scale = 2)
    private BigDecimal maxScore;

    /**
     * Percentage score achieved (score/max_score * 100).
     */
    @Column(name = "percentage", precision = 5, scale = 2)
    private BigDecimal percentage;

    /**
     * Current status of the attempt (e.g., "IN_PROGRESS", "COMPLETED", "ABANDONED").
     */
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    /**
     * All-args constructor used for manually creating a QuizAttempt instance.
     *
     * @param attemptId   the attempt ID
     * @param quizId      the quiz ID
     * @param userId      the user ID
     * @param startedAt   when the attempt started
     * @param finishedAt  when the attempt finished
     * @param score       the achieved score
     * @param maxScore    the maximum possible score
     * @param percentage  the percentage score
     * @param status      the attempt status
     */
    public QuizAttempt(Integer attemptId, Integer quizId, Integer userId, LocalDateTime startedAt,
                       LocalDateTime finishedAt, BigDecimal score, BigDecimal maxScore, BigDecimal percentage, String status) {
        this.attemptId = attemptId;
        this.quizId = quizId;
        this.userId = userId;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.score = score;
        this.maxScore = maxScore;
        this.percentage = percentage;
        this.status = status;
    }

    /**
     * Default no-argument constructor required by JPA.
     */
    public QuizAttempt() {}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        QuizAttempt that = (QuizAttempt) o;
        return Objects.equals(attemptId, that.attemptId) && Objects.equals(quizId, that.quizId) && Objects.equals(userId, that.userId) && Objects.equals(startedAt, that.startedAt) && Objects.equals(finishedAt, that.finishedAt) && Objects.equals(score, that.score) && Objects.equals(maxScore, that.maxScore) && Objects.equals(percentage, that.percentage) && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attemptId, quizId, userId, startedAt, finishedAt, score, maxScore, percentage, status);
    }
}
