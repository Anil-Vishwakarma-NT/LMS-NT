package com.nt.course_service_lms.dto.outDTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for outgoing quiz question data.
 * Used for returning quiz question information to clients.
 */
@Data
public class QuizQuestionOutDTO {

    private Long questionId;
    private Long quizId;
    private String questionText;
    private String questionType;
    private String options; // JSON string for question options
    private String correctAnswer; // JSON string for correct answer(s) - may be hidden based on context
    private BigDecimal points;
    private String explanation;
    private Boolean required;
    private Integer position;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor for all fields
    public QuizQuestionOutDTO(Long questionId, Long quizId, String questionText,
                              String questionType, String options, String correctAnswer,
                              BigDecimal points, String explanation, Boolean required,
                              Integer position, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.questionId = questionId;
        this.quizId = quizId;
        this.questionText = questionText;
        this.questionType = questionType;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.points = points;
        this.explanation = explanation;
        this.required = required;
        this.position = position;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Default constructor
    public QuizQuestionOutDTO() {
    }
}