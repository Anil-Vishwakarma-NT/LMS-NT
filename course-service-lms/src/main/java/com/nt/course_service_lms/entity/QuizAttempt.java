package com.nt.course_service_lms.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing a user's attempt at taking a quiz.
 * Maps to the quiz_attempt table in the database.
 */
@Entity
@Table(name = "quiz_attempt")
@Data
public class QuizAttempt {

    /**
     * Unique identifier for the quiz attempt.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_attempt_id")
    private Long quizAttemptId;

    /**
     * Attempt number for the quiz by the user.
     */
    @Column(name = "attempt", nullable = false)
    private Long attempt;

    /**
     * ID of the quiz that was attempted.
     */
    @Column(name = "quiz_id", nullable = false)
    private Long quizId;

    /**
     * ID of the user who attempted the quiz.
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * Timestamp when the quiz attempt was started.
     */
    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt = LocalDateTime.now();

    /**
     * Timestamp when the quiz attempt was finished.
     */
    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    /**
     * JSON or text representation of the score details.
     */
    @Column(name = "score_details", columnDefinition = "TEXT")
    private String scoreDetails;

    /**
     * Status of the quiz attempt (e.g., IN_PROGRESS, COMPLETED).
     */
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    /**
     * Timestamp when the quiz attempt record was created.
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the quiz attempt record was last updated.
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Default constructor.
     */
    public QuizAttempt() {}

    /**
     * Parameterized constructor to initialize all fields.
     *
     * @param quizAttemptId ID of the quiz attempt
     * @param attempt Attempt number
     * @param quizId ID of the quiz
     * @param userId ID of the user
     * @param startedAt Start timestamp
     * @param finishedAt Finish timestamp
     * @param scoreDetails Score details in string format
     * @param status Status of the attempt
     * @param createdAt Creation timestamp
     * @param updatedAt Last updated timestamp
     */
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

    /**
     * Equality check based on all fields.
     *
     * @param o Object to compare
     * @return true if objects are equal, false otherwise
     */
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

    /**
     * Hash code based on all fields.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(quizAttemptId, attempt, quizId, userId, startedAt, finishedAt,
                scoreDetails, status, createdAt, updatedAt);
    }
}
