package com.nt.course_service_lms.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for sending quiz details to the client.
 * <p>
 * This DTO is used to represent the quiz metadata such as title,
 * description, time limit, number of attempts, scoring, and configuration
 * options such as randomization and visibility of results.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizOutDTO {

    /**
     * Unique identifier for the quiz.
     */
    private Long quizId;

    /**
     * The type of the parent entity to which this quiz is associated.
     * <p>
     * This could be a course, module, or content block.
     * </p>
     */
    private String parentType;

    /**
     * The unique identifier of the parent entity (course, content, etc.)
     * this quiz is attached to.
     */
    private Long parentId;

    /**
     * The title or name of the quiz.
     */
    private String title;

    /**
     * A brief description of the quiz, providing additional context or instructions.
     */
    private String description;

    /**
     * The time limit for completing the quiz, in minutes.
     * <p>
     * A value of {@code null} or {@code 0} may indicate no time limit.
     * </p>
     */
    private Integer timeLimit;

    /**
     * The number of attempts a user is allowed to take the quiz.
     * <p>
     * A value of {@code null} or {@code 0} may indicate unlimited attempts.
     * </p>
     */
    private Integer attemptsAllowed;

    /**
     * The minimum score (as a percentage or raw points) required to pass the quiz.
     */
    private BigDecimal passingScore;

    /**
     * Flag indicating whether the quiz questions should be presented in random order.
     */
    private Boolean randomizeQuestions;

    /**
     * Flag indicating whether to show the quiz results (correct answers, scores) to the user.
     */
    private Boolean showResults;

    /**
     * Flag indicating whether the quiz is currently active or published.
     */
    private Boolean isActive;

    /**
     * The user ID of the creator of the quiz.
     */
    private Integer createdBy;

    /**
     * Timestamp indicating when the quiz was created.
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp indicating the last time the quiz was updated.
     */
    private LocalDateTime updatedAt;
}
