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
 * Entity representing a quiz in the Learning Management System.
 * This entity holds the details of a quiz including configuration settings
 * such as time limits, attempts allowed, and scoring parameters.
 * This class maps to the quiz table in the database.
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "quiz")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quiz {

    /**
     * Unique identifier for the quiz.
     * This is the primary key and is auto-generated using identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_id")
    private Long quizId;

    /**
     * Type of the parent entity (e.g., "course", "bundle").
     * Indicates what type of content this quiz is attached to.
     * Maximum length is 16 characters and cannot be null.
     */
    @Column(name = "parent_type", nullable = false, length = CommonConstants.NUMBER_SIXTEEN)
    private String parentType;

    /**
     * Identifier for the parent entity this quiz belongs to.
     * Cannot be null and represents the ID of the parent entity.
     */
    @Column(name = "parent_id", nullable = false)
    private Long parentId;

    /**
     * Title of the quiz.
     * This is typically a descriptive name for the quiz and cannot be null.
     */
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * Detailed description of the quiz purpose and content.
     * Stored as TEXT in the database and can be null.
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Time limit for completing the quiz in minutes.
     * If null, the quiz has no time limit.
     */
    @Column(name = "time_limit")
    private Integer timeLimit;

    /**
     * Number of attempts allowed for this quiz.
     * Cannot be null and defaults to 1 if not specified.
     */
    @Column(name = "attempts_allowed", nullable = false)
    private Integer attemptsAllowed = 1;

    /**
     * Minimum score required to pass the quiz.
     * Represented as a percentage (0.00 to 100.00) with precision 5 and scale 2.
     * Can be null if no passing score is required.
     */
    @Column(name = "passing_score", precision = CommonConstants.NUMBER_FIVE, scale = 2)
    private BigDecimal passingScore;

    /**
     * Flag indicating whether questions should be randomized.
     * Cannot be null and defaults to false.
     */
    @Column(name = "randomize_questions", nullable = false)
    private Boolean randomizeQuestions = false;

    /**
     * Flag indicating whether results should be shown to users.
     * Cannot be null and defaults to true.
     */
    @Column(name = "show_results", nullable = false)
    private Boolean showResults = true;

    /**
     * Flag indicating whether the quiz is active.
     * Cannot be null and defaults to true.
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * Identifier for the user who created this quiz.
     * Can be null if the creator is not tracked.
     */
    @Column(name = "created_by")
    private Integer createdBy;

    /**
     * Timestamp when the quiz was created.
     * Cannot be null and defaults to the current timestamp.
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Timestamp when the quiz was last updated.
     * Cannot be null and defaults to the current timestamp.
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    /**
     * Compares this Quiz with another object for equality.
     * Two Quiz objects are considered equal if all their fields are equal.
     * This method follows the general contract of Object.equals().
     *
     * @param o the object to compare with this Quiz
     * @return true if the specified object is equal to this Quiz, false otherwise
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Quiz quiz = (Quiz) o;
        return Objects.equals(quizId, quiz.quizId)
                && Objects.equals(parentType, quiz.parentType)
                && Objects.equals(parentId, quiz.parentId)
                && Objects.equals(title, quiz.title)
                && Objects.equals(description, quiz.description)
                && Objects.equals(timeLimit, quiz.timeLimit)
                && Objects.equals(attemptsAllowed, quiz.attemptsAllowed)
                && Objects.equals(passingScore, quiz.passingScore)
                && Objects.equals(randomizeQuestions, quiz.randomizeQuestions)
                && Objects.equals(showResults, quiz.showResults)
                && Objects.equals(isActive, quiz.isActive)
                && Objects.equals(createdBy, quiz.createdBy)
                && Objects.equals(createdAt, quiz.createdAt)
                && Objects.equals(updatedAt, quiz.updatedAt);
    }

    /**
     * Returns a hash code value for this Quiz object.
     * The hash code is computed based on all fields of the Quiz.
     * This method follows the general contract of Object.hashCode()
     * and is consistent with the equals() method.
     *
     * @return a hash code value for this Quiz object
     */
    @Override
    public int hashCode() {
        return Objects.hash(quizId, parentType, parentId, title, description, timeLimit,
                attemptsAllowed, passingScore, randomizeQuestions, showResults,
                isActive, createdBy, createdAt, updatedAt);
    }
}
