package com.nt.course_service_lms.entity;

import com.nt.course_service_lms.constants.CommonConstants;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing a user's attempt at taking a quiz.
 * Maps to the quiz_attempt table in the database.
 */
@Entity
@Table(name = "quiz_attempt")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    @Column(name = "status", nullable = false, length = CommonConstants.NUMBER_TWENTY)
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
     * Equality check based on all fields.
     *
     * @param o Object to compare
     * @return true if objects are equal, false otherwise
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QuizAttempt that)) {
            return false;
        }
        return Objects.equals(quizAttemptId, that.quizAttemptId)
                && Objects.equals(attempt, that.attempt)
                && Objects.equals(quizId, that.quizId)
                && Objects.equals(userId, that.userId)
                && Objects.equals(startedAt, that.startedAt)
                && Objects.equals(finishedAt, that.finishedAt)
                && Objects.equals(scoreDetails, that.scoreDetails)
                && Objects.equals(status, that.status)
                && Objects.equals(createdAt, that.createdAt)
                && Objects.equals(updatedAt, that.updatedAt);
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
