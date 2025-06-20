package com.example.course_service_lms.entity;

import jakarta.persistence.*;
import lombok.Data;

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
public class UserResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "response_id")
    private Integer responseId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "quiz_id", nullable = false)
    private Integer quizId;

    @Column(name = "question_id", nullable = false)
    private Integer questionId;

    @Column(name = "attempt", nullable = false)
    private Integer attempt;

    @Lob
    @Column(name = "user_answer", nullable = false, columnDefinition = "JSONB")
    private String userAnswer;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;

    @Column(name = "points_earned", nullable = false, precision = 5, scale = 2)
    private BigDecimal pointsEarned;

    @Column(name = "time_spent")
    private Integer timeSpent; // in seconds

    @Column(name = "answered_at", nullable = false)
    private LocalDateTime answeredAt;

    public UserResponse() {}

    public UserResponse(Integer responseId, Integer userId, Integer quizId, Integer questionId,
                        Integer attempt, String userAnswer, Boolean isCorrect, BigDecimal pointsEarned,
                        Integer timeSpent, LocalDateTime answeredAt) {
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
