package com.example.course_service_lms.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
/**
 * Entity representing a question within a quiz.
 * Maps to the quiz_question table.
 */
@Entity
@Table(name = "quiz_question")
@Data
public class QuizQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Integer questionId;

    @Column(name = "quiz_id", nullable = false)
    private Integer quizId;

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Column(name = "question_type", nullable = false, length = 20)
    private String questionType;

    @Lob
    @Column(name = "options", columnDefinition = "JSONB")
    private String options;

    @Lob
    @Column(name = "correct_answer", nullable = false, columnDefinition = "JSONB")
    private String correctAnswer;

    @Column(name = "points", nullable = false, precision = 5, scale = 2)
    private BigDecimal points;

    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "required", nullable = false)
    private Boolean required;

    @Column(name = "question_position", nullable = false)
    private Integer position;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public QuizQuestion() {}

    public QuizQuestion(Integer questionId, Integer quizId, String questionText, String questionType,
                        String options, String correctAnswer, BigDecimal points, String explanation,
                        Boolean required, Integer position, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.questionId = questionId;
        this.quizId = quizId;
        this.questionText = questionText;
        this.questionType = questionType;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.points = points;
        this.explanation = explanation;
        this.required = required;
        this.position = position;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuizQuestion that)) return false;
        return Objects.equals(questionId, that.questionId) &&
                Objects.equals(quizId, that.quizId) &&
                Objects.equals(questionText, that.questionText) &&
                Objects.equals(questionType, that.questionType) &&
                Objects.equals(options, that.options) &&
                Objects.equals(correctAnswer, that.correctAnswer) &&
                Objects.equals(points, that.points) &&
                Objects.equals(explanation, that.explanation) &&
                Objects.equals(required, that.required) &&
                Objects.equals(position, that.position) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionId, quizId, questionText, questionType, options, correctAnswer,
                points, explanation, required, position, createdAt, updatedAt);
    }
}
