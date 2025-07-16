package com.nt.course_service_lms.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAttemptOutDTO {

    private Long quizAttemptId;

    private Long attempt;

    private Long attemptsLeft;

    private Long quizId;

    private Long userId;

    private LocalDateTime startedAt;

    private LocalDateTime finishedAt;

    private String scoreDetails;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
