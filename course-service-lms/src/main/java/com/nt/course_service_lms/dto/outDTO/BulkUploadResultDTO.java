package com.nt.course_service_lms.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for bulk upload results
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkUploadResultDTO {
    private int totalQuestions;
    private int successfulUploads;
    private int failedUploads;
    private List<String> errors;
    private List<QuizQuestionOutDTO> uploadedQuestions;
}

