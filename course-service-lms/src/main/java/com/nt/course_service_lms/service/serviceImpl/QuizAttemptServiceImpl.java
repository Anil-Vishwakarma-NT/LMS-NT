package com.nt.course_service_lms.service.serviceImpl;

import com.nt.course_service_lms.dto.inDTO.QuizAttemptCreateInDTO;
import com.nt.course_service_lms.dto.inDTO.QuizAttemptUpdateInDTO;
import com.nt.course_service_lms.dto.outDTO.QuizAttemptOutDTO;
import com.nt.course_service_lms.dto.outDTO.QuizSubmissionResultOutDTO;
import com.nt.course_service_lms.dto.outDTO.UserQuizAttemptDetailsOutDTO;
import com.nt.course_service_lms.dto.outDTO.UserResponseOutDTO;
import com.nt.course_service_lms.dto.outDTO.UserResponseWithCorrectAnswerOutDTO;
import com.nt.course_service_lms.entity.Quiz;
import com.nt.course_service_lms.entity.QuizAttempt;
import com.nt.course_service_lms.entity.QuizQuestion;
import com.nt.course_service_lms.entity.UserResponse;
import com.nt.course_service_lms.exception.ResourceNotFoundException;
import com.nt.course_service_lms.exception.ResourceNotValidException;
import com.nt.course_service_lms.repository.QuizAttemptRepository;
import com.nt.course_service_lms.repository.QuizQuestionRepository;
import com.nt.course_service_lms.repository.QuizRepository;
import com.nt.course_service_lms.repository.UserResponseRepository;
import com.nt.course_service_lms.service.QuizAttemptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service implementation for QuizAttempt operations in the Learning Management System.
 * This class provides comprehensive functionality for managing quiz attempts including
 * creation, updating, retrieval, completion, abandonment, and timeout operations.
 *
 * <p>The service ensures proper validation of quiz attempt states and enforces
 * business rules such as maximum allowed attempts per user per quiz.</p>
 *
 * <p>All operations are transactional to ensure data consistency and integrity.</p>
 *
 * @author Course Service LMS Team
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class QuizAttemptServiceImpl implements QuizAttemptService {

    /**
     * Constant representing the "IN_PROGRESS" status for quiz attempts.
     * This status indicates that a quiz attempt is currently active and ongoing.
     */
    public static final String IN_PROGRESS = "IN_PROGRESS";

    /**
     * Repository for performing CRUD operations on QuizAttempt entities.
     * Provides access to the underlying database for quiz attempt data.
     */
    private final QuizAttemptRepository quizAttemptRepository;

    /**
     * Repository for performing CRUD operations on Quiz entities.
     * Used to validate quiz existence and retrieve quiz configuration.
     */
    private final QuizRepository quizRepository;

    private final UserResponseRepository userResponseRepository;

    private final QuizQuestionRepository quizQuestionRepository;

    /**
     * Creates a new quiz attempt for a user and quiz combination.
     *
     * <p>This method performs the following operations:</p>
     * <ul>
     *   <li>Validates input parameters (userId and quizId)</li>
     *   <li>Checks if the quiz exists</li>
     *   <li>Verifies if user has any active attempts for the quiz</li>
     *   <li>Calculates the next attempt number</li>
     *   <li>Validates against maximum allowed attempts</li>
     *   <li>Creates and saves the new quiz attempt</li>
     * </ul>
     *
     * @param dto the data transfer object containing user ID and quiz ID for creating the attempt
     * @return QuizAttemptOutDTO containing the created attempt details with remaining attempts count
     * @throws ResourceNotValidException if input validation fails or maximum attempts exceeded
     * @throws ResourceNotFoundException if the specified quiz is not found
     */
    @Override
    public QuizAttemptOutDTO createQuizAttempt(final QuizAttemptCreateInDTO dto) {
        log.info("Creating new quiz attempt for user: {} and quiz: {}", dto.getUserId(), dto.getQuizId());

        // Validate input
        if (dto.getUserId() == null || dto.getQuizId() == null) {
            throw new ResourceNotValidException("User ID and Quiz ID are required");
        }

        Quiz quiz = quizRepository.findById(dto.getQuizId()).orElseThrow(
                () -> new ResourceNotFoundException("Quiz Not Found")
        );

        // Check if user has any active attempts for this quiz
        Optional<QuizAttempt> activeAttempt = quizAttemptRepository.findActiveAttemptByUserAndQuiz(
                dto.getUserId(),
                dto.getQuizId()
        );

        if (activeAttempt.isPresent()) {
            log.info("User {} already has an active attempt for quiz {}, returning existing attempt",
                    dto.getUserId(), dto.getQuizId());

            QuizAttempt existingAttempt = activeAttempt.get();
            QuizAttemptOutDTO existingAttemptOutDTO = convertToOutDTO(existingAttempt);
            existingAttemptOutDTO.setAttemptsLeft(quiz.getAttemptsAllowed() - existingAttempt.getAttempt());
            return existingAttemptOutDTO;
        }

        // Get the latest attempt for this user and quiz
        QuizAttempt existingAttempt = quizAttemptRepository.findTopByUserIdAndQuizIdOrderByAttemptDesc(
                dto.getUserId(),
                dto.getQuizId()
        );

        // Calculate the next attempt number
        Long nextAttemptNumber = (existingAttempt != null) ? existingAttempt.getAttempt() + 1 : 1;

        // Check if user has exceeded maximum attempts
        if (nextAttemptNumber > quiz.getAttemptsAllowed()) {
            throw new ResourceNotValidException("User has exceeded maximum allowed attempts for this quiz");
        }

        // Create new attempt
        QuizAttempt quizAttempt = new QuizAttempt();
        quizAttempt.setAttempt(nextAttemptNumber); // Set the calculated attempt number
        quizAttempt.setQuizId(dto.getQuizId());
        quizAttempt.setUserId(dto.getUserId());
        quizAttempt.setStartedAt(LocalDateTime.now());
        quizAttempt.setStatus("IN_PROGRESS");
        quizAttempt.setCreatedAt(LocalDateTime.now());
        quizAttempt.setUpdatedAt(LocalDateTime.now());

        QuizAttempt savedAttempt = quizAttemptRepository.save(quizAttempt);
        log.info("Created quiz attempt with ID: {} (attempt number: {})", savedAttempt.getQuizAttemptId(), nextAttemptNumber);

        QuizAttemptOutDTO quizAttemptOutDTO = convertToOutDTO(savedAttempt);
        quizAttemptOutDTO.setAttemptsLeft(quiz.getAttemptsAllowed() - quizAttempt.getAttempt());
        return quizAttemptOutDTO;
    }

    /**
     * Updates an existing quiz attempt with new information.
     *
     * <p>This method allows updating various fields of a quiz attempt including:</p>
     * <ul>
     *   <li>Finished timestamp</li>
     *   <li>Score details</li>
     *   <li>Status (with validation for valid transitions)</li>
     * </ul>
     *
     * <p>The method automatically sets the finished timestamp when the status
     * is changed to COMPLETED, ABANDONED, or TIMED_OUT.</p>
     *
     * @param quizAttemptId the unique identifier of the quiz attempt to update
     * @param dto           the data transfer object containing the fields to update
     * @return QuizAttemptOutDTO containing the updated attempt details
     * @throws ResourceNotValidException if the quiz attempt ID is null or status transition is invalid
     * @throws ResourceNotFoundException if the quiz attempt with the given ID is not found
     */
    @Override
    public QuizAttemptOutDTO updateQuizAttempt(final Long quizAttemptId, final QuizAttemptUpdateInDTO dto) {
        log.info("Updating quiz attempt with ID: {}", quizAttemptId);

        if (quizAttemptId == null) {
            throw new ResourceNotValidException("Quiz attempt ID cannot be null");
        }

        QuizAttempt existingAttempt = quizAttemptRepository.findById(quizAttemptId)
                .orElseThrow(() -> new ResourceNotFoundException("QuizAttempt not found with ID: " + quizAttemptId));

        // Validate status transition
        if (dto.getStatus() != null && existingAttempt.getStatus() != null) {
            if (isInvalidStatusTransition(existingAttempt.getStatus(), dto.getStatus())) {
                throw new ResourceNotValidException("Invalid status transition from "
                        + existingAttempt.getStatus() + " to " + dto.getStatus());
            }
        }

        // Update fields if provided
        if (dto.getFinishedAt() != null) {
            existingAttempt.setFinishedAt(dto.getFinishedAt());
        }

        if (dto.getScoreDetails() != null) {
            existingAttempt.setScoreDetails(dto.getScoreDetails());
        }

        if (dto.getStatus() != null) {
            existingAttempt.setStatus(dto.getStatus());
            // Auto-set finishedAt if status is set to COMPLETED, ABANDONED, or TIMED_OUT
            if (("COMPLETED".equals(dto.getStatus()) || "ABANDONED".equals(dto.getStatus())
                    || "TIMED_OUT".equals(dto.getStatus())) && existingAttempt.getFinishedAt() == null) {
                existingAttempt.setFinishedAt(LocalDateTime.now());
            }
        }

        existingAttempt.setUpdatedAt(LocalDateTime.now());

        QuizAttempt updatedAttempt = quizAttemptRepository.save(existingAttempt);
        log.info("Updated quiz attempt with ID: {}", quizAttemptId);
        return convertToOutDTO(updatedAttempt);
    }

    /**
     * Retrieves a quiz attempt by its unique identifier.
     *
     * @param quizAttemptId the unique identifier of the quiz attempt to retrieve
     * @return Optional containing QuizAttemptOutDTO if found, empty otherwise
     * @throws ResourceNotValidException if the quiz attempt ID is null
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<QuizAttemptOutDTO> getQuizAttemptById(final Long quizAttemptId) {
        log.debug("Fetching quiz attempt with ID: {}", quizAttemptId);

        if (quizAttemptId == null) {
            throw new ResourceNotValidException("Quiz attempt ID cannot be null");
        }

        return quizAttemptRepository.findById(quizAttemptId)
                .map(this::convertToOutDTO);
    }

    /**
     * Retrieves all quiz attempts with pagination support.
     *
     * @param pageable the pagination information including page number, size, and sorting
     * @return Page containing QuizAttemptOutDTO objects with pagination metadata
     * @throws ResourceNotValidException if the pageable parameter is null
     */
    @Override
    @Transactional(readOnly = true)
    public Page<QuizAttemptOutDTO> getAllQuizAttempts(final Pageable pageable) {
        log.debug("Fetching all quiz attempts with pagination");

        if (pageable == null) {
            throw new ResourceNotValidException("Pageable cannot be null");
        }

        Page<QuizAttempt> attemptPage = quizAttemptRepository.findAll(pageable);
        List<QuizAttemptOutDTO> attemptDTOs = attemptPage.getContent().stream()
                .map(this::convertToOutDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(attemptDTOs, pageable, attemptPage.getTotalElements());
    }

    /**
     * Retrieves all quiz attempts for a specific user, ordered by creation date (newest first).
     *
     * @param userId the unique identifier of the user whose attempts to retrieve
     * @return List of QuizAttemptOutDTO objects ordered by creation date descending
     * @throws ResourceNotValidException if the user ID is null
     */
    @Override
    @Transactional(readOnly = true)
    public List<QuizAttemptOutDTO> getQuizAttemptsByUserId(final Long userId) {
        log.debug("Fetching quiz attempts for user: {}", userId);

        if (userId == null) {
            throw new ResourceNotValidException("User ID cannot be null");
        }

        return quizAttemptRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToOutDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all quiz attempts for a specific quiz, ordered by creation date (newest first).
     *
     * @param quizId the unique identifier of the quiz whose attempts to retrieve
     * @return List of QuizAttemptOutDTO objects ordered by creation date descending
     * @throws ResourceNotValidException if the quiz ID is null
     */
    @Override
    @Transactional(readOnly = true)
    public List<QuizAttemptOutDTO> getQuizAttemptsByQuizId(final Long quizId) {
        log.debug("Fetching quiz attempts for quiz: {}", quizId);

        if (quizId == null) {
            throw new ResourceNotValidException("Quiz ID cannot be null");
        }

        return quizAttemptRepository.findByQuizIdOrderByCreatedAtDesc(quizId)
                .stream()
                .map(this::convertToOutDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all quiz attempts for a specific user and quiz combination,
     * ordered by attempt number (highest first).
     *
     * @param userId the unique identifier of the user
     * @param quizId the unique identifier of the quiz
     * @return List of QuizAttemptOutDTO objects ordered by attempt number descending
     * @throws ResourceNotValidException if either user ID or quiz ID is null
     */
    @Override
    @Transactional(readOnly = true)
    public List<QuizAttemptOutDTO> getQuizAttemptsByUserAndQuiz(final Long userId, final Long quizId) {
        log.debug("Fetching quiz attempts for user: {} and quiz: {}", userId, quizId);

        if (userId == null || quizId == null) {
            throw new ResourceNotValidException("User ID and Quiz ID cannot be null");
        }

        return quizAttemptRepository.findByUserIdAndQuizIdOrderByAttemptDesc(userId, quizId)
                .stream()
                .map(this::convertToOutDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all quiz attempts with a specific status, ordered by creation date (newest first).
     *
     * @param status the status to filter by (IN_PROGRESS, COMPLETED, ABANDONED, TIMED_OUT)
     * @return List of QuizAttemptOutDTO objects with the specified status
     * @throws ResourceNotValidException if the status is null, empty, or invalid
     */
    @Override
    @Transactional(readOnly = true)
    public List<QuizAttemptOutDTO> getQuizAttemptsByStatus(final String status) {
        log.debug("Fetching quiz attempts with status: {}", status);

        if (status == null || status.trim().isEmpty()) {
            throw new ResourceNotValidException("Status cannot be null or empty");
        }

        if (!isValidStatus(status)) {
            throw new ResourceNotValidException("Invalid status: " + status);
        }

        return quizAttemptRepository.findByStatusOrderByCreatedAtDesc(status)
                .stream()
                .map(this::convertToOutDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the most recent quiz attempt for a specific user and quiz combination.
     *
     * @param userId the unique identifier of the user
     * @param quizId the unique identifier of the quiz
     * @return Optional containing the latest QuizAttemptOutDTO if found, empty otherwise
     * @throws ResourceNotValidException if either user ID or quiz ID is null
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<QuizAttemptOutDTO> getLatestAttemptByUserAndQuiz(final Long userId, final Long quizId) {
        log.debug("Fetching latest attempt for user: {} and quiz: {}", userId, quizId);

        if (userId == null || quizId == null) {
            throw new ResourceNotValidException("User ID and Quiz ID cannot be null");
        }

        return quizAttemptRepository.findLatestAttemptByUserAndQuiz(userId, quizId)
                .map(this::convertToOutDTO);
    }

    /**
     * Permanently deletes a quiz attempt from the system.
     *
     * @param quizAttemptId the unique identifier of the quiz attempt to delete
     * @throws ResourceNotValidException if the quiz attempt ID is null
     * @throws ResourceNotFoundException if the quiz attempt with the given ID is not found
     */
    @Override
    public void deleteQuizAttempt(final Long quizAttemptId) {
        log.info("Deleting quiz attempt with ID: {}", quizAttemptId);

        if (quizAttemptId == null) {
            throw new ResourceNotValidException("Quiz attempt ID cannot be null");
        }

        if (!quizAttemptRepository.existsById(quizAttemptId)) {
            throw new ResourceNotFoundException("QuizAttempt not found with ID: " + quizAttemptId);
        }

        quizAttemptRepository.deleteById(quizAttemptId);
        log.info("Deleted quiz attempt with ID: {}", quizAttemptId);
    }

    /**
     * Marks a quiz attempt as completed and records the score details.
     *
     * <p>This method can only be called on attempts with "IN_PROGRESS" status.
     * It automatically sets the finished timestamp to the current time.</p>
     *
     * @param quizAttemptId the unique identifier of the quiz attempt to complete
     * @param scoreDetails  the score details or results of the completed attempt
     * @return QuizAttemptOutDTO containing the completed attempt details
     * @throws ResourceNotValidException if the quiz attempt ID is null or attempt cannot be completed
     * @throws ResourceNotFoundException if the quiz attempt with the given ID is not found
     */
    @Override
    public QuizAttemptOutDTO completeAttempt(final Long quizAttemptId, final String scoreDetails) {
        log.info("Completing quiz attempt with ID: {}", quizAttemptId);

        if (quizAttemptId == null) {
            throw new ResourceNotValidException("Quiz attempt ID cannot be null");
        }

        QuizAttempt attempt = quizAttemptRepository.findById(quizAttemptId)
                .orElseThrow(() -> new ResourceNotFoundException("QuizAttempt not found with ID: " + quizAttemptId));

        // Validate that attempt can be completed
        if (!"IN_PROGRESS".equals(attempt.getStatus())) {
            throw new ResourceNotValidException("Cannot complete quiz attempt with status: " + attempt.getStatus());
        }

        attempt.setStatus("COMPLETED");
        attempt.setFinishedAt(LocalDateTime.now());
        attempt.setScoreDetails(scoreDetails);
        attempt.setUpdatedAt(LocalDateTime.now());

        QuizAttempt savedAttempt = quizAttemptRepository.save(attempt);
        return convertToOutDTO(savedAttempt);
    }

    /**
     * Marks a quiz attempt as abandoned by the user.
     *
     * <p>This method can only be called on attempts with "IN_PROGRESS" status.
     * It automatically sets the finished timestamp to the current time.</p>
     *
     * @param quizAttemptId the unique identifier of the quiz attempt to abandon
     * @return QuizAttemptOutDTO containing the abandoned attempt details
     * @throws ResourceNotValidException if the quiz attempt ID is null or attempt cannot be abandoned
     * @throws ResourceNotFoundException if the quiz attempt with the given ID is not found
     */
    @Override
    public QuizAttemptOutDTO abandonAttempt(final Long quizAttemptId) {
        log.info("Abandoning quiz attempt with ID: {}", quizAttemptId);

        if (quizAttemptId == null) {
            throw new ResourceNotValidException("Quiz attempt ID cannot be null");
        }

        QuizAttempt attempt = quizAttemptRepository.findById(quizAttemptId)
                .orElseThrow(() -> new ResourceNotFoundException("QuizAttempt not found with ID: " + quizAttemptId));

        // Validate that attempt can be abandoned
        if (!"IN_PROGRESS".equals(attempt.getStatus())) {
            throw new ResourceNotValidException("Cannot abandon quiz attempt with status: " + attempt.getStatus());
        }

        attempt.setStatus("ABANDONED");
        attempt.setFinishedAt(LocalDateTime.now());
        attempt.setUpdatedAt(LocalDateTime.now());

        QuizAttempt savedAttempt = quizAttemptRepository.save(attempt);
        return convertToOutDTO(savedAttempt);
    }

    /**
     * Marks a quiz attempt as timed out due to exceeding the time limit.
     *
     * <p>This method can only be called on attempts with "IN_PROGRESS" status.
     * It automatically sets the finished timestamp to the current time.</p>
     *
     * @param quizAttemptId the unique identifier of the quiz attempt to time out
     * @return QuizAttemptOutDTO containing the timed out attempt details
     * @throws ResourceNotValidException if the quiz attempt ID is null or attempt cannot be timed out
     * @throws ResourceNotFoundException if the quiz attempt with the given ID is not found
     */
    @Override
    public QuizAttemptOutDTO timeOutAttempt(final Long quizAttemptId) {
        log.info("Timing out quiz attempt with ID: {}", quizAttemptId);

        if (quizAttemptId == null) {
            throw new ResourceNotValidException("Quiz attempt ID cannot be null");
        }

        QuizAttempt attempt = quizAttemptRepository.findById(quizAttemptId)
                .orElseThrow(() -> new ResourceNotFoundException("QuizAttempt not found with ID: " + quizAttemptId));

        // Validate that attempt can be timed out
        if (!"IN_PROGRESS".equals(attempt.getStatus())) {
            throw new ResourceNotValidException("Cannot time out quiz attempt with status: " + attempt.getStatus());
        }

        attempt.setStatus("TIMED_OUT");
        attempt.setFinishedAt(LocalDateTime.now());
        attempt.setUpdatedAt(LocalDateTime.now());

        QuizAttempt savedAttempt = quizAttemptRepository.save(attempt);
        return convertToOutDTO(savedAttempt);
    }

    @Override
    public List<UserQuizAttemptDetailsOutDTO> getUserAttemptDetails(Long userId, Long courseId) {
        try {
            // Single query to get all quiz attempts for user in the course with quiz details
            // This replaces multiple separate queries
            List<Object[]> attemptData = quizAttemptRepository.findUserAttemptDetailsWithQuizInfo(userId, courseId);

            if (attemptData.isEmpty()) {
                return new ArrayList<>();
            }

            // Extract unique quiz IDs and attempt info for bulk queries
            Set<Long> quizIds = new HashSet<>();
            Set<String> attemptKeys = new HashSet<>();

            for (Object[] data : attemptData) {
                Long quizId = (Long) data[2]; // quiz_id from the query
                Long attempt = (Long) data[1]; // attempt from the query
                quizIds.add(quizId);
                attemptKeys.add(userId + "_" + quizId + "_" + attempt);
            }

            // Bulk fetch all user responses for all attempts in one query
            List<UserResponse> allUserResponses = userResponseRepository
                    .findByUserIdAndQuizIdInAndAttemptIn(userId, new ArrayList<>(quizIds),
                            attemptData.stream().map(data -> (Long) data[1]).collect(Collectors.toList()));

            // Bulk fetch all quiz questions for all quizzes in one query
            List<QuizQuestion> allQuizQuestions = quizQuestionRepository
                    .findByQuizIdInOrderByQuizIdAscPositionAsc(new ArrayList<>(quizIds));

            // Group data by quiz_id and attempt for efficient processing
            Map<String, List<UserResponse>> responsesByAttempt = allUserResponses.stream()
                    .collect(Collectors.groupingBy(r -> r.getUserId() + "_" + r.getQuizId() + "_" + r.getAttempt()));

            Map<Long, List<QuizQuestion>> questionsByQuiz = allQuizQuestions.stream()
                    .collect(Collectors.groupingBy(QuizQuestion::getQuizId));

            // Create a map of questionId to correct answer for quick lookup
            Map<Long, String> correctAnswersByQuestionId = allQuizQuestions.stream()
                    .collect(Collectors.toMap(QuizQuestion::getQuestionId, QuizQuestion::getCorrectAnswer));

            // Pre-calculate max scores for each quiz to avoid repeated calculations
            Map<Long, BigDecimal> maxScoresByQuiz = questionsByQuiz.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> entry.getValue().stream()
                                    .map(QuizQuestion::getPoints)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    ));

            List<UserQuizAttemptDetailsOutDTO> results = new ArrayList<>();

            // Process each attempt
            for (Object[] data : attemptData) {
                // Extract data from the joined query result with proper type conversion
                Long quizAttemptId = (Long) data[0];
                Long attempt = (Long) data[1];
                Long quizId = (Long) data[2];

                // Convert Timestamp to LocalDateTime safely
                LocalDateTime startedAt = convertTimestampToLocalDateTime(data[3]);
                LocalDateTime finishedAt = convertTimestampToLocalDateTime(data[4]);

                String scoreDetails = (String) data[5];
                String status = (String) data[6];

                // Convert Timestamp to LocalDateTime safely
                LocalDateTime createdAt = convertTimestampToLocalDateTime(data[7]);
                LocalDateTime updatedAt = convertTimestampToLocalDateTime(data[8]);

                String quizTitle = (String) data[9];
                String quizDescription = (String) data[10];
                Integer timeLimit = (Integer) data[11];
                Integer attemptsAllowed = (Integer) data[12];
                BigDecimal passingScore = (BigDecimal) data[13];

                String attemptKey = userId + "_" + quizId + "_" + attempt;

                // Get responses for this specific attempt
                List<UserResponse> attemptResponses = responsesByAttempt.getOrDefault(attemptKey, new ArrayList<>());

                // Get questions for this quiz
                List<QuizQuestion> quizQuestions = questionsByQuiz.getOrDefault(quizId, new ArrayList<>());

                // Calculate scores and statistics
                BigDecimal totalScore = attemptResponses.stream()
                        .map(UserResponse::getPointsEarned)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal maxPossibleScore = maxScoresByQuiz.getOrDefault(quizId, BigDecimal.ZERO);

                long correctAnswers = attemptResponses.stream()
                        .mapToLong(response -> response.getIsCorrect() ? 1L : 0L)
                        .sum();

                long totalQuestions = quizQuestions.size();

                BigDecimal percentageScore = maxPossibleScore.compareTo(BigDecimal.ZERO) > 0
                        ? totalScore.multiply(BigDecimal.valueOf(100))
                        .divide(maxPossibleScore, 2, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO;

                // Convert responses to DTOs with correct answers
                List<UserResponseWithCorrectAnswerOutDTO> responseOutDTOs = attemptResponses.stream()
                        .map(response -> convertToUserResponseWithCorrectAnswerOutDTO(response, correctAnswersByQuestionId))
                        .collect(Collectors.toList());

                // Build QuizAttemptOutDTO from query data
                QuizAttemptOutDTO attemptOutDTO = QuizAttemptOutDTO.builder()
                        .quizAttemptId(quizAttemptId)
                        .attempt(attempt)
                        .quizId(quizId)
                        .userId(userId)
                        .startedAt(startedAt)
                        .finishedAt(finishedAt)
                        .scoreDetails(scoreDetails)
                        .status(status)
                        .createdAt(createdAt)
                        .updatedAt(updatedAt)
                        .build();

                // Determine submission type
                String submissionType = determineSubmissionType(status);

                // Build the result DTO
                UserQuizAttemptDetailsOutDTO resultDTO = UserQuizAttemptDetailsOutDTO.builder()
                        .quizAttempt(attemptOutDTO)
                        .userResponses(responseOutDTOs)
                        .totalScore(totalScore)
                        .maxPossibleScore(maxPossibleScore)
                        .correctAnswers(correctAnswers)
                        .totalQuestions(totalQuestions)
                        .percentageScore(percentageScore)
                        .submissionType(submissionType)
                        .submittedAt(finishedAt)
                        .build();

                results.add(resultDTO);
            }

            // Sort results by attempt number (most recent first)
            results.sort((a, b) -> b.getQuizAttempt().getAttempt().compareTo(a.getQuizAttempt().getAttempt()));

            return results;

        } catch (Exception e) {
            log.error("Error fetching user attempt details for userId: {} and courseId: {}", userId, courseId, e);
            throw new RuntimeException("Failed to fetch user attempt details", e);
        }
    }

    /**
     * Convert UserResponse entity to UserResponseWithCorrectAnswerOutDTO
     */
    private UserResponseWithCorrectAnswerOutDTO convertToUserResponseWithCorrectAnswerOutDTO(
            UserResponse userResponse, Map<Long, String> correctAnswersByQuestionId) {

        String correctAnswer = correctAnswersByQuestionId.get(userResponse.getQuestionId());

        return UserResponseWithCorrectAnswerOutDTO.builder()
                .responseId(userResponse.getResponseId())
                .userId(userResponse.getUserId())
                .quizId(userResponse.getQuizId())
                .questionId(userResponse.getQuestionId())
                .attempt(userResponse.getAttempt())
                .userAnswer(userResponse.getUserAnswer())
                .correctAnswer(correctAnswer)
                .isCorrect(userResponse.getIsCorrect())
                .pointsEarned(userResponse.getPointsEarned())
                .answeredAt(userResponse.getAnsweredAt())
                .build();
    }

    /**
     * Safely convert Timestamp or LocalDateTime objects to LocalDateTime
     */
    private LocalDateTime convertTimestampToLocalDateTime(Object timestamp) {
        if (timestamp == null) {
            return null;
        }

        if (timestamp instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) timestamp).toLocalDateTime();
        } else if (timestamp instanceof java.time.LocalDateTime) {
            return (java.time.LocalDateTime) timestamp;
        } else {
            throw new IllegalArgumentException("Unsupported timestamp type: " + timestamp.getClass());
        }
    }

    /**
     * Convert UserResponse entity to UserResponseOutDTO
     */
    private UserResponseOutDTO convertToUserResponseOutDTO(UserResponse userResponse) {
        return UserResponseOutDTO.builder()
                .responseId(userResponse.getResponseId())
                .userId(userResponse.getUserId())
                .quizId(userResponse.getQuizId())
                .questionId(userResponse.getQuestionId())
                .attempt(userResponse.getAttempt())
                .userAnswer(userResponse.getUserAnswer())
                .isCorrect(userResponse.getIsCorrect())
                .pointsEarned(userResponse.getPointsEarned())
                .answeredAt(userResponse.getAnsweredAt())
                .build();
    }

    /**
     * Determine submission type based on attempt status
     */
    private String determineSubmissionType(String status) {
        switch (status) {
            case "COMPLETED":
                return "MANUAL_SUBMIT";
            case "TIMED_OUT":
                return "AUTO_SUBMIT";
            case "ABANDONED":
                return "ABANDONED";
            case "IN_PROGRESS":
                return "IN_PROGRESS";
            default:
                return "UNKNOWN";
        }
    }

    /**
     * Checks if a quiz attempt exists with the given ID.
     *
     * @param quizAttemptId the unique identifier of the quiz attempt to check
     * @return true if the quiz attempt exists, false otherwise
     * @throws ResourceNotValidException if the quiz attempt ID is null
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsById(final Long quizAttemptId) {
        if (quizAttemptId == null) {
            throw new ResourceNotValidException("Quiz attempt ID cannot be null");
        }

        return quizAttemptRepository.existsById(quizAttemptId);
    }

    /**
     * Counts the total number of attempts made by a user for a specific quiz.
     *
     * @param userId the unique identifier of the user
     * @param quizId the unique identifier of the quiz
     * @return the total count of attempts made by the user for the quiz
     * @throws ResourceNotValidException if either user ID or quiz ID is null
     */
    @Override
    @Transactional(readOnly = true)
    public long countAttemptsByUserAndQuiz(final Long userId, final Long quizId) {
        log.debug("Counting attempts for user: {} and quiz: {}", userId, quizId);

        if (userId == null || quizId == null) {
            throw new ResourceNotValidException("User ID and Quiz ID cannot be null");
        }

        return quizAttemptRepository.countByUserIdAndQuizId(userId, quizId);
    }

    /**
     * Converts a QuizAttempt entity to a QuizAttemptOutDTO for external representation.
     *
     * <p>This method performs a field-by-field mapping from the entity to the DTO,
     * ensuring that all relevant data is transferred while maintaining proper
     * separation between internal entity structure and external API contracts.</p>
     *
     * @param quizAttempt the QuizAttempt entity to convert
     * @return QuizAttemptOutDTO containing the converted data
     */
    private QuizAttemptOutDTO convertToOutDTO(final QuizAttempt quizAttempt) {
        QuizAttemptOutDTO dto = new QuizAttemptOutDTO();
        dto.setQuizAttemptId(quizAttempt.getQuizAttemptId());
        dto.setAttempt(quizAttempt.getAttempt());
        dto.setQuizId(quizAttempt.getQuizId());
        dto.setUserId(quizAttempt.getUserId());
        dto.setStartedAt(quizAttempt.getStartedAt());
        dto.setFinishedAt(quizAttempt.getFinishedAt());
        dto.setScoreDetails(quizAttempt.getScoreDetails());
        dto.setStatus(quizAttempt.getStatus());
        dto.setCreatedAt(quizAttempt.getCreatedAt());
        dto.setUpdatedAt(quizAttempt.getUpdatedAt());
        return dto;
    }

    /**
     * Validates if the given status is one of the allowed quiz attempt statuses.
     *
     * <p>Valid statuses are:</p>
     * <ul>
     *   <li>IN_PROGRESS - Quiz is currently being taken</li>
     *   <li>COMPLETED - Quiz has been successfully completed</li>
     *   <li>ABANDONED - Quiz was abandoned by the user</li>
     *   <li>TIMED_OUT - Quiz exceeded the time limit</li>
     * </ul>
     *
     * @param status the status string to validate
     * @return true if the status is valid, false otherwise
     */
    private boolean isValidStatus(final String status) {
        return status.equals("IN_PROGRESS")
                || status.equals("COMPLETED") || status.equals("ABANDONED")
                || status.equals("TIMED_OUT");
    }

    /**
     * Validates if a status transition from the current status to the new status is valid.
     *
     * <p>Business rule: Once a quiz attempt is marked as COMPLETED, ABANDONED, or TIMED_OUT,
     * the status cannot be changed to any other state. This ensures data integrity and
     * prevents manipulation of completed attempts.</p>
     *
     * @param currentStatus the current status of the quiz attempt
     * @param newStatus     the new status to transition to
     * @return true if the transition is invalid, false if it's valid
     */
    private boolean isInvalidStatusTransition(final String currentStatus, final String newStatus) {
        // Once completed, abandoned, or timed out, status cannot be changed
        return "COMPLETED".equals(currentStatus) || "ABANDONED".equals(currentStatus)
                || "TIMED_OUT".equals(currentStatus);
    }
}
