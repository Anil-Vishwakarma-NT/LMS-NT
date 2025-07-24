//package com.nt.course_service_lms.controller;
//
//import com.nt.course_service_lms.dto.inDTO.BulkQuizQuestionInDTO;
//import com.nt.course_service_lms.dto.outDTO.BulkUploadResultDTO;
//import com.nt.course_service_lms.dto.outDTO.StandardResponseOutDTO;
//import com.nt.course_service_lms.service.BulkUploadService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
///**
// * REST controller for bulk upload operations
// */
//@RestController
//@RequestMapping("/api/service-api/quiz-questions")
//@RequiredArgsConstructor
//@Slf4j
//public class BulkUploadController {
//
//    private final BulkUploadService bulkUploadService;
//
//    /**
//     * Bulk upload quiz questions from file
//     *
//     * @param quizId     Quiz ID
//     * @param file       File containing questions
//     * @param skipErrors Whether to skip errors and continue processing
//     * @return ResponseEntity containing upload results
//     */
//    @PostMapping("/bulk-upload")
//    public ResponseEntity<StandardResponseOutDTO<BulkUploadResultDTO>> bulkUploadQuestions(
//            @RequestParam Long quizId,
//            @RequestParam MultipartFile file,
//            @RequestParam(defaultValue = "false") boolean skipErrors) {
//
//        log.info("Received bulk upload request for quiz ID: {} with file: {}", quizId, file.getOriginalFilename());
//
//        // Validate file
//        if (!bulkUploadService.validateFile(file)) {
//            return ResponseEntity.badRequest()
//                    .body(StandardResponseOutDTO.<BulkUploadResultDTO>failure("Invalid file format. Please use CSV, Excel, or TXT files."));
//        }
//
//        // Create bulk upload DTO
//        BulkQuizQuestionInDTO bulkUploadDTO = new BulkQuizQuestionInDTO();
//        bulkUploadDTO.setQuizId(quizId);
//        bulkUploadDTO.setFile(file);
//        bulkUploadDTO.setSkipErrors(skipErrors);
//
//        // Process bulk upload
//        BulkUploadResultDTO result = bulkUploadService.bulkUploadQuestions(bulkUploadDTO);
//
//        // Determine response status based on results
//        if (result.getFailedUploads() == 0) {
//            log.info("Bulk upload completed successfully for quiz ID: {}. {} questions uploaded.", quizId, result.getSuccessfulUploads());
//            return ResponseEntity.ok(StandardResponseOutDTO.success(result, "All questions uploaded successfully"));
//        } else if (result.getSuccessfulUploads() > 0) {
//            log.warn("Bulk upload completed with some errors for quiz ID: {}. Success: {}, Failed: {}",
//                    quizId, result.getSuccessfulUploads(), result.getFailedUploads());
//            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
//                    .body(StandardResponseOutDTO.success(result, "Upload completed with some errors"));
//        } else {
//            log.error("Bulk upload failed for quiz ID: {}. All questions failed to upload.", quizId);
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(StandardResponseOutDTO.failure("Upload failed: " + String.join(", ", result.getErrors())));
//        }
//    }
//
//    /**
//     * Download sample template file
//     *
//     * @param format File format (csv, excel, txt)
//     * @return ResponseEntity with sample file content
//     */
//    @GetMapping("/bulk-upload/template")
//    public ResponseEntity<String> downloadTemplate(@RequestParam(defaultValue = "csv") String format) {
//        log.info("Generating sample template for format: {}", format);
//
//        switch (format.toLowerCase()) {
//            case "csv":
//                return ResponseEntity.ok()
//                        .header("Content-Disposition", "attachment; filename=quiz_questions_template.csv")
//                        .header("Content-Type", "text/csv")
//                        .body(getCsvTemplate());
//            case "txt":
//                return ResponseEntity.ok()
//                        .header("Content-Disposition", "attachment; filename=quiz_questions_template.txt")
//                        .header("Content-Type", "text/plain")
//                        .body(getTxtTemplate());
//            default:
//                return ResponseEntity.badRequest().body("Invalid format. Use 'csv' or 'txt'");
//        }
//    }
//
//    private String getCsvTemplate() {
//        return "Question Text,Question Type,Options,Correct Answer,Points,Explanation,Required\n" +
//                "\"What is the capital of France?\",MCQ_SINGLE,\"[{\"\"option\"\":\"\"A\"\",\"\"text\"\":\"\"Paris\"\"},{\"\"option\"\":\"\"B\"\",\"\"text\"\":\"\"London\"\"},{\"\"option\"\":\"\"C\"\",\"\"text\"\":\"\"Berlin\"\"},{\"\"option\"\":\"\"D\"\",\"\"text\"\":\"\"Madrid\"\"}]\",\"[\"\"A\"\"]\",1.0,\"Paris is the capital city of France.\",true\n" +
//                "\"Select all prime numbers\",MCQ_MULTIPLE,\"[{\"\"option\"\":\"\"A\"\",\"\"text\"\":\"\"2\"\"},{\"\"option\"\":\"\"B\"\",\"\"text\"\":\"\"3\"\"},{\"\"option\"\":\"\"C\"\",\"\"text\"\":\"\"4\"\"},{\"\"option\"\":\"\"D\"\",\"\"text\"\":\"\"5\"\"}]\",\"[\"\"A\"\",\"\"B\"\",\"\"D\"\"]\",2.0,\"Prime numbers are 2, 3, and 5.\",true\n" +
//                "\"What is 2+2?\",SHORT_ANSWER,\"\",\"4\",1.0,\"Basic arithmetic.\",false";
//    }
//
//    private String getTxtTemplate() {
//        return "QUESTION: What is the capital of France?\n" +
//                "TYPE: MCQ_SINGLE\n" +
//                "OPTIONS: [{\"option\":\"A\",\"text\":\"Paris\"},{\"option\":\"B\",\"text\":\"London\"},{\"option\":\"C\",\"text\":\"Berlin\"},{\"option\":\"D\",\"text\":\"Madrid\"}]\n" +
//                "ANSWER: [\"A\"]\n" +
//                "POINTS: 1.0\n" +
//                "EXPLANATION: Paris is the capital city of France.\n" +
//                "REQUIRED: true\n" +
//                "\n" +
//                "QUESTION: Select all prime numbers\n" +
//                "TYPE: MCQ_MULTIPLE\n" +
//                "OPTIONS: [{\"option\":\"A\",\"text\":\"2\"},{\"option\":\"B\",\"text\":\"3\"},{\"option\":\"C\",\"text\":\"4\"},{\"option\":\"D\",\"text\":\"5\"}]\n" +
//                "ANSWER: [\"A\",\"B\",\"D\"]\n" +
//                "POINTS: 2.0\n" +
//                "EXPLANATION: Prime numbers are 2, 3, and 5.\n" +
//                "REQUIRED: true\n" +
//                "\n" +
//                "QUESTION: What is 2+2?\n" +
//                "TYPE: SHORT_ANSWER\n" +
//                "OPTIONS: \n" +
//                "ANSWER: 4\n" +
//                "POINTS: 1.0\n" +
//                "EXPLANATION: Basic arithmetic.\n" +
//                "REQUIRED: false";
//    }
//}
