package com.example.course_service_lms.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing an activity log entry for quiz interactions.
 * Tracks user actions like starting, submitting, pausing, or abandoning a quiz.
 */
@Entity
@Table(name = "quiz_activity_log")
@Data
public class QuizActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Integer logId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "quiz_id", nullable = false)
    private Integer quizId;

    @Column(name = "attempt", nullable = false)
    private Integer attempt;

    @Column(name = "action_type", nullable = false, length = 30)
    private String actionType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public QuizActivityLog() {}

    public QuizActivityLog(Integer logId, Integer userId, Integer quizId, Integer attempt, String actionType,
                           LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.logId = logId;
        this.userId = userId;
        this.quizId = quizId;
        this.attempt = attempt;
        this.actionType = actionType;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuizActivityLog that)) return false;
        return Objects.equals(logId, that.logId) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(quizId, that.quizId) &&
                Objects.equals(attempt, that.attempt) &&
                Objects.equals(actionType, that.actionType) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(logId, userId, quizId, attempt, actionType, createdAt, updatedAt);
    }
}
