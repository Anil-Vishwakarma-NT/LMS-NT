package com.nt.course_service_lms.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for quiz submission results containing attempt details and responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizSubmissionResultOutDTO {

    private QuizAttemptOutDTO quizAttempt;
    private List<UserResponseOutDTO> userResponses;
    private BigDecimal totalScore;
    private BigDecimal maxPossibleScore;
    private Long correctAnswers;
    private Long totalQuestions;
    private BigDecimal percentageScore;
    private String submissionType; // "MANUAL" or "AUTO_TIMEOUT"
    private LocalDateTime submittedAt;
}