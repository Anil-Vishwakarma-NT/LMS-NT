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

    /**
     * Unique identifier for the question.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long questionId;

    /**
     * ID of the quiz to which this question belongs.
     */
    @Column(name = "quiz_id", nullable = false)
    private Long quizId;

    /**
     * The text/content of the question.
     */
    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    /**
     * Type of the question (e.g., MULTIPLE_CHOICE, TRUE_FALSE).
     */
    @Column(name = "question_type", nullable = false, length = 20)
    private String questionType;

    /**
     * Available options for the question, typically in JSON or delimited format.
     */
    @Column(name = "options", columnDefinition = "TEXT")
    private String options;

    /**
     * The correct answer(s) for the question.
     */
    @Column(name = "correct_answer", nullable = false, columnDefinition = "TEXT")
    private String correctAnswer;

    /**
     * Points assigned to the question.
     */
    @Column(name = "points", nullable = false, precision = 5, scale = 2)
    private BigDecimal points;

    /**
     * Explanation for the correct answer (optional).
     */
    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    /**
     * Indicates whether answering this question is required.
     */
    @Column(name = "required", nullable = false)
    private Boolean required;

    /**
     * Position of the question within the quiz (ordering).
     */
    @Column(name = "question_position", nullable = false)
    private Integer position;

    /**
     * Timestamp when the question record was created.
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the question record was last updated.
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Default constructor.
     */
    public QuizQuestion() {}

    /**
     * Parameterized constructor to initialize all fields.
     *
     * @param questionId Unique identifier for the question
     * @param quizId ID of the associated quiz
     * @param questionText The actual question content
     * @param questionType Type of the question
     * @param options Possible options for the question
     * @param correctAnswer The correct answer(s)
     * @param points Points awarded for the correct answer
     * @param explanation Explanation of the answer
     * @param required Whether the question is mandatory
     * @param position Position/order of the question in the quiz
     * @param createdAt Creation timestamp
     * @param updatedAt Last updated timestamp
     */
    public QuizQuestion(Long questionId, Long quizId, String questionText, String questionType,
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

    /**
     * Checks equality based on all fields.
     *
     * @param o The object to compare with
     * @return true if objects are equal, false otherwise
     */
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

    /**
     * Generates hash code based on all fields.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(questionId, quizId, questionText, questionType, options, correctAnswer,
                points, explanation, required, position, createdAt, updatedAt);
    }
}
