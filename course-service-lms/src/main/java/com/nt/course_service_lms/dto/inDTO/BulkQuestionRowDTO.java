package com.nt.course_service_lms.dto.inDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO representing a single question row from bulk upload file
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BulkQuestionRowDTO {
    private String questionText;
    private String questionType; // MCQ_SINGLE, MCQ_MULTIPLE, SHORT_ANSWER
    private String options; // JSON string for MCQ options
    private String correctAnswer; // JSON string for correct answer(s)
    private BigDecimal points;
    private String explanation;
    private Boolean required;
}

