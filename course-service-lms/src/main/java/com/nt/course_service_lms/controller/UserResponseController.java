package com.nt.course_service_lms.controller;

import com.nt.course_service_lms.dto.inDTO.UserResponseInDTO;
import com.nt.course_service_lms.dto.inDTO.UserResponseUpdateInDTO;
import com.nt.course_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.course_service_lms.dto.outDTO.UserResponseOutDTO;
import com.nt.course_service_lms.service.UserResponseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import java.math.BigDecimal;
import java.util.List;

/**
 * REST Controller for managing user responses to quiz questions in the Learning Management System.
 * Provides comprehensive CRUD operations and various query capabilities for user quiz responses.
 *
 * @author NT Course Service Team
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/service-api/v1/user-responses")
@RequiredArgsConstructor
@Slf4j
public class UserResponseController {

    /**
     * Service layer dependency for handling user response business logic operations.
     */
    private final UserResponseService userResponseService;

    /**
     * Creates new user responses for multiple quiz questions in a single request.
     * Validates input data and ensures the response list is not empty before processing.
     *
     * @param userResponseInDTOList List of user response input DTOs containing quiz answers
     * @return ResponseEntity containing the created user responses with HTTP 201 status
     * @throws IllegalArgumentException if the user response list is empty
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
     * Retrieves a specific user response by its unique identifier.
     *
     * @param responseId The unique identifier of the user response to retrieve
     * @return ResponseEntity containing the user response data with HTTP 200 status
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
     * Updates an existing user response with new information.
     * Only allows modification of user's selected answer and related fields.
     *
     * @param responseId The unique identifier of the user response to update
     * @param userResponseUpdateInDTO DTO containing the updated user response data
     * @return ResponseEntity containing the updated user response with HTTP 200 status
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
     * Deletes a user response from the system permanently.
     * This operation cannot be undone and will remove all associated data.
     *
     * @param responseId The unique identifier of the user response to delete
     * @return ResponseEntity with HTTP 200 status confirming successful deletion
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
     * Retrieves all user responses in the system with pagination support.
     * Supports sorting and filtering through Spring Data pagination parameters.
     *
     * @param pageable Pagination and sorting parameters (page number, size, sort criteria)
     * @return ResponseEntity containing a paginated list of user responses
     */
    @GetMapping
    public ResponseEntity<StandardResponseOutDTO<Page<UserResponseOutDTO>>> getAllUserResponses(
            final Pageable pageable) {
        log.info("Received request to get all user responses with pagination - page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<UserResponseOutDTO> userResponses = userResponseService.getAllUserResponses(pageable);

        log.info("Retrieved {} user responses successfully", userResponses.getTotalElements());
        return ResponseEntity.ok(StandardResponseOutDTO.success(userResponses, "User responses retrieved successfully"));
    }

    /**
     * Retrieves all user responses for a specific user as a complete list.
     * Returns all quiz responses submitted by the specified user across all quizzes.
     *
     * @param userId The unique identifier of the user whose responses to retrieve
     * @return ResponseEntity containing a list of user responses for the specified user
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
     * Retrieves user responses for a specific user with pagination support.
     * Provides paginated access to all quiz responses submitted by the specified user.
     *
     * @param userId The unique identifier of the user whose responses to retrieve
     * @param pageable Pagination and sorting parameters
     * @return ResponseEntity containing a paginated list of user responses
     */
    @GetMapping("/user/{userId}/paginated")
    public ResponseEntity<StandardResponseOutDTO<Page<UserResponseOutDTO>>> getUserResponsesByUserIdPaginated(
            @PathVariable final Long userId,
            final Pageable pageable) {
        log.info("Received request to get user responses for user ID: {} with pagination", userId);

        Page<UserResponseOutDTO> userResponses = userResponseService.getUserResponsesByUserId(userId, pageable);

        log.info("Retrieved {} user responses for user ID: {}", userResponses.getTotalElements(), userId);
        return ResponseEntity.ok(StandardResponseOutDTO.success(userResponses, "User responses retrieved successfully"));
    }

    /**
     * Retrieves all user responses for a specific quiz as a complete list.
     * Returns all responses submitted by various users for the specified quiz.
     *
     * @param quizId The unique identifier of the quiz whose responses to retrieve
     * @return ResponseEntity containing a list of user responses for the specified quiz
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
     * Retrieves user responses for a specific quiz with pagination support.
     * Provides paginated access to all responses submitted for the specified quiz.
     *
     * @param quizId The unique identifier of the quiz whose responses to retrieve
     * @param pageable Pagination and sorting parameters
     * @return ResponseEntity containing a paginated list of user responses for the quiz
     */
    @GetMapping("/quiz/{quizId}/paginated")
    public ResponseEntity<StandardResponseOutDTO<Page<UserResponseOutDTO>>> getUserResponsesByQuizIdPaginated(
            @PathVariable final Long quizId,
            final Pageable pageable) {
        log.info("Received request to get user responses for quiz ID: {} with pagination", quizId);

        Page<UserResponseOutDTO> userResponses = userResponseService.getUserResponsesByQuizId(quizId, pageable);

        log.info("Retrieved {} user responses for quiz ID: {}", userResponses.getTotalElements(), quizId);
        return ResponseEntity.ok(StandardResponseOutDTO.success(userResponses, "User responses retrieved successfully"));
    }

    /**
     * Retrieves user responses for a specific user and quiz combination.
     * Returns all responses submitted by the specified user for the specified quiz across all attempts.
     *
     * @param userId The unique identifier of the user
     * @param quizId The unique identifier of the quiz
     * @return ResponseEntity containing a list of user responses for the user-quiz combination
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
     * Retrieves user responses for a specific user, quiz, and attempt combination.
     * Returns responses submitted by the specified user for a particular quiz attempt.
     *
     * @param userId The unique identifier of the user
     * @param quizId The unique identifier of the quiz
     * @param attempt The attempt number for the quiz (1-based indexing)
     * @return ResponseEntity containing a list of user responses for the specific attempt
     */
    @GetMapping("/user/{userId}/quiz/{quizId}/attempt/{attempt}")
    public ResponseEntity<StandardResponseOutDTO<List<UserResponseOutDTO>>> getUserResponsesByUserIdQuizIdAndAttempt(
            @PathVariable final Long userId,
            @PathVariable final Long quizId,
            @PathVariable final Long attempt) {
        log.info("Received request to get user responses for user ID: {}, quiz ID: {}, attempt: {}", userId, quizId, attempt);

        List<UserResponseOutDTO> userResponses = userResponseService.getUserResponsesByUserIdAndQuizIdAndAttempt(
                userId, quizId, attempt
        );

        log.info("Retrieved {} user responses for user ID: {}, quiz ID: {}, attempt: {}",
                userResponses.size(), userId, quizId, attempt);
        return ResponseEntity.ok(StandardResponseOutDTO.success(userResponses, "User responses retrieved successfully"));
    }

    /**
     * Calculates and retrieves the total score achieved by a user in a specific quiz attempt.
     * Aggregates scores from all questions answered in the specified attempt.
     *
     * @param userId The unique identifier of the user
     * @param quizId The unique identifier of the quiz
     * @param attempt The attempt number for the quiz (1-based indexing)
     * @return ResponseEntity containing the total score as a BigDecimal value
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
     * Counts the number of correct answers provided by a user in a specific quiz attempt.
     * Evaluates all responses for the specified attempt and returns the count of correct answers.
     *
     * @param userId The unique identifier of the user
     * @param quizId The unique identifier of the quiz
     * @param attempt The attempt number for the quiz (1-based indexing)
     * @return ResponseEntity containing the count of correct answers as a Long value
     */
    @GetMapping("/user/{userId}/quiz/{quizId}/attempt/{attempt}/correct-count")
    public ResponseEntity<StandardResponseOutDTO<Long>> countCorrectAnswers(
            @PathVariable final Long userId,
            @PathVariable final Long quizId,
            @PathVariable final Long attempt) {
        log.info("Received request to count correct answers for user ID: {}, quiz ID: {}, attempt: {}",
                userId, quizId, attempt);

        Long correctCount = userResponseService.countCorrectAnswers(userId, quizId, attempt);

        log.info("Retrieved correct answers count: {} for user ID: {}, quiz ID: {}, attempt: {}",
                correctCount, userId, quizId, attempt);
        return ResponseEntity.ok(StandardResponseOutDTO.success(correctCount, "Correct answers count retrieved successfully"));
    }

    /**
     * Retrieves the maximum attempt number for a specific user and quiz combination.
     * Determines the highest attempt number the user has made for the specified quiz.
     *
     * @param userId The unique identifier of the user
     * @param quizId The unique identifier of the quiz
     * @return ResponseEntity containing the maximum attempt number as a Long value
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
