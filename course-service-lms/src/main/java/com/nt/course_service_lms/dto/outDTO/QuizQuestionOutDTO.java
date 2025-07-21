package com.nt.course_service_lms.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for outgoing quiz question data.
 * <p>
 * This DTO is used to send quiz question details from the backend to the client,
 * typically when viewing or managing quiz content. It encapsulates the question
 * metadata including the question text, type, options, correct answers, scoring,
 * and other related attributes.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizQuestionOutDTO {

    /**
     * Unique identifier of the question.
     */
    private Long questionId;

    /**
     * Identifier of the quiz to which this question belongs.
     */
    private Long quizId;

    /**
     * The actual text/content of the quiz question.
     */
    private String questionText;

    /**
     * Type of the question.
     * <p>
     * Supported types include:
     * <ul>
     *     <li>MCQ_SINGLE</li>
     *     <li>MCQ_MULTIPLE</li>
     *     <li>SHORT_ANSWER</li>
     * </ul>
     * </p>
     */
    private String questionType;

    /**
     * The options available for the question, stored as a JSON string.
     * <p>
     * Required for multiple choice question types.
     * </p>
     */
    private String options;

    /**
     * The correct answer(s) to the question, stored as a JSON string.
     * <p>
     * Depending on the context, this field may be hidden from clients (e.g., during quiz attempts).
     * </p>
     */
    private String correctAnswer;

    /**
     * The number of points assigned to the question.
     */
    private BigDecimal points;

    /**
     * Explanation for the correct answer.
     * <p>
     * Can be used to provide feedback or clarification to learners after submission.
     * </p>
     */
    private String explanation;

    /**
     * Indicates whether answering this question is mandatory.
     */
    private Boolean required;

    /**
     * The position of the question in the quiz (used for ordering).
     */
    private Integer position;

    /**
     * Timestamp when the question was created.
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp when the question was last updated.
     */
    private LocalDateTime updatedAt;
}
