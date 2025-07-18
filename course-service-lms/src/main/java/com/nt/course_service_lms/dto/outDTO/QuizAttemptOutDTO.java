package com.nt.course_service_lms.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for representing a user's quiz attempt.
 * <p>
 * This DTO provides detailed information about an individual attempt made by a user
 * on a specific quiz, including timing, scoring, and attempt status.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAttemptOutDTO {

    /**
     * Unique identifier for the quiz attempt.
     */
    private Long quizAttemptId;

    /**
     * The current attempt number for this user on the quiz.
     */
    private Long attempt;

    /**
     * The number of attempts remaining for the user on this quiz.
     */
    private Long attemptsLeft;

    /**
     * The ID of the quiz being attempted.
     */
    private Long quizId;

    /**
     * The ID of the user attempting the quiz.
     */
    private Long userId;

    /**
     * The timestamp when the quiz attempt was started.
     */
    private LocalDateTime startedAt;

    /**
     * The timestamp when the quiz attempt was completed.
     */
    private LocalDateTime finishedAt;

    /**
     * JSON or string representation containing detailed scoring information.
     * <p>
     * May include per-question scores, total score, and breakdowns.
     * </p>
     */
    private String scoreDetails;

    /**
     * Status of the quiz attempt.
     * <p>
     * Examples: "IN_PROGRESS", "COMPLETED", "TIMED_OUT", etc.
     * </p>
     */
    private String status;

    /**
     * Timestamp when this quiz attempt record was created.
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp when this quiz attempt record was last updated.
     */
    private LocalDateTime updatedAt;
}
