package com.nt.course_service_lms.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreviousAttemptDetailsOutDTO {
    private QuizAttemptOutDTO quizAttempt;
    private List<UserResponseOutDTO> userResponses;
    private BigDecimal totalScore;
    private BigDecimal maxPossibleScore;
    private Long correctAnswers;
    private Long totalQuestions;
    private BigDecimal percentageScore;
    private String submissionType;
    private LocalDateTime submittedAt;
}
