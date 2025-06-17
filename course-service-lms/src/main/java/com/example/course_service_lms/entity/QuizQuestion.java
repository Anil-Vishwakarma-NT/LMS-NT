package com.example.course_service_lms.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing a question within a quiz.
 *
 * <p>This entity holds the details of individual questions including the question text,
 * type, point value, and position within the quiz.</p>
 *
 * <p>This class maps to the {@code quiz_question} table in the database.</p>
 */
@Entity
@Table(name = "quiz_question")
@Data
public class QuizQuestion {

    /**
     * Unique identifier for the quiz question.
     * <p>This is the primary key and is auto-generated.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Integer questionId;

    /**
     * Identifier for the quiz this question belongs to.
     */
    @Column(name = "quiz_id", nullable = false)
    private Integer quizId;

    /**
     * Text content of the question.
     * <p>This is the actual question presented to users.</p>
     */
    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    /**
     * Type of question (e.g., "SINGLE-MULTIPLE_CHOICE", "MULTI-SELECT", "TEXT").
     */
    @Column(name = "question_type", nullable = false, length = 20)
    private String questionType;

    /**
     * Point value for this question.
     * <p>Default value is 1.0 point.</p>
     */
    @Column(name = "points", nullable = false, precision = 6, scale = 2)
    private BigDecimal points = BigDecimal.ONE;

    /**
     * Explanation or feedback for the question.
     * <p>Shown to users after they answer or when reviewing results.</p>
     */
    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    /**
     * Flag indicating whether this question is required to be answered.
     * <p>Default value is true.</p>
     */
    @Column(name = "required", nullable = false)
    private Boolean required = true;

    /**
     * Position/order of the question within the quiz.
     * <p>Used to determine the sequence of questions.</p>
     */
    @Column(name = "position", nullable = false)
    private Integer position;

    /**
     * Timestamp when the question was created.
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * All-args constructor used for manually creating a QuizQuestion instance.
     *
     * @param questionId    the question ID
     * @param quizId        the quiz ID
     * @param questionText  the question text
     * @param questionType  the type of question
     * @param points        the point value
     * @param explanation   the explanation/feedback
     * @param required      whether the question is required
     * @param position      the position in the quiz
     * @param createdAt     when the question was created
     */
    public QuizQuestion(Integer questionId, Integer quizId, String questionText, String questionType,
                        BigDecimal points, String explanation, Boolean required, Integer position, LocalDateTime createdAt) {
        this.questionId = questionId;
        this.quizId = quizId;
        this.questionText = questionText;
        this.questionType = questionType;
        this.points = points;
        this.explanation = explanation;
        this.required = required;
        this.position = position;
        this.createdAt = createdAt;
    }

    /**
     * Default no-argument constructor required by JPA.
     */
    public QuizQuestion() {}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        QuizQuestion that = (QuizQuestion) o;
        return Objects.equals(questionId, that.questionId) && Objects.equals(quizId, that.quizId) && Objects.equals(questionText, that.questionText) && Objects.equals(questionType, that.questionType) && Objects.equals(points, that.points) && Objects.equals(explanation, that.explanation) && Objects.equals(required, that.required) && Objects.equals(position, that.position) && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionId, quizId, questionText, questionType, points, explanation, required, position, createdAt);
    }
}
