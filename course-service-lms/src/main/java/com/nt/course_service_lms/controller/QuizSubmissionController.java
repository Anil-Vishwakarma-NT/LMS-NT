package com.nt.course_service_lms.controller;

import com.nt.course_service_lms.dto.inDTO.QuizSubmissionInDTO;
import com.nt.course_service_lms.dto.outDTO.QuizSubmissionResultOutDTO;
import com.nt.course_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.course_service_lms.service.serviceImpl.QuizSubmissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Controller for handling quiz submissions
 */
@RestController
@RequestMapping("/api/service-api/quiz-submissions")
@RequiredArgsConstructor
@Slf4j
public class QuizSubmissionController {

    private final QuizSubmissionService quizSubmissionService;

    /**
     * Submit quiz manually by user
     */
    @PostMapping("/manual/{quizAttemptId}")
    public ResponseEntity<StandardResponseOutDTO<QuizSubmissionResultOutDTO>> submitQuizManually(
            @PathVariable Long quizAttemptId,
            @Valid @RequestBody QuizSubmissionInDTO submissionDTO) {

        log.info("Manual quiz submission request for attempt: {}", quizAttemptId);

        QuizSubmissionResultOutDTO result = quizSubmissionService.submitQuizManually(
                quizAttemptId, submissionDTO.getUserResponses());

        StandardResponseOutDTO<QuizSubmissionResultOutDTO> response =
                StandardResponseOutDTO.success(result, "Quiz submitted manually successfully");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Submit quiz automatically due to timeout
     */
    @PostMapping("/timeout/{quizAttemptId}")
    public ResponseEntity<StandardResponseOutDTO<QuizSubmissionResultOutDTO>> submitQuizOnTimeout(
            @PathVariable Long quizAttemptId,
            @RequestBody QuizSubmissionInDTO submissionDTO) {

        log.info("Auto-timeout quiz submission request for attempt: {}", quizAttemptId);

        QuizSubmissionResultOutDTO result = quizSubmissionService.submitQuizOnTimeout(
                quizAttemptId, submissionDTO.getUserResponses());

        StandardResponseOutDTO<QuizSubmissionResultOutDTO> response =
                StandardResponseOutDTO.success(result, "Quiz submitted automatically due to timeout");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Generic submit quiz endpoint
     */
    @PostMapping("/{quizAttemptId}")
    public ResponseEntity<StandardResponseOutDTO<QuizSubmissionResultOutDTO>> submitQuiz(
            @PathVariable Long quizAttemptId,
            @Valid @RequestBody QuizSubmissionInDTO submissionDTO,
            @RequestParam(defaultValue = "MANUAL") String submissionType) {

        log.info("Quiz submission request for attempt: {}, type: {}", quizAttemptId, submissionType);

        QuizSubmissionResultOutDTO result = quizSubmissionService.submitQuiz(
                quizAttemptId, submissionDTO.getUserResponses(), submissionType);

        StandardResponseOutDTO<QuizSubmissionResultOutDTO> response =
                StandardResponseOutDTO.success(result, "Quiz submitted successfully");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}