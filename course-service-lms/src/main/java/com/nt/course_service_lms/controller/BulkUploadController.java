package com.nt.course_service_lms.controller;

import com.nt.course_service_lms.dto.inDTO.BulkQuizQuestionInDTO;
import com.nt.course_service_lms.dto.outDTO.BulkUploadResultDTO;
import com.nt.course_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.course_service_lms.service.BulkUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for handling bulk upload of quiz questions.
 * <p>
 * Provides endpoints to upload quiz questions in bulk from a file and
 * to download sample templates in CSV or TXT format.
 * </p>
 *
 * <p>
 * Supported file formats: CSV, Excel, TXT
 * </p>
 *
 * @author
 */
@RestController
@RequestMapping("/api/service-api/quiz-questions")
@RequiredArgsConstructor
@Slf4j
public class BulkUploadController {

    /**
     * Service to handle bulk upload logic.
     */
    private final BulkUploadService bulkUploadService;

    /**
     * Endpoint to handle bulk upload of quiz questions.
     * <p>
     * Accepts a file and quiz ID and delegates the processing to the service layer.
     * </p>
     *
     * @param quizId     ID of the quiz to which questions will be uploaded
     * @param file       Multipart file containing the questions
     * @param skipErrors Flag to determine whether to continue processing on errors
     * @return A standardized response containing the result of the upload
     */
    @PostMapping("/bulk-upload")
    public ResponseEntity<StandardResponseOutDTO<BulkUploadResultDTO>> bulkUploadQuestions(
            @RequestParam final Long quizId,
            @RequestParam final MultipartFile file,
            @RequestParam(defaultValue = "false") final boolean skipErrors) {

        log.info("Received bulk upload request for quiz ID: {} with file: {}", quizId, file.getOriginalFilename());

        // Validate file format
        if (!bulkUploadService.validateFile(file)) {
            return ResponseEntity.badRequest()
                    .body(StandardResponseOutDTO.<BulkUploadResultDTO>failure(
                                    "Invalid file format. Please use CSV, Excel, or TXT files."
                            )
                    );
        }

        // Prepare DTO
        BulkQuizQuestionInDTO bulkUploadDTO = new BulkQuizQuestionInDTO();
        bulkUploadDTO.setQuizId(quizId);
        bulkUploadDTO.setFile(file);
        bulkUploadDTO.setSkipErrors(skipErrors);

        // Process the file
        BulkUploadResultDTO result = bulkUploadService.bulkUploadQuestions(bulkUploadDTO);

        // Return appropriate response based on result
        if (result.getFailedUploads() == 0) {
            log.info("Bulk upload completed successfully for quiz ID: {}."
                    + " {} questions uploaded.", quizId, result.getSuccessfulUploads());
            return ResponseEntity.ok(StandardResponseOutDTO.success(result, "All questions uploaded successfully"));
        } else if (result.getSuccessfulUploads() > 0) {
            log.warn("Bulk upload completed with some errors for quiz ID: {}. Success: {}, Failed: {}",
                    quizId, result.getSuccessfulUploads(), result.getFailedUploads());
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .body(StandardResponseOutDTO.success(result, "Upload completed with some errors"));
        } else {
            log.error("Bulk upload failed for quiz ID: {}. All questions failed to upload.", quizId);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(StandardResponseOutDTO.failure("Upload failed: " + String.join(", ", result.getErrors())));
        }
    }

    /**
     * Endpoint to download a sample template for bulk upload.
     * <p>
     * Generates a sample file content based on the requested format.
     * </p>
     *
     * @param format File format (csv or txt). Defaults to "csv".
     * @return ResponseEntity containing sample file content
     */
    @GetMapping("/bulk-upload/template")
    public ResponseEntity<String> downloadTemplate(@RequestParam(defaultValue = "csv") final String format) {
        log.info("Generating sample template for format: {}", format);

        switch (format.toLowerCase()) {
            case "csv":
                return ResponseEntity.ok()
                        .header("Content-Disposition", "attachment; filename=quiz_questions_template.csv")
                        .header("Content-Type", "text/csv")
                        .body(getCsvTemplate());
            case "txt":
                return ResponseEntity.ok()
                        .header("Content-Disposition", "attachment; filename=quiz_questions_template.txt")
                        .header("Content-Type", "text/plain")
                        .body(getTxtTemplate());
            default:
                return ResponseEntity.badRequest().body("Invalid format. Use 'csv' or 'txt'");
        }
    }

    /**
     * Helper method to return a sample CSV template for quiz questions.
     *
     * @return String containing CSV headers and example rows
     */
    private String getCsvTemplate() {
        return "Question Text,Question Type,Options,Correct Answer,Points,Explanation,Required\n"
                + "\"What is the capital of France?\",MCQ_SINGLE,\"[\"\"Paris\"\""
                + ",\"\"London\"\",\"\"Berlin\"\",\"\"Madrid\"\"]\",\"[\"\"Paris\"\"]\",1.0,\"Paris"
                + " is the capital city of France.\",true\n"
                + "\"Select all prime numbers\",MCQ_MULTIPLE,\"[\"\"2\"\",\"\"3\"\","
                + "\"\"4\"\",\"\"5\"\"]\",\"[\"\"2\"\",\"\"3\"\",\"\"5\"\"]\",2.0,"
                + "\"Prime numbers are 2, 3, and 5.\",true\n"
                + "\"What is 2+2?\",SHORT_ANSWER,\"[]\",\"[\"\"4\"\"]\",1.0,"
                + "\"Basic arithmetic.\",false\n"
                + "\"Which programming languages are object-oriented?\","
                + "MCQ_MULTIPLE,\"[\"\"Java\"\",\"\"Python\"\",\"\"C\"\""
                + ",\"\"JavaScript\"\"]\",\"[\"\"Java\"\",\"\"Python\"\","
                + "\"\"JavaScript\"\"]\",3.0,\"Java, Python, and JavaScript support OOP.\",true\n"
                + "\"What does API stand for?\",SHORT_ANSWER,\"[]\","
                + "\"[\"\"Application Programming Interface\"\"]\",1.5,\"API"
                + " is a set of protocols and tools for building software applications.\",true";
    }

    /**
     * Helper method to return a sample TXT template for quiz questions.
     *
     * @return String containing sample TXT-formatted quiz question rows
     */
    private String getTxtTemplate() {
        return "QUESTION: What is the capital of France?\n"
                + "TYPE: MCQ_SINGLE\n"
                + "OPTIONS: [\"Paris\",\"London\",\"Berlin\",\"Madrid\"]\n"
                + "ANSWER: [\"Paris\"]\n"
                + "POINTS: 1.0\n"
                + "EXPLANATION: Paris is the capital city of France.\n"
                + "REQUIRED: true\n"
                + "\n"
                + "QUESTION: Select all prime numbers\n"
                + "TYPE: MCQ_MULTIPLE\n"
                + "OPTIONS: [\"2\",\"3\",\"4\",\"5\"]\n"
                + "ANSWER: [\"2\",\"3\",\"5\"]\n"
                + "POINTS: 2.0\n"
                + "EXPLANATION: Prime numbers are 2, 3, and 5.\n"
                + "REQUIRED: true\n"
                + "\n"
                + "QUESTION: What is 2+2?\n"
                + "TYPE: SHORT_ANSWER\n"
                + "OPTIONS: []\n"
                + "ANSWER: [\"4\"]\n"
                + "POINTS: 1.0\n"
                + "EXPLANATION: Basic arithmetic.\n"
                + "REQUIRED: false\n"
                + "\n"
                + "QUESTION: Which programming languages are object-oriented?\n"
                + "TYPE: MCQ_MULTIPLE\n"
                + "OPTIONS: [\"Java\",\"Python\",\"C\",\"JavaScript\"]\n"
                + "ANSWER: [\"Java\",\"Python\",\"JavaScript\"]\n"
                + "POINTS: 3.0\n"
                + "EXPLANATION: Java, Python, and JavaScript support object-oriented programming.\n"
                + "REQUIRED: true\n"
                + "\n"
                + "QUESTION: What does API stand for?\n"
                + "TYPE: SHORT_ANSWER\n"
                + "OPTIONS: []\n"
                + "ANSWER: [\"Application Programming Interface\"]\n"
                + "POINTS: 1.5\n"
                + "EXPLANATION: API is a set of protocols and tools for building software applications.\n"
                + "REQUIRED: true";
    }
}
