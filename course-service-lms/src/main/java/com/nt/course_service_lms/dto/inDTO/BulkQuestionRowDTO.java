package com.nt.course_service_lms.dto.inDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO representing a single question row extracted from a bulk upload file.
 * <p>
 * This is used to import questions into the system in bulk, such as for quizzes or exams.
 * It supports various question types and formats (like MCQ, short answers, etc.).
 * </p>
 *
 * <p>Expected formats:
 * <ul>
 *   <li><b>questionType:</b> MCQ_SINGLE, MCQ_MULTIPLE, SHORT_ANSWER</li>
 *   <li><b>options:</b> JSON string representing available choices for MCQs</li>
 *   <li><b>correctAnswer:</b> JSON string representing one or more correct answers</li>
 * </ul>
 * </p>
 *
 * @author
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BulkQuestionRowDTO {

    /**
     * The actual text of the question.
     */
    private String questionText;

    /**
     * The type of the question.
     * <p>
     * Possible values: {@code MCQ_SINGLE}, {@code MCQ_MULTIPLE}, {@code SHORT_ANSWER}.
     * </p>
     */
    private String questionType;

    /**
     * A JSON-formatted string representing the available options for MCQ-type questions.
     * <p>Ignored for short answer questions.</p>
     */
    private String options;

    /**
     * A JSON-formatted string representing the correct answer(s) for the question.
     * <p>
     * Can be a single answer or multiple depending on the question type.
     * </p>
     */
    private String correctAnswer;

    /**
     * The number of points assigned to the question.
     */
    private BigDecimal points;

    /**
     * Explanation for the correct answer, shown optionally after submission.
     */
    private String explanation;

    /**
     * Indicates whether answering this question is required (non-optional).
     */
    private Boolean required;
}
