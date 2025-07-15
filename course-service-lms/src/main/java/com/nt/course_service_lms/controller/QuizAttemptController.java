package com.nt.course_service_lms.controller;

import com.nt.course_service_lms.constants.CommonConstants;
import com.nt.course_service_lms.dto.inDTO.QuizAttemptCreateInDTO;
import com.nt.course_service_lms.dto.inDTO.QuizAttemptUpdateInDTO;
import com.nt.course_service_lms.dto.outDTO.QuizAttemptOutDTO;
import com.nt.course_service_lms.service.QuizAttemptService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing quiz attempts in the Learning Management System.
 * Provides endpoints for creating, updating, retrieving, and managing quiz attempts.
 */
@RestController
@RequestMapping("/api/service-api/quiz-attempt")
@Slf4j
public class QuizAttemptController {

    /**
     * Service layer dependency for quiz attempt operations.
     */
    @Autowired
    private QuizAttemptService quizAttemptService;

    /**
     * Creates a new quiz attempt.
     *
     * @param dto the quiz attempt creation data transfer object containing user ID and quiz ID
     * @return ResponseEntity containing the created quiz attempt data
     */
    @PostMapping
    public ResponseEntity<QuizAttemptOutDTO> createQuizAttempt(@Valid @RequestBody final QuizAttemptCreateInDTO dto) {
        log.info("REST request to create QuizAttempt for user: {} and quiz: {}", dto.getUserId(), dto.getQuizId());

        final QuizAttemptOutDTO createdAttempt = quizAttemptService.createQuizAttempt(dto);
        return new ResponseEntity<>(createdAttempt, HttpStatus.OK);
    }

    /**
     * Updates an existing quiz attempt.
     *
     * @param quizAttemptId the ID of the quiz attempt to update
     * @param dto           the quiz attempt update data transfer object
     * @return ResponseEntity containing the updated quiz attempt data
     */
    @PutMapping("/{quizAttemptId}")
    public ResponseEntity<QuizAttemptOutDTO> updateQuizAttempt(
            @PathVariable final Long quizAttemptId,
            @Valid @RequestBody final QuizAttemptUpdateInDTO dto) {
        log.info("REST request to update QuizAttempt with ID: {}", quizAttemptId);

        final QuizAttemptOutDTO updatedAttempt = quizAttemptService.updateQuizAttempt(quizAttemptId, dto);
        return ResponseEntity.ok(updatedAttempt);
    }

    /**
     * Retrieves a quiz attempt by its ID.
     *
     * @param quizAttemptId the ID of the quiz attempt to retrieve
     * @return ResponseEntity containing the quiz attempt data or 404 if not found
     */
    @GetMapping("/{quizAttemptId}")
    public ResponseEntity<QuizAttemptOutDTO> getQuizAttemptById(@PathVariable final Long quizAttemptId) {
        log.info("REST request to get QuizAttempt with ID: {}", quizAttemptId);

        final Optional<QuizAttemptOutDTO> quizAttempt = quizAttemptService.getQuizAttemptById(quizAttemptId);
        return quizAttempt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Retrieves all quiz attempts with pagination support.
     *
     * @param pageable the pagination information with default size of 20 and sorted by createdAt
     * @return ResponseEntity containing a page of quiz attempts
     */
    @GetMapping
    public ResponseEntity<Page<QuizAttemptOutDTO>> getAllQuizAttempts(
            @PageableDefault(size = CommonConstants.NUMBER_TWENTY, sort = "createdAt") final Pageable pageable) {
        log.info("REST request to get all QuizAttempts with pagination");

        final Page<QuizAttemptOutDTO> quizAttempts = quizAttemptService.getAllQuizAttempts(pageable);
        return ResponseEntity.ok(quizAttempts);
    }

    /**
     * Retrieves all quiz attempts for a specific user.
     *
     * @param userId the ID of the user whose quiz attempts to retrieve
     * @return ResponseEntity containing a list of quiz attempts for the user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<QuizAttemptOutDTO>> getQuizAttemptsByUserId(@PathVariable final Long userId) {
        log.info("REST request to get QuizAttempts for user: {}", userId);

        final List<QuizAttemptOutDTO> quizAttempts = quizAttemptService.getQuizAttemptsByUserId(userId);
        return ResponseEntity.ok(quizAttempts);
    }

    /**
     * Retrieves all quiz attempts for a specific quiz.
     *
     * @param quizId the ID of the quiz whose attempts to retrieve
     * @return ResponseEntity containing a list of quiz attempts for the quiz
     */
    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<QuizAttemptOutDTO>> getQuizAttemptsByQuizId(@PathVariable final Long quizId) {
        log.info("REST request to get QuizAttempts for quiz: {}", quizId);

        final List<QuizAttemptOutDTO> quizAttempts = quizAttemptService.getQuizAttemptsByQuizId(quizId);
        return ResponseEntity.ok(quizAttempts);
    }

    /**
     * Retrieves all quiz attempts for a specific user and quiz combination.
     *
     * @param userId the ID of the user
     * @param quizId the ID of the quiz
     * @return ResponseEntity containing a list of quiz attempts for the user and quiz
     */
    @GetMapping("/user/{userId}/quiz/{quizId}")
    public ResponseEntity<List<QuizAttemptOutDTO>> getQuizAttemptsByUserAndQuiz(
            @PathVariable final Long userId,
            @PathVariable final Long quizId) {
        log.info("REST request to get QuizAttempts for user: {} and quiz: {}", userId, quizId);

        final List<QuizAttemptOutDTO> quizAttempts = quizAttemptService.getQuizAttemptsByUserAndQuiz(userId, quizId);
        return ResponseEntity.ok(quizAttempts);
    }

    /**
     * Retrieves all quiz attempts with a specific status.
     *
     * @param status the status of quiz attempts to retrieve
     * @return ResponseEntity containing a list of quiz attempts with the specified status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<QuizAttemptOutDTO>> getQuizAttemptsByStatus(@PathVariable final String status) {
        log.info("REST request to get QuizAttempts with status: {}", status);

        final List<QuizAttemptOutDTO> quizAttempts = quizAttemptService.getQuizAttemptsByStatus(status);
        return ResponseEntity.ok(quizAttempts);
    }

    /**
     * Retrieves the latest quiz attempt for a specific user and quiz combination.
     *
     * @param userId the ID of the user
     * @param quizId the ID of the quiz
     * @return ResponseEntity containing the latest quiz attempt or 404 if not found
     */
    @GetMapping("/user/{userId}/quiz/{quizId}/latest")
    public ResponseEntity<QuizAttemptOutDTO> getLatestAttemptByUserAndQuiz(
            @PathVariable final Long userId,
            @PathVariable final Long quizId) {
        log.info("REST request to get latest QuizAttempt for user: {} and quiz: {}", userId, quizId);

        final Optional<QuizAttemptOutDTO> latestAttempt = quizAttemptService.getLatestAttemptByUserAndQuiz(userId, quizId);
        return latestAttempt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Deletes a quiz attempt by its ID.
     *
     * @param quizAttemptId the ID of the quiz attempt to delete
     * @return ResponseEntity with no content status
     */
    @DeleteMapping("/{quizAttemptId}")
    public ResponseEntity<Void> deleteQuizAttempt(@PathVariable final Long quizAttemptId) {
        log.info("REST request to delete QuizAttempt with ID: {}", quizAttemptId);

        quizAttemptService.deleteQuizAttempt(quizAttemptId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Marks a quiz attempt as completed.
     *
     * @param quizAttemptId the ID of the quiz attempt to complete
     * @param scoreDetails  optional score details for the completed attempt
     * @return ResponseEntity containing the completed quiz attempt data
     */
    @PatchMapping("/{quizAttemptId}/complete")
    public ResponseEntity<QuizAttemptOutDTO> completeAttempt(
            @PathVariable final Long quizAttemptId,
            @RequestBody(required = false) final String scoreDetails) {
        log.info("REST request to complete QuizAttempt with ID: {}", quizAttemptId);

        final QuizAttemptOutDTO completedAttempt = quizAttemptService.completeAttempt(quizAttemptId, scoreDetails);
        return ResponseEntity.ok(completedAttempt);
    }

    /**
     * Marks a quiz attempt as abandoned.
     *
     * @param quizAttemptId the ID of the quiz attempt to abandon
     * @return ResponseEntity containing the abandoned quiz attempt data
     */
    @PatchMapping("/{quizAttemptId}/abandon")
    public ResponseEntity<QuizAttemptOutDTO> abandonAttempt(@PathVariable final Long quizAttemptId) {
        log.info("REST request to abandon QuizAttempt with ID: {}", quizAttemptId);

        final QuizAttemptOutDTO abandonedAttempt = quizAttemptService.abandonAttempt(quizAttemptId);
        return ResponseEntity.ok(abandonedAttempt);
    }

    /**
     * Marks a quiz attempt as timed out.
     *
     * @param quizAttemptId the ID of the quiz attempt to time out
     * @return ResponseEntity containing the timed out quiz attempt data
     */
    @PatchMapping("/{quizAttemptId}/timeout")
    public ResponseEntity<QuizAttemptOutDTO> timeOutAttempt(@PathVariable final Long quizAttemptId) {
        log.info("REST request to time out QuizAttempt with ID: {}", quizAttemptId);

        final QuizAttemptOutDTO timedOutAttempt = quizAttemptService.timeOutAttempt(quizAttemptId);
        return ResponseEntity.ok(timedOutAttempt);
    }

    /**
     * Checks if a quiz attempt exists by its ID.
     *
     * @param quizAttemptId the ID of the quiz attempt to check
     * @return ResponseEntity with 200 status if exists, 404 if not found
     */
    @RequestMapping(value = "/{quizAttemptId}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> checkQuizAttemptExists(@PathVariable final Long quizAttemptId) {
        log.info("REST request to check if QuizAttempt exists with ID: {}", quizAttemptId);

        final boolean exists = quizAttemptService.existsById(quizAttemptId);
        return exists ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    /**
     * Counts the number of quiz attempts for a specific user and quiz combination.
     *
     * @param userId the ID of the user
     * @param quizId the ID of the quiz
     * @return ResponseEntity containing the count of quiz attempts
     */
    @GetMapping("/user/{userId}/quiz/{quizId}/count")
    public ResponseEntity<Long> countAttemptsByUserAndQuiz(
            @PathVariable final Long userId,
            @PathVariable final Long quizId) {
        log.info("REST request to count QuizAttempts for user: {} and quiz: {}", userId, quizId);

        final long count = quizAttemptService.countAttemptsByUserAndQuiz(userId, quizId);
        return ResponseEntity.ok(count);
    }

    /**
     * Health check endpoint to verify the controller is operational.
     *
     * @return ResponseEntity with a health status message
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("QuizAttempt Controller is healthy");
    }
}
