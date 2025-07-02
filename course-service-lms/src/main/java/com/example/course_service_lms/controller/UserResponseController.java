package com.example.course_service_lms.controller;

import com.example.course_service_lms.dto.inDTO.UserResponseInDTO;
import com.example.course_service_lms.dto.inDTO.UserResponseUpdateInDTO;
import com.example.course_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.example.course_service_lms.dto.outDTO.UserResponseOutDTO;
import com.example.course_service_lms.service.UserResponseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST Controller for managing user responses to quiz questions.
 * Provides endpoints for CRUD operations and various query operations.
 */
@RestController
@RequestMapping("/api/service-api/v1/user-responses")
@RequiredArgsConstructor
@Slf4j
public class UserResponseController {

    private final UserResponseService userResponseService;

    /**
     * Create a new user response
     */
    @PostMapping
    public ResponseEntity<StandardResponseOutDTO<List<UserResponseOutDTO>>> createUserResponse(
            @Valid @RequestBody final List<UserResponseInDTO> userResponseInDTOList) {
        log.info("Received request to create user responses for {} questions", userResponseInDTOList.size());

        if (userResponseInDTOList.isEmpty()) {
            log.warn("Empty user response list provided");
            return ResponseEntity.badRequest()
                    .body(StandardResponseOutDTO.error("User response list cannot be empty"));
        }

        UserResponseInDTO firstResponse = userResponseInDTOList.get(0);
        log.info("Processing responses for user ID: {}, quiz ID: {}",
                firstResponse.getUserId(), firstResponse.getQuizId());

        List<UserResponseOutDTO> createdUserResponses = userResponseService.createUserResponse(userResponseInDTOList);

        log.info("User responses created successfully. Total responses: {}", createdUserResponses.size());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponseOutDTO.success(createdUserResponses,
                        String.format("Successfully created %d user responses", createdUserResponses.size())));
    }

    /**
     * Get user response by ID
     */
    @GetMapping("/{responseId}")
    public ResponseEntity<StandardResponseOutDTO<UserResponseOutDTO>> getUserResponseById(
            @PathVariable final Long responseId) {
        log.info("Received request to get user response with ID: {}", responseId);

        UserResponseOutDTO userResponse = userResponseService.getUserResponseById(responseId);

        log.info("User response retrieved successfully with ID: {}", responseId);
        return ResponseEntity.ok(StandardResponseOutDTO.success(userResponse, "User response retrieved successfully"));
    }

    /**
     * Update user response
     */
    @PutMapping("/{responseId}")
    public ResponseEntity<StandardResponseOutDTO<UserResponseOutDTO>> updateUserResponse(
            @PathVariable final Long responseId,
            @Valid @RequestBody final UserResponseUpdateInDTO userResponseUpdateInDTO) {
        log.info("Received request to update user response with ID: {}", responseId);

        UserResponseOutDTO updatedUserResponse = userResponseService.updateUserResponse(responseId, userResponseUpdateInDTO);

        log.info("User response updated successfully with ID: {}", responseId);
        return ResponseEntity.ok(StandardResponseOutDTO.success(updatedUserResponse, "User response updated successfully"));
    }

    /**
     * Delete user response
     */
    @DeleteMapping("/{responseId}")
    public ResponseEntity<StandardResponseOutDTO<Void>> deleteUserResponse(
            @PathVariable final Long responseId) {
        log.info("Received request to delete user response with ID: {}", responseId);

        userResponseService.deleteUserResponse(responseId);

        log.info("User response deleted successfully with ID: {}", responseId);
        return ResponseEntity.ok(StandardResponseOutDTO.success(null, "User response deleted successfully"));
    }

    /**
     * Get all user responses with pagination
     */
    @GetMapping
    public ResponseEntity<StandardResponseOutDTO<Page<UserResponseOutDTO>>> getAllUserResponses(
            Pageable pageable) {
        log.info("Received request to get all user responses with pagination - page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<UserResponseOutDTO> userResponses = userResponseService.getAllUserResponses(pageable);

        log.info("Retrieved {} user responses successfully", userResponses.getTotalElements());
        return ResponseEntity.ok(StandardResponseOutDTO.success(userResponses, "User responses retrieved successfully"));
    }

    /**
     * Get user responses by user ID (List version)
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<StandardResponseOutDTO<List<UserResponseOutDTO>>> getUserResponsesByUserId(
            @PathVariable final Long userId) {
        log.info("Received request to get user responses for user ID: {}", userId);

        List<UserResponseOutDTO> userResponses = userResponseService.getUserResponsesByUserId(userId);

        log.info("Retrieved {} user responses for user ID: {}", userResponses.size(), userId);
        return ResponseEntity.ok(StandardResponseOutDTO.success(userResponses, "User responses retrieved successfully"));
    }

    /**
     * Get user responses by user ID with pagination
     */
    @GetMapping("/user/{userId}/paginated")
    public ResponseEntity<StandardResponseOutDTO<Page<UserResponseOutDTO>>> getUserResponsesByUserIdPaginated(
            @PathVariable final Long userId,
            Pageable pageable) {
        log.info("Received request to get user responses for user ID: {} with pagination", userId);

        Page<UserResponseOutDTO> userResponses = userResponseService.getUserResponsesByUserId(userId, pageable);

        log.info("Retrieved {} user responses for user ID: {}", userResponses.getTotalElements(), userId);
        return ResponseEntity.ok(StandardResponseOutDTO.success(userResponses, "User responses retrieved successfully"));
    }

    /**
     * Get user responses by quiz ID (List version)
     */
    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<StandardResponseOutDTO<List<UserResponseOutDTO>>> getUserResponsesByQuizId(
            @PathVariable final Long quizId) {
        log.info("Received request to get user responses for quiz ID: {}", quizId);

        List<UserResponseOutDTO> userResponses = userResponseService.getUserResponsesByQuizId(quizId);

        log.info("Retrieved {} user responses for quiz ID: {}", userResponses.size(), quizId);
        return ResponseEntity.ok(StandardResponseOutDTO.success(userResponses, "User responses retrieved successfully"));
    }

    /**
     * Get user responses by quiz ID with pagination
     */
    @GetMapping("/quiz/{quizId}/paginated")
    public ResponseEntity<StandardResponseOutDTO<Page<UserResponseOutDTO>>> getUserResponsesByQuizIdPaginated(
            @PathVariable final Long quizId,
            Pageable pageable) {
        log.info("Received request to get user responses for quiz ID: {} with pagination", quizId);

        Page<UserResponseOutDTO> userResponses = userResponseService.getUserResponsesByQuizId(quizId, pageable);

        log.info("Retrieved {} user responses for quiz ID: {}", userResponses.getTotalElements(), quizId);
        return ResponseEntity.ok(StandardResponseOutDTO.success(userResponses, "User responses retrieved successfully"));
    }

    /**
     * Get user responses by user ID and quiz ID
     */
    @GetMapping("/user/{userId}/quiz/{quizId}")
    public ResponseEntity<StandardResponseOutDTO<List<UserResponseOutDTO>>> getUserResponsesByUserIdAndQuizId(
            @PathVariable final Long userId,
            @PathVariable final Long quizId) {
        log.info("Received request to get user responses for user ID: {} and quiz ID: {}", userId, quizId);

        List<UserResponseOutDTO> userResponses = userResponseService.getUserResponsesByUserIdAndQuizId(userId, quizId);

        log.info("Retrieved {} user responses for user ID: {} and quiz ID: {}", userResponses.size(), userId, quizId);
        return ResponseEntity.ok(StandardResponseOutDTO.success(userResponses, "User responses retrieved successfully"));
    }

    /**
     * Get user responses by user ID, quiz ID, and attempt
     */
    @GetMapping("/user/{userId}/quiz/{quizId}/attempt/{attempt}")
    public ResponseEntity<StandardResponseOutDTO<List<UserResponseOutDTO>>> getUserResponsesByUserIdQuizIdAndAttempt(
            @PathVariable final Long userId,
            @PathVariable final Long quizId,
            @PathVariable final Long attempt) {
        log.info("Received request to get user responses for user ID: {}, quiz ID: {}, attempt: {}", userId, quizId, attempt);

        List<UserResponseOutDTO> userResponses = userResponseService.getUserResponsesByUserIdAndQuizIdAndAttempt(userId, quizId, attempt);

        log.info("Retrieved {} user responses for user ID: {}, quiz ID: {}, attempt: {}", userResponses.size(), userId, quizId, attempt);
        return ResponseEntity.ok(StandardResponseOutDTO.success(userResponses, "User responses retrieved successfully"));
    }

    /**
     * Get total score for a user in a specific quiz attempt
     */
    @GetMapping("/user/{userId}/quiz/{quizId}/attempt/{attempt}/total-score")
    public ResponseEntity<StandardResponseOutDTO<BigDecimal>> getTotalScore(
            @PathVariable final Long userId,
            @PathVariable final Long quizId,
            @PathVariable final Long attempt) {
        log.info("Received request to get total score for user ID: {}, quiz ID: {}, attempt: {}", userId, quizId, attempt);

        BigDecimal totalScore = userResponseService.getTotalScore(userId, quizId, attempt);

        log.info("Retrieved total score: {} for user ID: {}, quiz ID: {}, attempt: {}", totalScore, userId, quizId, attempt);
        return ResponseEntity.ok(StandardResponseOutDTO.success(totalScore, "Total score retrieved successfully"));
    }

    /**
     * Count correct answers for a user in a specific quiz attempt
     */
    @GetMapping("/user/{userId}/quiz/{quizId}/attempt/{attempt}/correct-count")
    public ResponseEntity<StandardResponseOutDTO<Long>> countCorrectAnswers(
            @PathVariable final Long userId,
            @PathVariable final Long quizId,
            @PathVariable final Long attempt) {
        log.info("Received request to count correct answers for user ID: {}, quiz ID: {}, attempt: {}", userId, quizId, attempt);

        Long correctCount = userResponseService.countCorrectAnswers(userId, quizId, attempt);

        log.info("Retrieved correct answers count: {} for user ID: {}, quiz ID: {}, attempt: {}", correctCount, userId, quizId, attempt);
        return ResponseEntity.ok(StandardResponseOutDTO.success(correctCount, "Correct answers count retrieved successfully"));
    }

    /**
     * Get maximum attempt number for a user in a specific quiz
     */
    @GetMapping("/user/{userId}/quiz/{quizId}/max-attempt")
    public ResponseEntity<StandardResponseOutDTO<Long>> getMaxAttemptNumber(
            @PathVariable final Long userId,
            @PathVariable final Long quizId) {
        log.info("Received request to get max attempt number for user ID: {} and quiz ID: {}", userId, quizId);

        Long maxAttempt = userResponseService.getMaxAttemptNumber(userId, quizId);

        log.info("Retrieved max attempt number: {} for user ID: {} and quiz ID: {}", maxAttempt, userId, quizId);
        return ResponseEntity.ok(StandardResponseOutDTO.success(maxAttempt, "Maximum attempt number retrieved successfully"));
    }
}