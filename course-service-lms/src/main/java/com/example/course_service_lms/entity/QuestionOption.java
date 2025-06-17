package com.example.course_service_lms.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Objects;

/**
 * Entity representing a question option in the quiz system.
 *
 * <p>This entity holds the details of an option for a quiz question, including
 * the option text and whether it's the correct answer.</p>
 *
 * <p>This class maps to the {@code question_option} table in the database.</p>
 */
@Entity
@Table(name = "question_option")
@Data
public class QuestionOption {

    /**
     * Unique identifier for the question option.
     * <p>This is the primary key and is auto-generated.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Integer optionId;

    /**
     * Identifier for the question this option belongs to.
     */
    @Column(name = "question_id", nullable = false)
    private Integer questionId;

    /**
     * Text content of the option.
     * <p>This is the actual answer choice presented to users.</p>
     */
    @Column(name = "option_text", nullable = false, columnDefinition = "TEXT")
    private String optionText;

    /**
     * Flag indicating whether this option is the correct answer.
     * <p>Default value is false.</p>
     */
    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect = false;

    /**
     * All-args constructor used for manually creating a QuestionOption instance.
     *
     * @param optionId     the option ID
     * @param questionId   the question ID this option belongs to
     * @param optionText   the text content of the option
     * @param isCorrect    whether this option is correct
     */
    public QuestionOption(Integer optionId, Integer questionId, String optionText, Boolean isCorrect) {
        this.optionId = optionId;
        this.questionId = questionId;
        this.optionText = optionText;
        this.isCorrect = isCorrect;
    }

    /**
     * Default no-argument constructor required by JPA.
     */
    public QuestionOption() {}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        QuestionOption that = (QuestionOption) o;
        return Objects.equals(optionId, that.optionId) && Objects.equals(questionId, that.questionId) && Objects.equals(optionText, that.optionText) && Objects.equals(isCorrect, that.isCorrect);
    }

    @Override
    public int hashCode() {
        return Objects.hash(optionId, questionId, optionText, isCorrect);
    }
}