package com.nt.course_service_lms.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO representing the result of a bulk upload operation for quiz questions.
 * <p>
 * Contains summary statistics (total, success, failure) along with
 * details of any errors and the successfully uploaded questions.
 * </p>
 *
 * <p>Useful for reporting back to the client after a bulk upload process.</p>
 *
 * @author
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkUploadResultDTO {

    /**
     * The total number of questions processed from the uploaded file.
     */
    private int totalQuestions;

    /**
     * The number of questions that were successfully uploaded.
     */
    private int successfulUploads;

    /**
     * The number of questions that failed to upload due to validation or processing errors.
     */
    private int failedUploads;

    /**
     * A list of error messages explaining why certain rows failed to upload.
     */
    private List<String> errors;

    /**
     * A list of DTOs representing the questions that were successfully uploaded.
     */
    private List<QuizQuestionOutDTO> uploadedQuestions;
}
