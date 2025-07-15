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
 * Entity representing an activity log entry for quiz interactions.
 * Tracks user actions like starting, submitting, pausing, or abandoning a quiz.
 * <p>
 * This entity is mapped to the "quiz_activity_log" table in the database and
 * maintains a chronological record of all quiz-related activities performed by users.
 * Each log entry captures the specific action taken, the user who performed it,
 * the quiz involved, and the attempt number.
 * </p>
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "quiz_activity_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizActivityLog {

    /**
     * Primary key for the quiz activity log entry.
     * Auto-generated using database identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Integer logId;

    /**
     * The unique identifier of the user who performed the quiz action.
     * References the user entity in the system.
     */
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    /**
     * The unique identifier of the quiz that was interacted with.
     * References the quiz entity in the system.
     */
    @Column(name = "quiz_id", nullable = false)
    private Integer quizId;

    /**
     * The attempt number for this quiz interaction.
     * Allows tracking multiple attempts by the same user on the same quiz.
     */
    @Column(name = "attempt", nullable = false)
    private Integer attempt;

    /**
     * The type of action performed on the quiz.
     * Common values include: "START", "SUBMIT", "PAUSE", "ABANDON", "RESUME".
     * Maximum length is 30 characters.
     */
    @Column(name = "action_type", nullable = false, length = CommonConstants.NUMBER_THIRTY)
    private String actionType;

    /**
     * Timestamp when this log entry was first created.
     * Automatically set to current time when the entity is instantiated.
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Timestamp when this log entry was last updated.
     * Automatically set to current time when the entity is instantiated.
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    /**
     * Compares this QuizActivityLog with another object for equality.
     * Two QuizActivityLog objects are considered equal if all their field values match.
     *
     * @param o the object to compare with this QuizActivityLog
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof QuizActivityLog that)) {
            return false;
        }
        return Objects.equals(logId, that.logId)
                && Objects.equals(userId, that.userId)
                && Objects.equals(quizId, that.quizId)
                && Objects.equals(attempt, that.attempt)
                && Objects.equals(actionType, that.actionType)
                && Objects.equals(createdAt, that.createdAt)
                && Objects.equals(updatedAt, that.updatedAt);
    }

    /**
     * Generates a hash code for this QuizActivityLog object.
     * The hash code is computed based on all field values to ensure
     * consistency with the equals method.
     *
     * @return the hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(logId, userId, quizId, attempt, actionType, createdAt, updatedAt);
    }
}
