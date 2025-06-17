package com.example.course_service_lms.entity;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing a user's response to a quiz question.
 *
 * <p>This entity stores the answer provided by a user for a specific question
 * during a quiz attempt, along with metadata about the response.</p>
 *
 * <p>This class maps to the {@code question_response} table in the database.</p>
 */
@Entity
@Table(name = "question_response")
@Data
public class QuestionResponse {

    /**
     * Unique identifier for the question response.
     * <p>This is the primary key and is auto-generated.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "response_id")
    private Integer responseId;

    /**
     * Identifier for the quiz attempt this response belongs to.
     */
    @Column(name = "attempt_id", nullable = false)
    private Integer attemptId;

    /**
     * Identifier for the question being answered.
     */
    @Column(name = "question_id", nullable = false)
    private Integer questionId;

    /**
     * Text answer provided by the user for open-ended questions.
     * <p>This field is optional and used for text-based responses.</p>
     */
    @Column(name = "answer_text", columnDefinition = "TEXT")
    private String answerText;

    /**
     * Identifier for the selected option (for multiple choice questions).
     * <p>This field is optional and used for option-based responses.</p>
     */
    @Column(name = "option_id")
    private Integer optionId;

    /**
     * Flag indicating whether the response is correct.
     */
    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;

    /**
     * Timestamp when the question was answered.
     * <p>Defaults to current timestamp when the response is created.</p>
     */
    @Column(name = "answered_at", nullable = false)
    private LocalDateTime answeredAt = LocalDateTime.now();

    /**
     * All-args constructor used for manually creating a QuestionResponse instance.
     *
     * @param responseId   the response ID
     * @param attemptId    the attempt ID
     * @param questionId   the question ID
     * @param answerText   the text answer
     * @param optionId     the selected option ID
     * @param isCorrect    whether the response is correct
     * @param answeredAt   when the question was answered
     */
    public QuestionResponse(Integer responseId, Integer attemptId, Integer questionId, String answerText,
                            Integer optionId, Boolean isCorrect, LocalDateTime answeredAt) {
        this.responseId = responseId;
        this.attemptId = attemptId;
        this.questionId = questionId;
        this.answerText = answerText;
        this.optionId = optionId;
        this.isCorrect = isCorrect;
        this.answeredAt = answeredAt;
    }

    /**
     * Default no-argument constructor required by JPA.
     */
    public QuestionResponse() {}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        QuestionResponse that = (QuestionResponse) o;
        return Objects.equals(responseId, that.responseId) && Objects.equals(attemptId, that.attemptId) && Objects.equals(questionId, that.questionId) && Objects.equals(answerText, that.answerText) && Objects.equals(optionId, that.optionId) && Objects.equals(isCorrect, that.isCorrect) && Objects.equals(answeredAt, that.answeredAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(responseId, attemptId, questionId, answerText, optionId, isCorrect, answeredAt);
    }
}
