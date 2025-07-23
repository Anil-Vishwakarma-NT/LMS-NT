package com.nt.course_service_lms.dto.outDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
     * Timestamp when the answer was submitted.
     * Formatted as ISO 8601 date-time string.
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime answeredAt;
}
