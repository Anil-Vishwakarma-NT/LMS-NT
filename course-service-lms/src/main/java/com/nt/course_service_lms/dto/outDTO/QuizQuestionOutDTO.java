package com.nt.course_service_lms.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for outgoing quiz question data.
 * Used for returning quiz question information to clients.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}