package com.nt.course_service_lms.service.serviceImpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.course_service_lms.dto.inDTO.QuizAttemptCreateInDTO;
import com.nt.course_service_lms.dto.inDTO.QuizAttemptUpdateInDTO;
import com.nt.course_service_lms.dto.outDTO.CourseOutDTO;
import com.nt.course_service_lms.dto.outDTO.QuizAttemptDetailsByCourseIDOutDTO;
import com.nt.course_service_lms.dto.outDTO.QuizAttemptDetailsByUserIDOutDTO;
import com.nt.course_service_lms.dto.outDTO.QuizAttemptOutDTO;
import com.nt.course_service_lms.dto.outDTO.UserQuizAttemptDetailsOutDTO;
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
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service implementation for managing quiz attempts in the Learning Management System.
 * Provides operations for creating, updating, retrieving, and managing quiz attempt lifecycle.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class QuizAttemptServiceImpl implements QuizAttemptService {

    /**
     * Status constant for ongoing quiz attempts.
     * Used to identify active quiz sessions.
     */
    public static final String IN_PROGRESS = "IN_PROGRESS";

    /**
     * Repository for quiz attempt database operations.
     * Handles CRUD operations for QuizAttempt entities.
     */
    private final QuizAttemptRepository quizAttemptRepository;

    /**
     * Repository for quiz database operations.
     * Used for quiz validation and configuration retrieval.
     */
    private final QuizRepository quizRepository;

    /**
     * Repository for user response database operations.
     * Manages user answers and response data.
     */
    private final UserResponseRepository userResponseRepository;

    /**
     * Repository for quiz question database operations.
     * Handles quiz question retrieval and validation.
     */
    private final QuizQuestionRepository quizQuestionRepository;

    /**
     * Creates a new quiz attempt for a user.
     * Validates quiz existence and enforces attempt limits.
     *
     * @param dto the quiz attempt creation data
     * @return QuizAttemptOutDTO the created attempt details
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
     * Updates an existing quiz attempt with new data.
     * Validates status transitions and auto-sets finished timestamp.
     *
     * @param quizAttemptId the attempt ID to update
     * @param dto the update data
     * @return QuizAttemptOutDTO the updated attempt details
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
     * Returns empty optional if attempt not found.
     *
     * @param quizAttemptId the attempt ID to retrieve
     * @return Optional<QuizAttemptOutDTO> the attempt details if found
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
     * Returns paginated results with sorting capabilities.
     *
     * @param pageable the pagination parameters
     * @return Page<QuizAttemptOutDTO> paginated attempt results
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
     * Retrieves all quiz attempts for a specific user.
     * Returns attempts ordered by creation date descending.
     *
     * @param userId the user ID to filter by
     * @return List<QuizAttemptOutDTO> user's quiz attempts
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
     * Retrieves all quiz attempts for a specific quiz.
     * Returns attempts ordered by creation date descending.
     *
     * @param quizId the quiz ID to filter by
     * @return List<QuizAttemptOutDTO> quiz's attempt history
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
     * Retrieves quiz attempts for a specific user and quiz combination.
     * Returns attempts ordered by attempt number descending.
     *
     * @param userId the user ID
     * @param quizId the quiz ID
     * @return List<QuizAttemptOutDTO> matching attempts
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
     * Retrieves quiz attempts filtered by status.
     * Returns attempts ordered by creation date descending.
     *
     * @param status the status to filter by
     * @return List<QuizAttemptOutDTO> matching attempts
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
     * Retrieves the most recent attempt for a user and quiz.
     * Returns empty optional if no attempts found.
     *
     * @param userId the user ID
     * @param quizId the quiz ID
     * @return Optional<QuizAttemptOutDTO> latest attempt if exists
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
     * Validates attempt existence before deletion.
     *
     * @param quizAttemptId the attempt ID to delete
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
     * Marks a quiz attempt as completed with score details.
     * Only IN_PROGRESS attempts can be completed.
     *
     * @param quizAttemptId the attempt ID to complete
     * @param scoreDetails the completion score data
     * @return QuizAttemptOutDTO the completed attempt
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
     * Only IN_PROGRESS attempts can be abandoned.
     *
     * @param quizAttemptId the attempt ID to abandon
     * @return QuizAttemptOutDTO the abandoned attempt
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
     * Marks a quiz attempt as timed out due to time limit.
     * Only IN_PROGRESS attempts can be timed out.
     *
     * @param quizAttemptId the attempt ID to time out
     * @return QuizAttemptOutDTO the timed out attempt
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

    /**
     * Retrieves detailed user attempt information for a course.
     * Includes responses, scores, and question details with optimized queries.
     *
     * @param userId the user ID
     * @param courseId the course ID
     * @return List<UserQuizAttemptDetailsOutDTO> detailed attempt data
     */
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

            // Create a map of questionId to question text for quick lookup
            Map<Long, String> questionTextByQuestionId = allQuizQuestions.stream()
                    .collect(Collectors.toMap(QuizQuestion::getQuestionId, QuizQuestion::getQuestionText));

            // Create a map of questionId to options for quick lookup
            Map<Long, String> optionsByQuestionId = allQuizQuestions.stream()
                    .collect(Collectors.toMap(QuizQuestion::getQuestionId,
                            q -> q.getOptions() != null ? q.getOptions() : ""));

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

                // Convert responses to DTOs with correct answers, question text, and options
                List<UserResponseWithCorrectAnswerOutDTO> responseOutDTOs = attemptResponses.stream()
                        .map(response -> convertToUserResponseWithCorrectAnswerOutDTO(response, correctAnswersByQuestionId, questionTextByQuestionId, optionsByQuestionId))
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
     * Converts UserResponse entity to DTO with additional question data.
     * Includes correct answers, question text, and options for comprehensive response data.
     *
     * @param userResponse the user response entity
     * @param correctAnswersByQuestionId map of question ID to correct answer
     * @param questionTextByQuestionId map of question ID to question text
     * @param optionsByQuestionId map of question ID to options
     * @return UserResponseWithCorrectAnswerOutDTO complete response data
     */
    private UserResponseWithCorrectAnswerOutDTO convertToUserResponseWithCorrectAnswerOutDTO(
            UserResponse userResponse, Map<Long, String> correctAnswersByQuestionId,
            Map<Long, String> questionTextByQuestionId, Map<Long, String> optionsByQuestionId) {

        String correctAnswer = correctAnswersByQuestionId.get(userResponse.getQuestionId());
        String questionText = questionTextByQuestionId.get(userResponse.getQuestionId());
        String options = optionsByQuestionId.get(userResponse.getQuestionId());

        return UserResponseWithCorrectAnswerOutDTO.builder()
                .responseId(userResponse.getResponseId())
                .userId(userResponse.getUserId())
                .quizId(userResponse.getQuizId())
                .questionId(userResponse.getQuestionId())
                .questionText(questionText)
                .attempt(userResponse.getAttempt())
                .options(options)
                .userAnswer(userResponse.getUserAnswer())
                .correctAnswer(correctAnswer)
                .isCorrect(userResponse.getIsCorrect())
                .pointsEarned(userResponse.getPointsEarned())
                .answeredAt(userResponse.getAnsweredAt())
                .build();
    }

    /**
     * Safely converts timestamp objects to LocalDateTime.
     * Handles Timestamp and LocalDateTime object types with null safety.
     *
     * @param timestamp the timestamp object to convert
     * @return LocalDateTime the converted timestamp or null
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
     * Determines submission type based on attempt status.
     * Maps attempt status to user-friendly submission types.
     *
     * @param status the attempt status
     * @return String the submission type
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
     * Checks if a quiz attempt exists by ID.
     * Validates existence without loading the full entity.
     *
     * @param quizAttemptId the attempt ID to check
     * @return boolean true if exists, false otherwise
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
     * Counts total attempts by a user for a specific quiz.
     * Returns the number of attempts regardless of status.
     *
     * @param userId the user ID
     * @param quizId the quiz ID
     * @return long the count of attempts
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
     * Retrieves detailed quiz attempt information grouped by user.
     * Includes course details and comprehensive attempt data with responses.
     *
     * @param userId the user ID to get details for
     * @return List<QuizAttemptDetailsByUserIDOutDTO> user's attempt details by course
     */
    @Transactional(readOnly = true)
    public List<QuizAttemptDetailsByUserIDOutDTO> getQuizAttemptDetailsByUserID(Long userId) {
        List<Object[]> results = quizAttemptRepository.findQuizAttemptDetailsByUserId(userId);

        if (results.isEmpty()) {
            return Collections.emptyList();
        }

        // Group results by course - using correct index for c.course_id
        Map<Long, List<Object[]>> groupedByCourse = results.stream()
                .filter(row -> row[23] != null) // Ensure c.course_id is not null
                .collect(Collectors.groupingBy(row -> ((Number) row[23]).longValue())); // c.course_id is at index 23

        return groupedByCourse.entrySet().stream()
                .map(courseEntry -> {
                    Long courseId = courseEntry.getKey();
                    List<Object[]> courseResults = courseEntry.getValue();

                    // Build course DTO from first row
                    Object[] firstRow = courseResults.get(0);
                    CourseOutDTO courseOutDTO = buildCourseOutDTO(firstRow);

                    // Group by quiz attempt
                    Map<String, List<Object[]>> groupedByAttempt = courseResults.stream()
                            .collect(Collectors.groupingBy(row ->
                                    row[0] + "_" + row[1] + "_" + row[2])); // quiz_attempt_id + attempt + quiz_id

                    List<UserQuizAttemptDetailsOutDTO> attemptDetails = groupedByAttempt.entrySet().stream()
                            .map(attemptEntry -> buildUserQuizAttemptDetailsOutDTO(attemptEntry.getValue()))
                            .collect(Collectors.toList());

                    return QuizAttemptDetailsByUserIDOutDTO.builder()
                            .courseOutDTO(courseOutDTO)
                            .userQuizAttemptDetailsOutDTOS(attemptDetails)
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * Retrieves detailed quiz attempt information grouped by course.
     * Includes user details and comprehensive attempt data with responses.
     *
     * @param courseId the course ID to get details for
     * @return List<QuizAttemptDetailsByCourseIDOutDTO> course's attempt details by user
     */
    public List<QuizAttemptDetailsByCourseIDOutDTO> getQuizAttemptDetailsByCourseID(Long courseId) {
        List<Object[]> results = quizAttemptRepository.findQuizAttemptDetailsByCourseId(courseId);

        if (results.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, QuizAttemptDetailsByCourseIDOutDTO> userGroupedData = new LinkedHashMap<>();
        Map<String, UserQuizAttemptDetailsOutDTO> attemptDetailsMap = new HashMap<>();

        for (Object[] row : results) {
            Long userId = ((Number) row[4]).longValue();
            Long quizAttemptId = ((Number) row[0]).longValue();
            Long attempt = ((Number) row[1]).longValue();

            // Create unique key for attempt
            String attemptKey = userId + "_" + quizAttemptId + "_" + attempt;

            // Create or get user DTO (only once per user)
            QuizAttemptDetailsByCourseIDOutDTO userDto = userGroupedData.computeIfAbsent(userId, k ->
                    QuizAttemptDetailsByCourseIDOutDTO.builder()
                            .userId(userId)
                            .userName((String) row[5])
                            .firstName((String) row[7])
                            .lastName((String) row[8])
                            .userQuizAttemptDetailsOutDTOS(new ArrayList<>())
                            .build()
            );

            // Create or get attempt details (only once per attempt)
            UserQuizAttemptDetailsOutDTO attemptDetails = attemptDetailsMap.computeIfAbsent(attemptKey, k -> {
                // Parse score_details JSON to extract pre-calculated values
                String scoreDetailsJson = (String) row[11];
                ScoreDetails scoreDetails = parseScoreDetails(scoreDetailsJson);

                QuizAttemptOutDTO quizAttempt = QuizAttemptOutDTO.builder()
                        .quizAttemptId(quizAttemptId)
                        .attempt(attempt)
                        .quizId(((Number) row[2]).longValue())
                        .userId(userId)
                        .startedAt(convertToLocalDateTime(row[9]))
                        .finishedAt(convertToLocalDateTime(row[10]))
                        .scoreDetails(scoreDetailsJson)
                        .status((String) row[12])
                        .createdAt(null) // Not in your current query
                        .updatedAt(null) // Not in your current query
                        .build();

                UserQuizAttemptDetailsOutDTO details = UserQuizAttemptDetailsOutDTO.builder()
                        .quizAttempt(quizAttempt)
                        .userResponses(new ArrayList<>())
                        // Use pre-calculated values from score_details
                        .totalScore(scoreDetails.totalScore)
                        .maxPossibleScore(scoreDetails.maxPossibleScore)
                        .correctAnswers(scoreDetails.correctAnswers)
                        .totalQuestions(scoreDetails.totalQuestions)
                        .percentageScore(scoreDetails.percentageScore)
                        .submissionType(scoreDetails.submissionType)
                        .submittedAt(scoreDetails.submittedAt)
                        .build();

                userDto.getUserQuizAttemptDetailsOutDTOS().add(details);
                return details;
            });

            // Add response data if present (only create response objects)
            if (row[13] != null) { // response_id is not null
                UserResponseWithCorrectAnswerOutDTO response = UserResponseWithCorrectAnswerOutDTO.builder()
                        .responseId(((Number) row[13]).longValue())
                        .userId(userId)
                        .quizId(((Number) row[2]).longValue())
                        .questionId(row[14] != null ? ((Number) row[14]).longValue() : null)
                        .attempt(attempt)
                        .questionText((String) row[15])
                        .options((String) row[21]) // Updated index for options
                        .userAnswer((String) row[17])
                        .correctAnswer((String) row[22]) // Updated index for correct_answer
                        .isCorrect((Boolean) row[18])
                        .pointsEarned(row[19] != null ? (BigDecimal) row[19] : BigDecimal.ZERO)
                        .answeredAt(convertToLocalDateTime(row[20]))
                        .build();

                attemptDetails.getUserResponses().add(response);
            }
        }

        return new ArrayList<>(userGroupedData.values());
    }

    /**
     * Helper class for parsing score details from JSON.
     * Contains score metrics and submission information.
     */
    private static class ScoreDetails {
        /** Total score achieved in the attempt */
        BigDecimal totalScore = BigDecimal.ZERO;
        /** Maximum possible score for the quiz */
        BigDecimal maxPossibleScore = BigDecimal.ZERO;
        /** Percentage score achieved */
        BigDecimal percentageScore = BigDecimal.ZERO;
        /** Number of correct answers */
        Long correctAnswers = 0L;
        /** Total number of questions */
        Long totalQuestions = 0L;
        /** Type of submission (manual, auto, etc.) */
        String submissionType = "MANUAL";
        /** Timestamp when attempt was submitted */
        LocalDateTime submittedAt;
    }

    /**
     * Safely converts various timestamp types to LocalDateTime.
     * Handles Timestamp, String, and Long timestamp formats.
     *
     * @param value the timestamp value to convert
     * @return LocalDateTime converted timestamp or null
     */
    private LocalDateTime convertToLocalDateTime(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Timestamp) {
            return ((Timestamp) value).toLocalDateTime();
        } else if (value instanceof String) {
            try {
                // Handle string timestamps like "2025-07-16 16:33:09.651786"
                String timestampStr = (String) value;
                // Remove potential microseconds if present
                if (timestampStr.contains(".") && timestampStr.length() > 23) {
                    timestampStr = timestampStr.substring(0, 23);
                }
                return LocalDateTime.parse(timestampStr.replace(" ", "T"));
            } catch (Exception e) {
                log.warn("Failed to parse timestamp string: {}", value, e);
                return null;
            }
        } else if (value instanceof Long) {
            // Handle Unix timestamp (milliseconds)
            return LocalDateTime.ofInstant(Instant.ofEpochMilli((Long) value), ZoneOffset.UTC);
        }

        log.warn("Unexpected timestamp type: {} for value: {}", value.getClass().getName(), value);
        return null;
    }

    /**
     * Parses score details from JSON string to ScoreDetails object.
     * Handles malformed JSON gracefully with default values.
     *
     * @param scoreDetailsJson the JSON string to parse
     * @return ScoreDetails parsed score information
     */
    private ScoreDetails parseScoreDetails(String scoreDetailsJson) {
        ScoreDetails details = new ScoreDetails();

        if (scoreDetailsJson == null || scoreDetailsJson.trim().isEmpty()) {
            return details;
        }

        try {
            // Simple JSON parsing - you might want to use Jackson or Gson for production
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(scoreDetailsJson);

            details.totalScore = new BigDecimal(jsonNode.get("totalScore").asText());
            details.maxPossibleScore = new BigDecimal(jsonNode.get("maxPossibleScore").asText());
            details.percentageScore = new BigDecimal(jsonNode.get("percentageScore").asText());
            details.correctAnswers = jsonNode.get("correctAnswers").asLong();
            details.totalQuestions = jsonNode.get("totalQuestions").asLong();
            details.submissionType = jsonNode.get("submissionType").asText();

            if (jsonNode.has("submittedAt") && !jsonNode.get("submittedAt").isNull()) {
                details.submittedAt = LocalDateTime.parse(jsonNode.get("submittedAt").asText());
            }

        } catch (Exception e) {
            // Log the error and use default values
            log.warn("Failed to parse score_details JSON: {}", scoreDetailsJson, e);
        }

        return details;
    }

    /**
     * Builds CourseOutDTO from database query result row.
     * Maps course fields from Object array to structured DTO.
     *
     * @param row the query result row containing course data
     * @return CourseOutDTO the course information
     */
    private CourseOutDTO buildCourseOutDTO(Object[] row) {
        return CourseOutDTO.builder()
                .courseId(((Number) row[23]).longValue())        // c.course_id - index 23
                .ownerId(((Number) row[24]).longValue())         // c.owner_id - index 24
                .title((String) row[25])                         // c.title as course_title - index 25
                .description((String) row[26])                   // c.description as course_description - index 26
                .level((String) row[27])                         // c.level as course_level - index 27
                .active((Boolean) row[28])                       // c.is_active as course_active - index 28
                .createdAt(row[29] != null ? ((Timestamp) row[29]).toLocalDateTime() : null) // c.created_at - index 29
                .updatedAt(row[30] != null ? ((Timestamp) row[30]).toLocalDateTime() : null) // c.updated_at - index 30
                .build();
    }

    /**
     * Builds UserQuizAttemptDetailsOutDTO from grouped attempt data.
     * Processes attempt rows to create comprehensive attempt details with statistics.
     *
     * @param attemptRows the list of rows for a single attempt
     * @return UserQuizAttemptDetailsOutDTO complete attempt details
     */
    private UserQuizAttemptDetailsOutDTO buildUserQuizAttemptDetailsOutDTO(List<Object[]> attemptRows) {
        Object[] firstRow = attemptRows.get(0);

        // Build QuizAttemptOutDTO
        QuizAttemptOutDTO quizAttemptOutDTO = QuizAttemptOutDTO.builder()
                .quizAttemptId(((Number) firstRow[0]).longValue())   // qa.quiz_attempt_id
                .attempt(((Number) firstRow[1]).longValue())         // qa.attempt
                .quizId(((Number) firstRow[2]).longValue())          // qa.quiz_id
                .userId(((Number) firstRow[3]).longValue())          // qa.user_id
                .startedAt(firstRow[4] != null ? ((Timestamp) firstRow[4]).toLocalDateTime() : null)  // qa.started_at
                .finishedAt(firstRow[5] != null ? ((Timestamp) firstRow[5]).toLocalDateTime() : null) // qa.finished_at
                .scoreDetails((String) firstRow[6])                  // qa.score_details
                .status((String) firstRow[7])                        // qa.status as attempt_status
                .createdAt(firstRow[8] != null ? ((Timestamp) firstRow[8]).toLocalDateTime() : null)  // qa.created_at
                .updatedAt(firstRow[9] != null ? ((Timestamp) firstRow[9]).toLocalDateTime() : null)  // qa.updated_at
                .build();

        // Build user responses (filter out null responses)
        List<UserResponseWithCorrectAnswerOutDTO> userResponses = attemptRows.stream()
                .filter(row -> row[10] != null) // response_id is not null
                .map(this::buildUserResponseWithCorrectAnswerOutDTO)
                .collect(Collectors.toList());

        // Calculate statistics
        BigDecimal totalScore = userResponses.stream()
                .map(UserResponseWithCorrectAnswerOutDTO::getPointsEarned)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal maxPossibleScore = attemptRows.stream()
                .filter(row -> row[20] != null) // qq.points as max_points - index 20
                .map(row -> (BigDecimal) row[20])
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long correctAnswers = userResponses.stream()
                .mapToLong(response -> Boolean.TRUE.equals(response.getIsCorrect()) ? 1 : 0)
                .sum();

        long totalQuestions = userResponses.size();

        BigDecimal percentageScore = maxPossibleScore.compareTo(BigDecimal.ZERO) > 0
                ? totalScore.divide(maxPossibleScore, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        return UserQuizAttemptDetailsOutDTO.builder()
                .quizAttempt(quizAttemptOutDTO)
                .userResponses(userResponses)
                .totalScore(totalScore)
                .maxPossibleScore(maxPossibleScore)
                .correctAnswers(correctAnswers)
                .totalQuestions(totalQuestions)
                .percentageScore(percentageScore)
                .submissionType("COMPLETED")
                .submittedAt(quizAttemptOutDTO.getFinishedAt())
                .build();
    }

    /**
     * Builds UserResponseWithCorrectAnswerOutDTO from database row.
     * Maps user response fields including question details and correct answers.
     *
     * @param row the query result row containing response data
     * @return UserResponseWithCorrectAnswerOutDTO complete response information
     */
    private UserResponseWithCorrectAnswerOutDTO buildUserResponseWithCorrectAnswerOutDTO(Object[] row) {
        return UserResponseWithCorrectAnswerOutDTO.builder()
                .responseId(((Number) row[10]).longValue())       // ur.response_id
                .userId(((Number) row[3]).longValue())            // qa.user_id
                .quizId(((Number) row[2]).longValue())            // qa.quiz_id
                .questionId(row[11] != null ? ((Number) row[11]).longValue() : null) // ur.question_id
                .questionText((String) row[18])                   // qq.question_text - index 18
                .attempt(((Number) row[1]).longValue())           // qa.attempt
                .options((String) row[21])                        // qq.options - index 21
                .userAnswer((String) row[12])                     // ur.user_answer
                .correctAnswer((String) row[22])                  // qq.correct_answer - index 22
                .isCorrect((Boolean) row[13])                     // ur.is_correct
                .pointsEarned(row[14] != null ? (BigDecimal) row[14] : BigDecimal.ZERO) // ur.points_earned
                .answeredAt(row[15] != null ? ((Timestamp) row[15]).toLocalDateTime() : null) // ur.answered_at
                .build();
    }

    /**
     * Converts QuizAttempt entity to QuizAttemptOutDTO.
     * Maps all entity fields to corresponding DTO fields for external representation.
     *
     * @param quizAttempt the entity to convert
     * @return QuizAttemptOutDTO the converted DTO
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
     * Validates if the given status is allowed for quiz attempts.
     * Checks against predefined valid status values.
     *
     * @param status the status to validate
     * @return boolean true if valid, false otherwise
     */
    private boolean isValidStatus(final String status) {
        return status.equals("IN_PROGRESS")
                || status.equals("COMPLETED") || status.equals("ABANDONED")
                || status.equals("TIMED_OUT");
    }

    /**
     * Validates if status transition is allowed based on business rules.
     * Prevents changes from final states (COMPLETED, ABANDONED, TIMED_OUT).
     *
     * @param currentStatus the current attempt status
     * @param newStatus the desired new status
     * @return boolean true if transition is invalid, false if valid
     */
    private boolean isInvalidStatusTransition(final String currentStatus, final String newStatus) {
        // Once completed, abandoned, or timed out, status cannot be changed
        return "COMPLETED".equals(currentStatus) || "ABANDONED".equals(currentStatus)
                || "TIMED_OUT".equals(currentStatus);
    }
}