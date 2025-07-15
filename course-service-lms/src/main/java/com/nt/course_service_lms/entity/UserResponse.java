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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing a user's response to a quiz question.
 * Maps to the user_response table in the database.
 */
@Entity
@Table(name = "user_response")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    /**
     * Unique identifier for the user response.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "response_id")
    private Long responseId;

    /**
     * ID of the user who submitted the response.
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * ID of the quiz associated with the response.
     */
    @Column(name = "quiz_id", nullable = false)
    private Long quizId;

    /**
     * ID of the question answered by the user.
     */
    @Column(name = "question_id", nullable = false)
    private Long questionId;

    /**
     * Attempt number of the quiz when this response was submitted.
     */
    @Column(name = "attempt", nullable = false)
    private Long attempt;

    /**
     * The user's submitted answer (stored as text).
     */
    @Column(name = "user_answer", nullable = false, columnDefinition = "TEXT")
    private String userAnswer;

    /**
     * Indicates whether the user's answer was correct.
     */
    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;

    /**
     * Points earned by the user for this response.
     */
    @Column(name = "points_earned", nullable = false, precision = CommonConstants.NUMBER_FIVE, scale = 2)
    private BigDecimal pointsEarned;

    /**
     * Timestamp when the user answered the question.
     */
    @Column(name = "answered_at", nullable = false)
    private LocalDateTime answeredAt;

    /**
     * Equality check based on all fields.
     *
     * @param o Object to compare
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserResponse that)) {
            return false;
        }
        return Objects.equals(responseId, that.responseId)
                && Objects.equals(userId, that.userId)
                && Objects.equals(quizId, that.quizId)
                && Objects.equals(questionId, that.questionId)
                && Objects.equals(attempt, that.attempt)
                && Objects.equals(userAnswer, that.userAnswer)
                && Objects.equals(isCorrect, that.isCorrect)
                && Objects.equals(pointsEarned, that.pointsEarned)
                && Objects.equals(answeredAt, that.answeredAt);
    }

    /**
     * Generates hash code based on all fields.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(responseId, userId, quizId, questionId, attempt, userAnswer,
                isCorrect, pointsEarned, answeredAt);
    }
}
