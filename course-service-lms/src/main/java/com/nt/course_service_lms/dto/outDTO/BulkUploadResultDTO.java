package com.nt.course_service_lms.dto.outDTO;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for bulk upload results
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BulkUploadResultDTO {
    private int totalQuestions;
    private int successfulUploads;
    private int failedUploads;
    private List<String> errors;
    private List<QuizQuestionOutDTO> uploadedQuestions;
}

