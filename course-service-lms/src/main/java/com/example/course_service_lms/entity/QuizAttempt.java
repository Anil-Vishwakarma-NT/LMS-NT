package com.example.course_service_lms.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing a user's attempt at taking a quiz.
 * Maps to the quiz_attempt table.
 */
@Entity
@Table(name = "quiz_attempt")
@Data
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_attempt_id")
    private Long quizAttemptId;

    @Column(name = "attempt", nullable = false)
    private Long attempt;

    @Column(name = "quiz_id", nullable = false)
    private Long quizId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt = LocalDateTime.now();

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @Column(name = "score_details", columnDefinition = "TEXT")
    private String scoreDetails;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public QuizAttempt() {}

    public QuizAttempt(Long quizAttemptId, Long attempt, Long quizId, Long userId,
                       LocalDateTime startedAt, LocalDateTime finishedAt, String scoreDetails,
                       String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.quizAttemptId = quizAttemptId;
        this.attempt = attempt;
        this.quizId = quizId;
        this.userId = userId;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.scoreDetails = scoreDetails;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuizAttempt that)) return false;
        return Objects.equals(quizAttemptId, that.quizAttemptId) &&
                Objects.equals(attempt, that.attempt) &&
                Objects.equals(quizId, that.quizId) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(startedAt, that.startedAt) &&
                Objects.equals(finishedAt, that.finishedAt) &&
                Objects.equals(scoreDetails, that.scoreDetails) &&
                Objects.equals(status, that.status) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quizAttemptId, attempt, quizId, userId, startedAt, finishedAt,
                scoreDetails, status, createdAt, updatedAt);
    }
}
