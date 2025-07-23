package com.nt.course_service_lms.service.serviceImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.course_service_lms.constants.CommonConstants;
import com.nt.course_service_lms.dto.inDTO.QuizAttemptUpdateInDTO;
import com.nt.course_service_lms.dto.inDTO.UserResponseInDTO;
import com.nt.course_service_lms.dto.outDTO.QuizAttemptOutDTO;
import com.nt.course_service_lms.dto.outDTO.QuizSubmissionResultOutDTO;
import com.nt.course_service_lms.dto.outDTO.UserResponseOutDTO;
import com.nt.course_service_lms.entity.QuizAttempt;
import com.nt.course_service_lms.entity.QuizQuestion;
import com.nt.course_service_lms.exception.ResourceNotFoundException;
import com.nt.course_service_lms.exception.ResourceNotValidException;
import com.nt.course_service_lms.repository.QuizAttemptRepository;
import com.nt.course_service_lms.repository.QuizQuestionRepository;
import com.nt.course_service_lms.service.QuizAttemptService;
import com.nt.course_service_lms.service.UserResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service class responsible for handling quiz submissions in the Learning Management System.
 * <p>
 * This service provides comprehensive functionality for processing quiz submissions including:
 * <ul>
 *   <li>Manual quiz submissions by users</li>
 *   <li>Automatic quiz submissions when time expires</li>
 *   <li>Score calculations and statistics</li>
 *   <li>Quiz attempt status management</li>
 * </ul>
 * </p>
 *
 * <p>
 * The service handles both complete and partial submissions, calculates scores based on
 * user responses, and maintains comprehensive audit trails for all quiz submission activities.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QuizSubmissionService {

    /**
     * Service for managing user responses to quiz questions.
     * Used for creating, retrieving, and calculating scores from user responses.
     */
    private final UserResponseService userResponseService;

    /**
     * Service for managing quiz attempts.
     * Used for updating quiz attempt status and completion details.
     */
    private final QuizAttemptService quizAttemptService;

    /**
     * Repository for quiz attempt data access operations.
     * Used for retrieving and validating quiz attempts.
     */
    private final QuizAttemptRepository quizAttemptRepository;

    /**
     * Repository for quiz question data access operations.
     * Used for retrieving question details and calculating maximum possible scores.
     */
    private final QuizQuestionRepository quizQuestionRepository;

    /**
     * Jackson ObjectMapper for JSON serialization and deserialization.
     * Used for converting score details to JSON format for storage.
     */
    private final ObjectMapper objectMapper;

    /**
     * Submits a quiz attempt with user responses and processes the submission.
     * <p>
     * This method handles the complete quiz submission workflow including:
     * <ul>
     *   <li>Validating the quiz attempt exists and is in progress</li>
     *   <li>Saving user responses (if provided)</li>
     *   <li>Calculating scores and statistics</li>
     *   <li>Updating quiz attempt with completion details</li>
     *   <li>Creating comprehensive submission results</li>
     * </ul>
     * </p>
     *
     * @param quizAttemptId  the unique identifier of the quiz attempt to submit
     * @param userResponses  list of user responses to quiz questions (can be null or empty for partial submissions)
     * @param submissionType the type of submission ("MANUAL" for user-initiated, "AUTO_TIMEOUT" for time-based)
     * @return QuizSubmissionResultOutDTO containing complete submission results including scores and attempt details
     * @throws ResourceNotFoundException if the quiz attempt is not found
     * @throws ResourceNotValidException if the quiz attempt is not in a valid state for submission
     * @throws RuntimeException          if any unexpected error occurs during submission processing
     */
    @Transactional
    public QuizSubmissionResultOutDTO submitQuiz(final Long quizAttemptId,
                                                 final List<UserResponseInDTO> userResponses,
                                                 final String submissionType) {
        log.info("Submitting quiz attempt ID: {} with {} responses, submission type: {}",
                quizAttemptId, userResponses != null ? userResponses.size() : 0, submissionType);

        try {
            // 1. Validate quiz attempt exists and is in progress
            QuizAttempt attempt = validateAndGetAttempt(quizAttemptId);

            // 2. Save user responses (only if there are any)
            List<UserResponseOutDTO> savedResponses = null;
            if (userResponses != null && !userResponses.isEmpty()) {
                try {
                    savedResponses = userResponseService.createUserResponse(userResponses);
                    log.info("Saved {} user responses for attempt {}", savedResponses.size(), quizAttemptId);
                } catch (ResourceNotFoundException e) {
                    log.error("Resource not found while saving user responses for attempt {}: {}", quizAttemptId, e.getMessage());
                    throw e;
                } catch (ResourceNotValidException e) {
                    log.error("Invalid resource while saving user responses for attempt {}: {}", quizAttemptId, e.getMessage());
                    throw e;
                } catch (RuntimeException e) {
                    log.error("Runtime exception while saving user responses for attempt {}", quizAttemptId, e);
                    throw new RuntimeException("Failed to save user responses", e);
                } catch (Exception e) {
                    log.error("Unexpected exception while saving user responses for attempt {}", quizAttemptId, e);
                    throw new RuntimeException("Unexpected error while saving user responses", e);
                }
            }

            // 3. Calculate scores and statistics
            QuizScoreCalculation scoreCalculation;
            try {
                scoreCalculation = calculateScores(attempt, savedResponses);
            } catch (ResourceNotFoundException e) {
                log.error("Resource not found while calculating scores for attempt {}: {}", quizAttemptId, e.getMessage());
                throw e;
            } catch (ResourceNotValidException e) {
                log.error("Invalid resource while calculating scores for attempt {}: {}", quizAttemptId, e.getMessage());
                throw e;
            } catch (RuntimeException e) {
                log.error("Runtime exception while calculating scores for attempt {}", quizAttemptId, e);
                throw new RuntimeException("Failed to calculate quiz scores", e);
            } catch (Exception e) {
                log.error("Unexpected exception while calculating scores for attempt {}", quizAttemptId, e);
                throw new RuntimeException("Unexpected error while calculating scores", e);
            }

            // 4. Update quiz attempt with completion details
            QuizAttemptOutDTO updatedAttempt;
            try {
                updatedAttempt = completeQuizAttempt(attempt, scoreCalculation, submissionType);
            } catch (ResourceNotFoundException e) {
                log.error("Resource not found while completing quiz attempt {}: {}", quizAttemptId, e.getMessage());
                throw e;
            } catch (ResourceNotValidException e) {
                log.error("Invalid resource while completing quiz attempt {}: {}", quizAttemptId, e.getMessage());
                throw e;
            } catch (RuntimeException e) {
                log.error("Runtime exception while completing quiz attempt {}", quizAttemptId, e);
                throw new RuntimeException("Failed to complete quiz attempt", e);
            } catch (Exception e) {
                log.error("Unexpected exception while completing quiz attempt {}", quizAttemptId, e);
                throw new RuntimeException("Unexpected error while completing quiz attempt", e);
            }

            // 5. Create and return submission result
            QuizSubmissionResultOutDTO result = new QuizSubmissionResultOutDTO();
            result.setQuizAttempt(updatedAttempt);
            result.setUserResponses(savedResponses);
            result.setTotalScore(scoreCalculation.getTotalScore());
            result.setMaxPossibleScore(scoreCalculation.getMaxPossibleScore());
            result.setCorrectAnswers(scoreCalculation.getCorrectAnswers());
            result.setTotalQuestions(scoreCalculation.getTotalQuestions());
            result.setPercentageScore(scoreCalculation.getPercentageScore());
            result.setSubmissionType(submissionType);
            result.setSubmittedAt(LocalDateTime.now());

            log.info("Quiz submission completed successfully for attempt {}", quizAttemptId);
            return result;

        } catch (ResourceNotFoundException e) {
            log.error("Resource not found during quiz submission for attempt {}: {}", quizAttemptId, e.getMessage());
            throw e;
        } catch (ResourceNotValidException e) {
            log.error("Invalid resource during quiz submission for attempt {}: {}", quizAttemptId, e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            log.error("Runtime exception during quiz submission for attempt {}", quizAttemptId, e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected exception during quiz submission for attempt {}", quizAttemptId, e);
            throw new RuntimeException("Failed to submit quiz", e);
        }
    }

    /**
     * Handles automatic quiz submission when the quiz time limit expires.
     * <p>
     * This method is typically called by a scheduled job or timer service when
     * a quiz attempt reaches its time limit. It processes any partial responses
     * that were saved during the quiz attempt and completes the submission
     * with a "TIMED_OUT" status.
     * </p>
     *
     * @param quizAttemptId the unique identifier of the quiz attempt that timed out
     * @param userResponses list of user responses collected before timeout (can be null or empty)
     * @return QuizSubmissionResultOutDTO containing the timeout submission results
     * @throws ResourceNotFoundException if the quiz attempt is not found
     * @throws ResourceNotValidException if the quiz attempt is not in a valid state for timeout submission
     * @throws RuntimeException          if any unexpected error occurs during timeout processing
     */
    @Transactional
    public QuizSubmissionResultOutDTO submitQuizOnTimeout(final Long quizAttemptId,
                                                          final List<UserResponseInDTO> userResponses) {
        log.info("Auto-submitting quiz attempt {} due to timeout", quizAttemptId);
        try {
            return submitQuiz(quizAttemptId, userResponses, "AUTO_TIMEOUT");
        } catch (ResourceNotFoundException e) {
            log.error("Resource not found during timeout submission for attempt {}: {}", quizAttemptId, e.getMessage());
            throw e;
        } catch (ResourceNotValidException e) {
            log.error("Invalid resource during timeout submission for attempt {}: {}", quizAttemptId, e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            log.error("Runtime exception during timeout submission for attempt {}", quizAttemptId, e);
            throw new RuntimeException("Failed to submit quiz on timeout", e);
        } catch (Exception e) {
            log.error("Unexpected exception during timeout submission for attempt {}", quizAttemptId, e);
            throw new RuntimeException("Unexpected error during timeout submission", e);
        }
    }

    /**
     * Validates and retrieves a quiz attempt for submission processing.
     * <p>
     * This method performs comprehensive validation to ensure the quiz attempt
     * is in a valid state for submission, including:
     * <ul>
     *   <li>Checking that the quiz attempt ID is not null</li>
     *   <li>Verifying the quiz attempt exists in the database</li>
     *   <li>Confirming the attempt status is "IN_PROGRESS"</li>
     * </ul>
     * </p>
     *
     * @param quizAttemptId the unique identifier of the quiz attempt to validate
     * @return QuizAttempt entity if validation passes
     * @throws ResourceNotFoundException if the quiz attempt is not found in the database
     * @throws ResourceNotValidException if the quiz attempt ID is null or the attempt is not in progress
     * @throws RuntimeException          if any unexpected error occurs during validation
     */
    private QuizAttempt validateAndGetAttempt(final Long quizAttemptId) {
        try {
            if (quizAttemptId == null) {
                throw new ResourceNotValidException("Quiz attempt ID cannot be null");
            }

            QuizAttempt attempt = quizAttemptRepository.findById(quizAttemptId)
                    .orElseThrow(() -> new ResourceNotFoundException("Quiz attempt not found with ID: " + quizAttemptId));

            if (!"IN_PROGRESS".equals(attempt.getStatus())) {
                throw new ResourceNotValidException("Quiz attempt is not in progress. Current status: " + attempt.getStatus());
            }

            return attempt;
        } catch (ResourceNotFoundException e) {
            log.error("Quiz attempt not found with ID {}: {}", quizAttemptId, e.getMessage());
            throw e;
        } catch (ResourceNotValidException e) {
            log.error("Invalid quiz attempt with ID {}: {}", quizAttemptId, e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            log.error("Runtime exception while validating quiz attempt {}", quizAttemptId, e);
            throw new RuntimeException("Failed to validate quiz attempt", e);
        } catch (Exception e) {
            log.error("Unexpected exception while validating quiz attempt {}", quizAttemptId, e);
            throw new RuntimeException("Unexpected error during quiz attempt validation", e);
        }
    }

    /**
     * Calculates comprehensive scores and statistics for a quiz submission.
     * <p>
     * This method computes various scoring metrics including:
     * <ul>
     *   <li>Total score achieved by the user</li>
     *   <li>Maximum possible score for the quiz</li>
     *   <li>Number of correct answers</li>
     *   <li>Total number of questions</li>
     *   <li>Percentage score</li>
     * </ul>
     * </p>
     *
     * <p>
     * The calculation logic handles both scenarios:
     * <ul>
     *   <li>When responses are provided: calculates based on answered questions</li>
     *   <li>When no responses: calculates based on all questions in the quiz</li>
     * </ul>
     * </p>
     *
     * @param attempt   the quiz attempt entity containing user and quiz information
     * @param responses list of user responses (can be null or empty)
     * @return QuizScoreCalculation containing all calculated scores and statistics
     * @throws ResourceNotFoundException if required data for score calculation is not found
     * @throws ResourceNotValidException if the data required for calculation is invalid
     * @throws RuntimeException          if any unexpected error occurs during score calculation
     */
    private QuizScoreCalculation calculateScores(final QuizAttempt attempt, final List<UserResponseOutDTO> responses) {
        try {
            QuizScoreCalculation calculation = new QuizScoreCalculation();

            // Get total score from user responses
            BigDecimal totalScore;
            Long correctAnswers;

            try {
                totalScore = userResponseService.getTotalScore(attempt.getUserId(), attempt.getQuizId(), attempt.getAttempt());
            } catch (ResourceNotFoundException e) {
                log.error("Resource not found while getting total score for user {}, quiz {}, attempt {}: {}",
                        attempt.getUserId(), attempt.getQuizId(), attempt.getAttempt(), e.getMessage());
                throw e;
            } catch (ResourceNotValidException e) {
                log.error("Invalid resource while getting total score for user {}, quiz {}, attempt {}: {}",
                        attempt.getUserId(), attempt.getQuizId(), attempt.getAttempt(), e.getMessage());
                throw e;
            } catch (RuntimeException e) {
                log.error("Runtime exception while getting total score for user {}, quiz {}, attempt {}",
                        attempt.getUserId(), attempt.getQuizId(), attempt.getAttempt(), e);
                throw new RuntimeException("Failed to get total score", e);
            } catch (Exception e) {
                log.error("Unexpected exception while getting total score for user {}, quiz {}, attempt {}",
                        attempt.getUserId(), attempt.getQuizId(), attempt.getAttempt(), e);
                throw new RuntimeException("Unexpected error while getting total score", e);
            }

            try {
                correctAnswers = userResponseService.countCorrectAnswers(
                        attempt.getUserId(),
                        attempt.getQuizId(),
                        attempt.getAttempt()
                );
            } catch (ResourceNotFoundException e) {
                log.error("Resource not found while counting correct answers for user {}, quiz {}, attempt {}: {}",
                        attempt.getUserId(), attempt.getQuizId(), attempt.getAttempt(), e.getMessage());
                throw e;
            } catch (ResourceNotValidException e) {
                log.error("Invalid resource while counting correct answers for user {}, quiz {}, attempt {}: {}",
                        attempt.getUserId(), attempt.getQuizId(), attempt.getAttempt(), e.getMessage());
                throw e;
            } catch (RuntimeException e) {
                log.error("Runtime exception while counting correct answers for user {}, quiz {}, attempt {}",
                        attempt.getUserId(), attempt.getQuizId(), attempt.getAttempt(), e);
                throw new RuntimeException("Failed to count correct answers", e);
            } catch (Exception e) {
                log.error("Unexpected exception while counting correct answers for user {}, quiz {}, attempt {}",
                        attempt.getUserId(), attempt.getQuizId(), attempt.getAttempt(), e);
                throw new RuntimeException("Unexpected error while counting correct answers", e);
            }

            // Calculate max possible score from the original quiz questions
            BigDecimal maxPossibleScore = BigDecimal.ZERO;
            long totalQuestions = 0L;

            if (responses != null && !responses.isEmpty()) {
                // Get question IDs from responses
                Set<Long> questionIds = responses.stream()
                        .map(UserResponseOutDTO::getQuestionId)
                        .collect(Collectors.toSet());

                // Fetch the original questions to get their points
                List<QuizQuestion> questions = quizQuestionRepository.findAllById(questionIds);

                maxPossibleScore = questions.stream()
                        .map(QuizQuestion::getPoints)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                totalQuestions = (long) questions.size();

                log.debug("Calculated max possible score from {} questions: {}", questions.size(), maxPossibleScore);
            } else {
                // If no responses, calculate from all questions in the quiz
                List<QuizQuestion> allQuestions = quizQuestionRepository.findByQuizId(attempt.getQuizId());

                maxPossibleScore = allQuestions.stream()
                        .map(QuizQuestion::getPoints)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                totalQuestions = (long) allQuestions.size();

                log.debug("No responses provided, calculated max possible score from all {} quiz questions: {}",
                        allQuestions.size(), maxPossibleScore);
            }

            calculation.setTotalScore(totalScore != null ? totalScore : BigDecimal.ZERO);
            calculation.setCorrectAnswers(correctAnswers != null ? correctAnswers : 0L);
            calculation.setTotalQuestions(totalQuestions);
            calculation.setMaxPossibleScore(maxPossibleScore);

            // Calculate percentage score
            BigDecimal percentageScore = BigDecimal.ZERO;
            if (maxPossibleScore.compareTo(BigDecimal.ZERO) > 0) {
                percentageScore = calculation.getTotalScore()
                        .multiply(BigDecimal.valueOf(CommonConstants.NUMBER_HUNDRED))
                        .divide(maxPossibleScore, 2, BigDecimal.ROUND_HALF_UP);
            }
            calculation.setPercentageScore(percentageScore);

            log.info("Score calculation completed - Total Score: {}, Max Possible: {}, Percentage: {}%",
                    calculation.getTotalScore(), maxPossibleScore, percentageScore);

            return calculation;
        } catch (ResourceNotFoundException e) {
            log.error(
                    "Resource not found while calculating scores for attempt {}: {}",
                    attempt.getQuizAttemptId(),
                    e.getMessage()
            );
            throw e;
        } catch (ResourceNotValidException e) {
            log.error("Invalid resource while calculating scores for attempt {}: {}", attempt.getQuizAttemptId(), e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            log.error("Runtime exception while calculating scores for attempt {}", attempt.getQuizAttemptId(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected exception while calculating scores for attempt {}", attempt.getQuizAttemptId(), e);
            throw new RuntimeException("Unexpected error during score calculation", e);
        }
    }

    /**
     * Completes a quiz attempt by updating its status and storing comprehensive results.
     * <p>
     * This method performs the final step of quiz submission by:
     * <ul>
     *   <li>Creating detailed score information in JSON format</li>
     *   <li>Determining the appropriate completion status based on submission type</li>
     *   <li>Updating the quiz attempt record with completion details</li>
     *   <li>Setting the finish timestamp</li>
     * </ul>
     * </p>
     *
     * @param attempt        the quiz attempt entity to complete
     * @param calculation    the calculated scores and statistics
     * @param submissionType the type of submission ("MANUAL" or "AUTO_TIMEOUT")
     * @return QuizAttemptOutDTO containing the updated quiz attempt information
     * @throws ResourceNotFoundException if the quiz attempt cannot be found during update
     * @throws ResourceNotValidException if the update data is invalid
     * @throws RuntimeException          if JSON serialization fails or any unexpected error occurs
     */
    private QuizAttemptOutDTO completeQuizAttempt(final QuizAttempt attempt,
                                                  final QuizScoreCalculation calculation,
                                                  final String submissionType) {
        try {
            // Create score details JSON
            Map<String, Object> scoreDetails = new HashMap<>();
            scoreDetails.put("totalScore", calculation.getTotalScore());
            scoreDetails.put("maxPossibleScore", calculation.getMaxPossibleScore());
            scoreDetails.put("correctAnswers", calculation.getCorrectAnswers());
            scoreDetails.put("totalQuestions", calculation.getTotalQuestions());
            scoreDetails.put("percentageScore", calculation.getPercentageScore());
            scoreDetails.put("submissionType", submissionType);
            scoreDetails.put("submittedAt", LocalDateTime.now());

            String scoreDetailsJson;
            try {
                scoreDetailsJson = objectMapper.writeValueAsString(scoreDetails);
            } catch (Exception e) {
                log.error("Failed to serialize score details to JSON for attempt {}", attempt.getQuizAttemptId(), e);
                throw new RuntimeException("Failed to serialize score details", e);
            }

            // Update quiz attempt
            QuizAttemptUpdateInDTO updateDTO = new QuizAttemptUpdateInDTO();
            updateDTO.setStatus(getCompletionStatus(submissionType));
            updateDTO.setFinishedAt(LocalDateTime.now());
            updateDTO.setScoreDetails(scoreDetailsJson);

            try {
                return quizAttemptService.updateQuizAttempt(attempt.getQuizAttemptId(), updateDTO);
            } catch (ResourceNotFoundException e) {
                log.error("Resource not found while updating quiz attempt {}: {}", attempt.getQuizAttemptId(), e.getMessage());
                throw e;
            } catch (ResourceNotValidException e) {
                log.error("Invalid resource while updating quiz attempt {}: {}", attempt.getQuizAttemptId(), e.getMessage());
                throw e;
            } catch (RuntimeException e) {
                log.error("Runtime exception while updating quiz attempt {}", attempt.getQuizAttemptId(), e);
                throw new RuntimeException("Failed to update quiz attempt", e);
            } catch (Exception e) {
                log.error("Unexpected exception while updating quiz attempt {}", attempt.getQuizAttemptId(), e);
                throw new RuntimeException("Unexpected error while updating quiz attempt", e);
            }

        } catch (ResourceNotFoundException e) {
            log.error("Resource not found while completing quiz attempt {}: {}", attempt.getQuizAttemptId(), e.getMessage());
            throw e;
        } catch (ResourceNotValidException e) {
            log.error("Invalid resource while completing quiz attempt {}: {}", attempt.getQuizAttemptId(), e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            log.error("Runtime exception while completing quiz attempt {}", attempt.getQuizAttemptId(), e);
            throw e;
        } catch (Exception e) {
            log.error("Unexpected exception while completing quiz attempt {}", attempt.getQuizAttemptId(), e);
            throw new RuntimeException("Unexpected error while completing quiz attempt", e);
        }
    }

    /**
     * Determines the appropriate completion status based on the submission type.
     * <p>
     * This method maps submission types to their corresponding completion statuses:
     * <ul>
     *   <li>"AUTO_TIMEOUT" maps to "TIMED_OUT"</li>
     *   <li>"MANUAL" maps to "COMPLETED"</li>
     *   <li>Any other type defaults to "COMPLETED"</li>
     * </ul>
     * </p>
     *
     * @param submissionType the type of submission ("MANUAL", "AUTO_TIMEOUT", or other)
     * @return the appropriate completion status string
     * @throws RuntimeException if any unexpected error occurs during status determination
     */
    private String getCompletionStatus(final String submissionType) {
        try {
            return switch (submissionType) {
                case "AUTO_TIMEOUT" -> "TIMED_OUT";
                case "MANUAL" -> "COMPLETED";
                default -> "COMPLETED";
            };
        } catch (RuntimeException e) {
            log.error("Runtime exception while getting completion status for submission type {}", submissionType, e);
            throw new RuntimeException("Failed to determine completion status", e);
        } catch (Exception e) {
            log.error("Unexpected exception while getting completion status for submission type {}", submissionType, e);
            throw new RuntimeException("Unexpected error while determining completion status", e);
        }
    }

    /**
     * Inner class that encapsulates all score calculation results for a quiz submission.
     * <p>
     * This class serves as a data transfer object for passing calculated scores
     * and statistics between internal methods within the {@code QuizSubmissionService}.
     * It contains all the essential metrics needed to evaluate a quiz performance.
     * </p>
     */
    private final class QuizScoreCalculation {

        /**
         * The total score achieved by the user in the quiz.
         * <p>
         * This represents the sum of points earned for all correctly answered questions.
         * </p>
         */
        private BigDecimal totalScore;

        /**
         * The maximum possible score that can be achieved in the quiz.
         * <p>
         * This is the sum of all possible points for every question in the quiz,
         * regardless of whether the user answered correctly or not.
         * </p>
         */
        private BigDecimal maxPossibleScore;

        /**
         * The number of questions answered correctly by the user.
         */
        private Long correctAnswers;

        /**
         * The total number of questions in the quiz.
         */
        private Long totalQuestions;

        /**
         * The percentage score calculated as (totalScore / maxPossibleScore) * 100.
         * <p>
         * This value is rounded to 2 decimal places using {@code RoundingMode.HALF_UP}.
         * </p>
         */
        private BigDecimal percentageScore;

        /**
         * Gets the total score achieved by the user.
         *
         * @return the total score as a {@link BigDecimal}
         */
        public BigDecimal getTotalScore() {
            return totalScore;
        }

        /**
         * Sets the total score achieved by the user.
         *
         * @param totalScore the total score to set
         */
        public void setTotalScore(final BigDecimal totalScore) {
            this.totalScore = totalScore;
        }

        /**
         * Gets the maximum possible score for the quiz.
         *
         * @return the maximum possible score as a {@link BigDecimal}
         */
        public BigDecimal getMaxPossibleScore() {
            return maxPossibleScore;
        }

        /**
         * Sets the maximum possible score for the quiz.
         *
         * @param maxPossibleScore the maximum possible score to set
         */
        public void setMaxPossibleScore(final BigDecimal maxPossibleScore) {
            this.maxPossibleScore = maxPossibleScore;
        }

        /**
         * Gets the number of questions answered correctly by the user.
         *
         * @return the number of correct answers as a {@link Long}
         */
        public Long getCorrectAnswers() {
            return correctAnswers;
        }

        /**
         * Sets the number of questions answered correctly by the user.
         *
         * @param correctAnswers the number of correct answers
         */
        public void setCorrectAnswers(final Long correctAnswers) {
            this.correctAnswers = correctAnswers;
        }

        /**
         * Gets the total number of questions in the quiz.
         *
         * @return the total number of questions as a {@link Long}
         */
        public Long getTotalQuestions() {
            return totalQuestions;
        }

        /**
         * Sets the total number of questions in the quiz.
         *
         * @param totalQuestions the total number of questions
         */
        public void setTotalQuestions(final Long totalQuestions) {
            this.totalQuestions = totalQuestions;
        }

        /**
         * Gets the percentage score achieved by the user.
         *
         * @return the percentage score as a {@link BigDecimal}
         */
        public BigDecimal getPercentageScore() {
            return percentageScore;
        }

        /**
         * Sets the percentage score achieved by the user.
         *
         * @param percentageScore the percentage score to set
         */
        public void setPercentageScore(final BigDecimal percentageScore) {
            this.percentageScore = percentageScore;
        }
    }
}
