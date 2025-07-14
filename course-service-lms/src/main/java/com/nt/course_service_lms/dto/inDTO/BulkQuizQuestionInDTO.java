package com.nt.course_service_lms.dto.inDTO;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO for bulk upload of quiz questions
 */
@Data
public class BulkQuizQuestionInDTO {

    @NotNull(message = "Quiz ID is required")
    @Positive(message = "Quiz ID must be positive")
    private Long quizId;

    @NotNull(message = "File is required")
    private MultipartFile file;

    private boolean skipErrors = false; // Whether to skip invalid questions and continue processing
}