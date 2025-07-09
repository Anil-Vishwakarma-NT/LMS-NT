package com.nt.course_service_lms.dto.outDTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for quiz output representation.
 */
@Data
public class QuizOutDTO {
    private Long quizId;
    private String parentType;
    private Long parentId;
    private String title;
    private String description;
    private Integer timeLimit;
    private Integer attemptsAllowed;
    private BigDecimal passingScore;
    private Boolean randomizeQuestions;
    private Boolean showResults;
    private Boolean isActive;
    private Integer createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
