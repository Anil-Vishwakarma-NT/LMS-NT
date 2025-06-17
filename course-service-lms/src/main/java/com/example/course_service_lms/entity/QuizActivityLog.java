package com.example.course_service_lms.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing an activity log entry for quiz interactions.
 *
 * <p>This entity tracks all user activities during quiz attempts, including
 * question answers, navigation, and other interactions for audit purposes.</p>
 *
 * <p>This class maps to the {@code quiz_activity_log} table in the database.</p>
 */
@Entity
@Table(name = "quiz_activity_log")
@Data
public class QuizActivityLog {

    /**
     * Unique identifier for the activity log entry.
     * <p>This is the primary key and is auto-generated.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Integer logId;

    /**
     * Identifier for the user who performed the action.
     */
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    /**
     * Identifier for the quiz where the action occurred.
     */
    @Column(name = "quiz_id", nullable = false)
    private Integer quizId;

    /**
     * Identifier for the quiz attempt (if applicable).
     * <p>May be null for actions that occur outside of an attempt.</p>
     */
    @Column(name = "attempt_id")
    private Integer attemptId;

    /**
     * Type of action performed (e.g., "START_QUIZ", "ANSWER_QUESTION", "SUBMIT_QUIZ").
     */
    @Column(name = "action_type", nullable = false, length = 30)
    private String actionType;

    /**
     * Identifier for the question (if applicable).
     * <p>Used when the action relates to a specific question.</p>
     */
    @Column(name = "question_id")
    private Integer questionId;

    /**
     * Identifier for the selected option (if applicable).
     * <p>Used when the action involves selecting an answer option.</p>
     */
    @Column(name = "option_id")
    private Integer optionId;

    /**
     * Text answer provided by the user (for open-ended questions).
     */
    @Column(name = "answer_text", columnDefinition = "TEXT")
    private String answerText;

    /**
     * Flag indicating whether the action/answer was correct.
     * <p>Used for tracking answer accuracy.</p>
     */
    @Column(name = "is_correct")
    private Boolean isCorrect;

    /**
     * Additional metadata stored as JSON.
     * <p>Can contain extra information about the action or context.</p>
     */
    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata;

    /**
     * Timestamp when the action was performed.
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * All-args constructor used for manually creating a QuizActivityLog instance.
     *
     * @param logId       the log entry ID
     * @param userId      the user ID
     * @param quizId      the quiz ID
     * @param attemptId   the attempt ID
     * @param actionType  the type of action
     * @param questionId  the question ID
     * @param optionId    the option ID
     * @param answerText  the text answer
     * @param isCorrect   whether the action was correct
     * @param metadata    additional metadata
     * @param createdAt   when the action occurred
     */
    public QuizActivityLog(Integer logId, Integer userId, Integer quizId, Integer attemptId, String actionType,
                           Integer questionId, Integer optionId, String answerText, Boolean isCorrect,
                           String metadata, LocalDateTime createdAt) {
        this.logId = logId;
        this.userId = userId;
        this.quizId = quizId;
        this.attemptId = attemptId;
        this.actionType = actionType;
        this.questionId = questionId;
        this.optionId = optionId;
        this.answerText = answerText;
        this.isCorrect = isCorrect;
        this.metadata = metadata;
        this.createdAt = createdAt;
    }

    /**
     * Default no-argument constructor required by JPA.
     */
    public QuizActivityLog() {}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        QuizActivityLog that = (QuizActivityLog) o;
        return Objects.equals(logId, that.logId) && Objects.equals(userId, that.userId) && Objects.equals(quizId, that.quizId) && Objects.equals(attemptId, that.attemptId) && Objects.equals(actionType, that.actionType) && Objects.equals(questionId, that.questionId) && Objects.equals(optionId, that.optionId) && Objects.equals(answerText, that.answerText) && Objects.equals(isCorrect, that.isCorrect) && Objects.equals(metadata, that.metadata) && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(logId, userId, quizId, attemptId, actionType, questionId, optionId, answerText, isCorrect, metadata, createdAt);
    }
}
