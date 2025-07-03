package com.example.course_service_lms.dto.inDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Input DTO for UserResponse operations.
 * <p>
 * This DTO is used for creating and updating user responses to quiz questions.
 * It includes validation annotations to ensure data integrity and proper format.
 * The userAnswer field accepts JSON format to support various question types
 * (multiple choice, text, etc.).
 * </p>
 */
@Data
public class UserResponseInDTO {

    /**
     * The ID of the user who submitted the response.
     * Must be a positive number.
     */
    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be a positive number")
    private Long userId;

    /**
     * The ID of the quiz being attempted.
     * Must be a positive number.
     */
    @NotNull(message = "Quiz ID is required")
    @Positive(message = "Quiz ID must be a positive number")
    private Long quizId;

    /**
     * The ID of the specific question being answered.
     * Must be a positive number.
     */
    @NotNull(message = "Question ID is required")
    @Positive(message = "Question ID must be a positive number")
    private Long questionId;

    /**
     * The attempt number for this quiz by the user.
     * Must be a positive number starting from 1.
     */
    @NotNull(message = "Attempt number is required")
    @Positive(message = "Attempt number must be a positive number")
    private Long attempt;

    /**
     * The user's answer in JSON format.
     * Supports various answer types like single choice, multiple choice, text, etc.
     * Cannot be null or empty.
     */
    @NotBlank(message = "User answer is required")
    @Size(max = 10000, message = "User answer cannot exceed 10000 characters")
    private String userAnswer;

    /**
     * Timestamp when the answer was submitted.
     * If not provided, will be set to current time during processing.
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime answeredAt;

    /**
     * Default constructor for JSON deserialization.
     */
    public UserResponseInDTO() {}

    /**
     * Constructor with all fields for programmatic creation.
     *
     * @param userId        the ID of the user
     * @param quizId        the ID of the quiz
     * @param questionId    the ID of the question
     * @param attempt       the attempt number
     * @param userAnswer    the user's answer in JSON format
     * @param answeredAt    timestamp when answered
     */
    public UserResponseInDTO(Long userId, Long quizId, Long questionId, Long attempt,
                             String userAnswer, LocalDateTime answeredAt) {
        this.userId = userId;
        this.quizId = quizId;
        this.questionId = questionId;
        this.attempt = attempt;
        this.userAnswer = userAnswer;
        this.answeredAt = answeredAt;
    }
}
