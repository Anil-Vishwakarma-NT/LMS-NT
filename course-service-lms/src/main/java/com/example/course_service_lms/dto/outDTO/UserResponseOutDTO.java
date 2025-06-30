package com.example.course_service_lms.dto.outDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Output DTO for UserResponse operations.
 * <p>
 * This DTO represents the structure of user response data when returned
 * from API endpoints. It includes all relevant information about a user's
 * response to a quiz question, formatted for client consumption.
 * </p>
 */
@Data
public class UserResponseOutDTO {

    /**
     * Unique identifier for the user response.
     */
    private Long responseId;

    /**
     * The ID of the user who submitted the response.
     */
    private Long userId;

    /**
     * The ID of the quiz being attempted.
     */
    private Long quizId;

    /**
     * The ID of the specific question being answered.
     */
    private Long questionId;

    /**
     * The attempt number for this quiz by the user.
     */
    private Long attempt;

    /**
     * The user's answer in JSON format.
     * Contains the actual response data which can vary based on question type.
     */
    private String userAnswer;

    /**
     * Indicates whether the user's answer is correct.
     */
    private Boolean isCorrect;

    /**
     * Points earned for this response.
     * Represents the score achieved for answering this question.
     */
    private BigDecimal pointsEarned;

    /**
     * Time spent on this question in seconds.
     * Can be null if time tracking was not enabled.
     */
    private Long timeSpent;

    /**
     * Timestamp when the answer was submitted.
     * Formatted as ISO 8601 date-time string.
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime answeredAt;

    /**
     * Default constructor for JSON serialization.
     */
    public UserResponseOutDTO() {}

    /**
     * Constructor with all fields.
     *
     * @param responseId    unique identifier for the response
     * @param userId        the ID of the user
     * @param quizId        the ID of the quiz
     * @param questionId    the ID of the question
     * @param attempt       the attempt number
     * @param userAnswer    the user's answer in JSON format
     * @param isCorrect     whether the answer is correct
     * @param pointsEarned  points earned for this response
     * @param timeSpent     time spent on the question in seconds
     * @param answeredAt    timestamp when answered
     */
    public UserResponseOutDTO(Long responseId, Long userId, Long quizId, Long questionId,
                              Long attempt, String userAnswer, Boolean isCorrect,
                              BigDecimal pointsEarned, Long timeSpent, LocalDateTime answeredAt) {
        this.responseId = responseId;
        this.userId = userId;
        this.quizId = quizId;
        this.questionId = questionId;
        this.attempt = attempt;
        this.userAnswer = userAnswer;
        this.isCorrect = isCorrect;
        this.pointsEarned = pointsEarned;
        this.timeSpent = timeSpent;
        this.answeredAt = answeredAt;
    }
}
