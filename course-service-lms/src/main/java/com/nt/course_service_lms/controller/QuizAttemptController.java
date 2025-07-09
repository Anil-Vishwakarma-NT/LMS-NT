package com.nt.course_service_lms.controller;


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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/service-api/quiz-attempt")
@Slf4j
public class QuizAttemptController {

    @Autowired
    private QuizAttemptService quizAttemptService;

    /**
     * Create a new quiz attempt
     */
    @PostMapping
    public ResponseEntity<QuizAttemptOutDTO> createQuizAttempt(@Valid @RequestBody QuizAttemptCreateInDTO dto) {
        log.info("REST request to create QuizAttempt for user: {} and quiz: {}", dto.getUserId(), dto.getQuizId());

        QuizAttemptOutDTO createdAttempt = quizAttemptService.createQuizAttempt(dto);
        return new ResponseEntity<>(createdAttempt, HttpStatus.OK);
    }

    /**
     * Update an existing quiz attempt
     */
    @PutMapping("/{quizAttemptId}")
    public ResponseEntity<QuizAttemptOutDTO> updateQuizAttempt(
            @PathVariable Long quizAttemptId,
            @Valid @RequestBody QuizAttemptUpdateInDTO dto) {
        log.info("REST request to update QuizAttempt with ID: {}", quizAttemptId);

        QuizAttemptOutDTO updatedAttempt = quizAttemptService.updateQuizAttempt(quizAttemptId, dto);
        return ResponseEntity.ok(updatedAttempt);
    }

    /**
     * Get quiz attempt by ID
     */
    @GetMapping("/{quizAttemptId}")
    public ResponseEntity<QuizAttemptOutDTO> getQuizAttemptById(@PathVariable Long quizAttemptId) {
        log.info("REST request to get QuizAttempt with ID: {}", quizAttemptId);

        Optional<QuizAttemptOutDTO> quizAttempt = quizAttemptService.getQuizAttemptById(quizAttemptId);
        return quizAttempt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all quiz attempts with pagination
     */
    @GetMapping
    public ResponseEntity<Page<QuizAttemptOutDTO>> getAllQuizAttempts(
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        log.info("REST request to get all QuizAttempts with pagination");

        Page<QuizAttemptOutDTO> quizAttempts = quizAttemptService.getAllQuizAttempts(pageable);
        return ResponseEntity.ok(quizAttempts);
    }

    /**
     * Get quiz attempts by user ID
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<QuizAttemptOutDTO>> getQuizAttemptsByUserId(@PathVariable Long userId) {
        log.info("REST request to get QuizAttempts for user: {}", userId);

        List<QuizAttemptOutDTO> quizAttempts = quizAttemptService.getQuizAttemptsByUserId(userId);
        return ResponseEntity.ok(quizAttempts);
    }

    /**
     * Get quiz attempts by quiz ID
     * GET /api/v1/quiz-attempts/quiz/{quizId}
     */
    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<QuizAttemptOutDTO>> getQuizAttemptsByQuizId(@PathVariable Long quizId) {
        log.info("REST request to get QuizAttempts for quiz: {}", quizId);

        List<QuizAttemptOutDTO> quizAttempts = quizAttemptService.getQuizAttemptsByQuizId(quizId);
        return ResponseEntity.ok(quizAttempts);
    }

    /**
     * Get quiz attempts by user and quiz
     * GET /api/v1/quiz-attempts/user/{userId}/quiz/{quizId}
     */
    @GetMapping("/user/{userId}/quiz/{quizId}")
    public ResponseEntity<List<QuizAttemptOutDTO>> getQuizAttemptsByUserAndQuiz(
            @PathVariable Long userId,
            @PathVariable Long quizId) {
        log.info("REST request to get QuizAttempts for user: {} and quiz: {}", userId, quizId);

        List<QuizAttemptOutDTO> quizAttempts = quizAttemptService.getQuizAttemptsByUserAndQuiz(userId, quizId);
        return ResponseEntity.ok(quizAttempts);
    }

    /**
     * Get quiz attempts by status
     * GET /api/v1/quiz-attempts/status/{status}
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<QuizAttemptOutDTO>> getQuizAttemptsByStatus(@PathVariable String status) {
        log.info("REST request to get QuizAttempts with status: {}", status);

        List<QuizAttemptOutDTO> quizAttempts = quizAttemptService.getQuizAttemptsByStatus(status);
        return ResponseEntity.ok(quizAttempts);
    }

    /**
     * Get latest attempt by user and quiz
     * GET /api/v1/quiz-attempts/user/{userId}/quiz/{quizId}/latest
     */
    @GetMapping("/user/{userId}/quiz/{quizId}/latest")
    public ResponseEntity<QuizAttemptOutDTO> getLatestAttemptByUserAndQuiz(
            @PathVariable Long userId,
            @PathVariable Long quizId) {
        log.info("REST request to get latest QuizAttempt for user: {} and quiz: {}", userId, quizId);

        Optional<QuizAttemptOutDTO> latestAttempt = quizAttemptService.getLatestAttemptByUserAndQuiz(userId, quizId);
        return latestAttempt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete quiz attempt
     * DELETE /api/v1/quiz-attempts/{quizAttemptId}
     */
    @DeleteMapping("/{quizAttemptId}")
    public ResponseEntity<Void> deleteQuizAttempt(@PathVariable Long quizAttemptId) {
        log.info("REST request to delete QuizAttempt with ID: {}", quizAttemptId);

        quizAttemptService.deleteQuizAttempt(quizAttemptId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Complete a quiz attempt
     * PATCH /api/v1/quiz-attempts/{quizAttemptId}/complete
     */
    @PatchMapping("/{quizAttemptId}/complete")
    public ResponseEntity<QuizAttemptOutDTO> completeAttempt(
            @PathVariable Long quizAttemptId,
            @RequestBody(required = false) String scoreDetails) {
        log.info("REST request to complete QuizAttempt with ID: {}", quizAttemptId);

        QuizAttemptOutDTO completedAttempt = quizAttemptService.completeAttempt(quizAttemptId, scoreDetails);
        return ResponseEntity.ok(completedAttempt);
    }

    /**
     * Abandon a quiz attempt
     * PATCH /api/v1/quiz-attempts/{quizAttemptId}/abandon
     */
    @PatchMapping("/{quizAttemptId}/abandon")
    public ResponseEntity<QuizAttemptOutDTO> abandonAttempt(@PathVariable Long quizAttemptId) {
        log.info("REST request to abandon QuizAttempt with ID: {}", quizAttemptId);

        QuizAttemptOutDTO abandonedAttempt = quizAttemptService.abandonAttempt(quizAttemptId);
        return ResponseEntity.ok(abandonedAttempt);
    }

    /**
     * Time out a quiz attempt
     * PATCH /api/v1/quiz-attempts/{quizAttemptId}/timeout
     */
    @PatchMapping("/{quizAttemptId}/timeout")
    public ResponseEntity<QuizAttemptOutDTO> timeOutAttempt(@PathVariable Long quizAttemptId) {
        log.info("REST request to time out QuizAttempt with ID: {}", quizAttemptId);

        QuizAttemptOutDTO timedOutAttempt = quizAttemptService.timeOutAttempt(quizAttemptId);
        return ResponseEntity.ok(timedOutAttempt);
    }

    /**
     * Check if quiz attempt exists
     * HEAD /api/v1/quiz-attempts/{quizAttemptId}
     */
    @RequestMapping(value = "/{quizAttemptId}", method = RequestMethod.HEAD)
    public ResponseEntity<Void> checkQuizAttemptExists(@PathVariable Long quizAttemptId) {
        log.info("REST request to check if QuizAttempt exists with ID: {}", quizAttemptId);

        boolean exists = quizAttemptService.existsById(quizAttemptId);
        return exists ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    /**
     * Count attempts by user and quiz
     * GET /api/v1/quiz-attempts/user/{userId}/quiz/{quizId}/count
     */
    @GetMapping("/user/{userId}/quiz/{quizId}/count")
    public ResponseEntity<Long> countAttemptsByUserAndQuiz(
            @PathVariable Long userId,
            @PathVariable Long quizId) {
        log.info("REST request to count QuizAttempts for user: {} and quiz: {}", userId, quizId);

        long count = quizAttemptService.countAttemptsByUserAndQuiz(userId, quizId);
        return ResponseEntity.ok(count);
    }

    /**
     * Health check endpoint
     * GET /api/v1/quiz-attempts/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("QuizAttempt Controller is healthy");
    }
}
