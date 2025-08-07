package com.nt.course_service_lms.controller;

import com.nt.course_service_lms.dto.inDTO.QuizSubmissionInDTO;
import com.nt.course_service_lms.dto.outDTO.QuizSubmissionResultOutDTO;
import com.nt.course_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.course_service_lms.service.serviceImpl.QuizSubmissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling quiz submission operations in the Learning Management System.
 * Provides endpoints for manual quiz submissions, automatic timeout submissions, and generic quiz submissions.
 */
@RestController
@RequestMapping("/api/service-api/quiz-submissions")
@RequiredArgsConstructor
@Slf4j
public class QuizSubmissionController {

    /**
     * Service layer component responsible for handling quiz submission business logic.
     * Injected via constructor using Lombok's @RequiredArgsConstructor annotation.
     */
    @Autowired
    private QuizSubmissionService quizSubmissionService;

    /**
     * Handles automatic quiz submission when the quiz timer expires.
     * This endpoint is triggered when a quiz attempt times out and needs to be auto-submitted.
     *
     * @param quizAttemptId The unique identifier of the quiz attempt that timed out
     * @param submissionDTO The data transfer object containing user responses collected up to the timeout
     * @return ResponseEntity containing the quiz submission result wrapped in a standard response format
     */
    @PostMapping("/timeout/{quizAttemptId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<StandardResponseOutDTO<QuizSubmissionResultOutDTO>> submitQuizOnTimeout(
            @PathVariable final Long quizAttemptId,
            @RequestBody final QuizSubmissionInDTO submissionDTO) {

        log.info("Auto-timeout quiz submission request for attempt: {}", quizAttemptId);

        QuizSubmissionResultOutDTO result = quizSubmissionService.submitQuizOnTimeout(
                quizAttemptId, submissionDTO.getUserResponses());

        StandardResponseOutDTO<QuizSubmissionResultOutDTO> response =
                StandardResponseOutDTO.success(result, "Quiz submitted automatically due to timeout");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Generic quiz submission endpoint that can handle different submission types.
     * This is a flexible endpoint that can process both manual and automatic submissions
     * based on the submission type parameter.
     *
     * @param quizAttemptId  The unique identifier of the quiz attempt being submitted
     * @param submissionDTO  The data transfer object containing user responses and submission details
     * @param submissionType The type of submission (defaults to "MANUAL" if not specified)
     * @return ResponseEntity containing the quiz submission result wrapped in a standard response format
     * @throws jakarta.validation.ConstraintViolationException if the submission data is invalid
     */
    @PostMapping("/{quizAttemptId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<StandardResponseOutDTO<QuizSubmissionResultOutDTO>> submitQuiz(
            @PathVariable final Long quizAttemptId,
            @Valid @RequestBody final QuizSubmissionInDTO submissionDTO,
            @RequestParam(defaultValue = "MANUAL") final String submissionType) {

        log.info("Quiz submission request for attempt: {}, type: {}", quizAttemptId, submissionType);

        QuizSubmissionResultOutDTO result = quizSubmissionService.submitQuiz(
                quizAttemptId, submissionDTO.getUserResponses(), submissionType);

        StandardResponseOutDTO<QuizSubmissionResultOutDTO> response =
                StandardResponseOutDTO.success(result, "Quiz submitted successfully");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
