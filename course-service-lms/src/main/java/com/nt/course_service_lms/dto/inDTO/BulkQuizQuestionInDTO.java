package com.nt.course_service_lms.dto.inDTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * DTO for bulk uploading quiz questions using a file.
 * <p>
 * Accepts a quiz ID, a file (e.g., CSV or Excel), and a flag indicating whether to
 * skip invalid entries during the processing.
 * </p>
 * <p>
 * This DTO is typically used in endpoints that support importing multiple questions
 * into a quiz in a single operation.
 * </p>
 *
 * <p><b>Validation:</b>
 * <ul>
 *   <li>{@code quizId} must not be null and must be a positive number.</li>
 *   <li>{@code file} must not be null.</li>
 * </ul>
 * </p>
 *
 * @author
 */
@Data
public class BulkQuizQuestionInDTO {

    /**
     * The ID of the quiz to which the questions will be added.
     * <p>Must be a positive, non-null value.</p>
     */
    @NotNull(message = "Quiz ID is required")
    @Positive(message = "Quiz ID must be positive")
    private Long quizId;

    /**
     * The file containing the quiz questions in bulk format.
     * <p>This can be a CSV, Excel, or other supported formats.</p>
     */
    @NotNull(message = "File is required")
    private MultipartFile file;

    /**
     * Flag to indicate whether to skip invalid question rows
     * and continue processing the remaining questions.
     * <p>Defaults to {@code false}.</p>
     */
    private boolean skipErrors = false;
}
