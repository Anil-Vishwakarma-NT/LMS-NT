package com.example.course_service_lms.dto.outDTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
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
