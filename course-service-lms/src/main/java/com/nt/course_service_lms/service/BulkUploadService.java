package com.nt.course_service_lms.service;

import com.nt.course_service_lms.dto.inDTO.BulkQuizQuestionInDTO;
import com.nt.course_service_lms.dto.outDTO.BulkUploadResultDTO;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for bulk upload operations.
 */
public interface BulkUploadService {

    /**
     * Bulk upload quiz questions from file.
     *
     * @param bulkQuizQuestionInDTO bulk upload request data
     * @return bulk upload result with success/failure details
     */
    BulkUploadResultDTO bulkUploadQuestions(BulkQuizQuestionInDTO bulkQuizQuestionInDTO);

    /**
     * Validate file format and content.
     *
     * @param file uploaded file
     * @return validation result
     */
    boolean validateFile(MultipartFile file);
}
