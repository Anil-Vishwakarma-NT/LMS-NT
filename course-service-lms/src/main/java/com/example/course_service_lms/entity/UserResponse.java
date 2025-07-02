package com.example.course_service_lms.entity;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing a user's response to a quiz question.
 * Maps to the user_response table in the database
 */
@Entity
@Table(name = "user_response")
@Data
public class UserResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "response_id")
    private Long responseId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "quiz_id", nullable = false)
    private Long quizId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "attempt", nullable = false)
    private Long attempt;

//    @Column(name = "user_answer", nullable = false, columnDefinition = "TEXT")
//    private String userAnswer;
@Column(name = "user_answer", nullable = false, columnDefinition = "jsonb")
@JdbcTypeCode(SqlTypes.JSON)
private String userAnswer;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;

    @Column(name = "points_earned", nullable = false, precision = 5, scale = 2)
    private BigDecimal pointsEarned;

    @Column(name = "time_spent")
    private Long timeSpent; // in seconds

    @Column(name = "answered_at", nullable = false)
    private LocalDateTime answeredAt;

    public UserResponse() {}

    public UserResponse(Long responseId, Long userId, Long quizId, Long questionId,
                        Long attempt, String userAnswer, Boolean isCorrect, BigDecimal pointsEarned,
                        Long timeSpent, LocalDateTime answeredAt) {
        this.responseId = responseId;
        this.userId = userId;
        this.quizId = quizId;
        this.questionId = questionId;
        this.attempt = attempt;
        this.userAnswer = userAnswer;
        this.isCorrect = isCorrect;
        this.pointsEarned = pointsEarned;
        this.timeSpent = timeSpent;
        this.answeredAt = answeredAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserResponse that)) return false;
        return Objects.equals(responseId, that.responseId) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(quizId, that.quizId) &&
                Objects.equals(questionId, that.questionId) &&
                Objects.equals(attempt, that.attempt) &&
                Objects.equals(userAnswer, that.userAnswer) &&
                Objects.equals(isCorrect, that.isCorrect) &&
                Objects.equals(pointsEarned, that.pointsEarned) &&
                Objects.equals(timeSpent, that.timeSpent) &&
                Objects.equals(answeredAt, that.answeredAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(responseId, userId, quizId, questionId, attempt, userAnswer,
                isCorrect, pointsEarned, timeSpent, answeredAt);
    }
}
