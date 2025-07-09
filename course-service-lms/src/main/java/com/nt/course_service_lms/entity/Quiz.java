package com.nt.course_service_lms.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing a quiz in the Learning Management System.
 *
 * <p>This entity holds the details of a quiz including configuration settings
 * such as time limits, attempts allowed, and scoring parameters.</p>
 *
 * <p>This class maps to the {@code quiz} table in the database.</p>
 */
@Entity
@Table(name = "quiz")
@Data
public class Quiz {

    /**
     * Unique identifier for the quiz.
     * <p>This is the primary key and is auto-generated.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quiz_id")
    private Long quizId;

    /**
     * Type of the parent entity (e.g., "course", "bundle").
     * <p>Indicates what type of content this quiz is attached to.</p>
     */
    @Column(name = "parent_type", nullable = false, length = 16)
    private String parentType;

    /**
     * Identifier for the parent entity this quiz belongs to.
     */
    @Column(name = "parent_id", nullable = false)
    private Long parentId;

    /**
     * Title of the quiz.
     * <p>This is typically a descriptive name for the quiz.</p>
     */
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * Detailed description of the quiz purpose and content.
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Time limit for completing the quiz in minutes.
     * <p>If null, the quiz has no time limit.</p>
     */
    @Column(name = "time_limit")
    private Integer timeLimit;

    /**
     * Number of attempts allowed for this quiz.
     * <p>Default value is 1.</p>
     */
    @Column(name = "attempts_allowed", nullable = false)
    private Integer attemptsAllowed = 1;

    /**
     * Minimum score required to pass the quiz.
     * <p>Represented as a percentage (0.00 to 100.00).</p>
     */
    @Column(name = "passing_score", precision = 5, scale = 2)
    private BigDecimal passingScore;

    /**
     * Flag indicating whether questions should be randomized.
     * <p>Default value is false.</p>
     */
    @Column(name = "randomize_questions", nullable = false)
    private Boolean randomizeQuestions = false;

    /**
     * Flag indicating whether results should be shown to users.
     * <p>Default value is true.</p>
     */
    @Column(name = "show_results", nullable = false)
    private Boolean showResults = true;

    /**
     * Flag indicating whether the quiz is active.
     * <p>Default value is true.</p>
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * Identifier for the user who created this quiz.
     */
    @Column(name = "created_by")
    private Integer createdBy;

    /**
     * Timestamp when the quiz was created.
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Timestamp when the quiz was last updated.
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    /**
     * All-args constructor used for manually creating a Quiz instance.
     *
     * @param quizId             the quiz ID
     * @param parentType         the parent entity type
     * @param parentId           the parent entity ID
     * @param title              the quiz title
     * @param description        the quiz description
     * @param timeLimit          the time limit in minutes
     * @param attemptsAllowed    number of attempts allowed
     * @param passingScore       minimum passing score
     * @param randomizeQuestions whether to randomize questions
     * @param showResults        whether to show results
     * @param isActive           whether the quiz is active
     * @param createdBy          who created the quiz
     * @param createdAt          when it was created
     * @param updatedAt          when it was last updated
     */
    public Quiz(Long quizId, String parentType, Long parentId, String title, String description,
                Integer timeLimit, Integer attemptsAllowed, BigDecimal passingScore, Boolean randomizeQuestions,
                Boolean showResults, Boolean isActive, Integer createdBy, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.quizId = quizId;
        this.parentType = parentType;
        this.parentId = parentId;
        this.title = title;
        this.description = description;
        this.timeLimit = timeLimit;
        this.attemptsAllowed = attemptsAllowed;
        this.passingScore = passingScore;
        this.randomizeQuestions = randomizeQuestions;
        this.showResults = showResults;
        this.isActive = isActive;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Default no-argument constructor required by JPA.
     */
    public Quiz() {}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Quiz quiz = (Quiz) o;
        return Objects.equals(quizId, quiz.quizId) && Objects.equals(parentType, quiz.parentType) && Objects.equals(parentId, quiz.parentId) && Objects.equals(title, quiz.title) && Objects.equals(description, quiz.description) && Objects.equals(timeLimit, quiz.timeLimit) && Objects.equals(attemptsAllowed, quiz.attemptsAllowed) && Objects.equals(passingScore, quiz.passingScore) && Objects.equals(randomizeQuestions, quiz.randomizeQuestions) && Objects.equals(showResults, quiz.showResults) && Objects.equals(isActive, quiz.isActive) && Objects.equals(createdBy, quiz.createdBy) && Objects.equals(createdAt, quiz.createdAt) && Objects.equals(updatedAt, quiz.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quizId, parentType, parentId, title, description, timeLimit, attemptsAllowed, passingScore, randomizeQuestions, showResults, isActive, createdBy, createdAt, updatedAt);
    }
}
