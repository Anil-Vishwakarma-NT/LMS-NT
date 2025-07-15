package com.nt.course_service_lms.controller;

import com.nt.course_service_lms.dto.inDTO.QuizQuestionInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateQuizQuestionInDTO;
import com.nt.course_service_lms.dto.outDTO.QuizQuestionOutDTO;
import com.nt.course_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.course_service_lms.service.QuizQuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for managing quiz questions.
 *
 * <p>This controller provides endpoints for creating, retrieving, updating,
 * and deleting quiz questions.</p>
 */
@RestController
@RequestMapping("/api/service-api/quiz-questions")
@RequiredArgsConstructor
@Slf4j
public class QuizQuestionController {

    /**
     * Service for handling quiz question-related business logic.
     * This service provides methods for creating, retrieving, updating,
     * and deleting quiz questions.
     */
    private final QuizQuestionService quizQuestionService;

    /**
     * Creates a new quiz question.
     *
     * @param questionCreateInDTO The DTO containing question creation data
     * @return ResponseEntity containing the created question and success message
     */
    @PostMapping
    public ResponseEntity<StandardResponseOutDTO<QuizQuestionOutDTO>> createQuestion(
            @Valid @RequestBody final QuizQuestionInDTO questionCreateInDTO) {
        log.info("Received request to create question for quiz ID: {}", questionCreateInDTO.getQuizId());

        QuizQuestionOutDTO createdQuestion = quizQuestionService.createQuestion(questionCreateInDTO);

        log.info("Question created successfully with ID: {}", createdQuestion.getQuestionId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponseOutDTO.success(createdQuestion, "Question created successfully"));
    }

    /**
     * Retrieves all questions for a specific quiz.
     *
     * @param quizId The ID of the quiz
     * @return ResponseEntity containing the list of questions and success message
     */
    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<StandardResponseOutDTO<List<QuizQuestionOutDTO>>> getAllQuestionsByQuizId(
            @PathVariable final Long quizId) {
        log.info("Received request to get all questions for quiz ID: {}", quizId);

        List<QuizQuestionOutDTO> questions = quizQuestionService.getQuestionsByQuizId(quizId);

        log.info("Retrieved {} questions for quiz ID: {}", questions.size(), quizId);
        return ResponseEntity.ok(StandardResponseOutDTO.success(questions,
                String.format("Retrieved %d questions successfully", questions.size())));
    }

    /**
     * Retrieves a specific question by its ID.
     *
     * @param questionId The ID of the question to retrieve
     * @return ResponseEntity containing the question and success message
     */
    @GetMapping("/{questionId}")
    public ResponseEntity<StandardResponseOutDTO<QuizQuestionOutDTO>> getQuestionById(
            @PathVariable final Long questionId) {
        log.info("Received request to get question with ID: {}", questionId);

        QuizQuestionOutDTO question = quizQuestionService.getQuestionById(questionId);

        log.info("Question retrieved successfully with ID: {}", questionId);
        return ResponseEntity.ok(StandardResponseOutDTO.success(question, "Question retrieved successfully"));
    }

    /**
     * Updates an existing quiz question.
     *
     * @param questionId The ID of the question to update
     * @param questionUpdateInDTO The DTO containing updated question data
     * @return ResponseEntity containing the updated question and success message
     */
    @PutMapping("/{questionId}")
    public ResponseEntity<StandardResponseOutDTO<QuizQuestionOutDTO>> updateQuestion(
            @PathVariable final Long questionId,
            @Valid @RequestBody final UpdateQuizQuestionInDTO questionUpdateInDTO) {
        log.info("Received request to update question with ID: {}", questionId);

        QuizQuestionOutDTO updatedQuestion = quizQuestionService.updateQuestion(questionId, questionUpdateInDTO);

        log.info("Question updated successfully with ID: {}", questionId);
        return ResponseEntity.ok(StandardResponseOutDTO.success(updatedQuestion, "Question updated successfully"));
    }

    /**
     * Deletes a quiz question by its ID.
     *
     * @param questionId The ID of the question to delete
     * @return ResponseEntity containing success message
     */
    @DeleteMapping("/{questionId}")
    public ResponseEntity<StandardResponseOutDTO<Void>> deleteQuestion(
            @PathVariable final Long questionId) {
        log.info("Received request to delete question with ID: {}", questionId);

        quizQuestionService.deleteQuestion(questionId);

        log.info("Question deleted successfully with ID: {}", questionId);
        return ResponseEntity.ok(StandardResponseOutDTO.success(null, "Question deleted successfully"));
    }
}
