package com.nt.course_service_lms.service.serviceImpl;

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
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Service for handling quiz submissions (both manual and automatic)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QuizSubmissionService {

    private final UserResponseService userResponseService;
    private final QuizAttemptService quizAttemptService;
    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final ObjectMapper objectMapper;

    /**
     * Submits a quiz attempt with user responses
     *
     * @param quizAttemptId  the quiz attempt ID
     * @param userResponses  list of user responses (can be partial)
     * @param submissionType "MANUAL" or "AUTO_TIMEOUT"
     * @return QuizSubmissionResultOutDTO containing attempt details and responses
     */
    @Transactional
    public QuizSubmissionResultOutDTO submitQuiz(Long quizAttemptId,
                                                 List<UserResponseInDTO> userResponses,
                                                 String submissionType) {
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
     * Handles automatic quiz submission when time runs out
     */
    @Transactional
    public QuizSubmissionResultOutDTO submitQuizOnTimeout(Long quizAttemptId,
                                                          List<UserResponseInDTO> userResponses) {
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
     * Handles manual quiz submission by user
     */
    @Transactional
    public QuizSubmissionResultOutDTO submitQuizManually(Long quizAttemptId,
                                                         List<UserResponseInDTO> userResponses) {
        log.info("Manually submitting quiz attempt {}", quizAttemptId);
        try {
            return submitQuiz(quizAttemptId, userResponses, "MANUAL");
        } catch (ResourceNotFoundException e) {
            log.error("Resource not found during manual submission for attempt {}: {}", quizAttemptId, e.getMessage());
            throw e;
        } catch (ResourceNotValidException e) {
            log.error("Invalid resource during manual submission for attempt {}: {}", quizAttemptId, e.getMessage());
            throw e;
        } catch (RuntimeException e) {
            log.error("Runtime exception during manual submission for attempt {}", quizAttemptId, e);
            throw new RuntimeException("Failed to submit quiz manually", e);
        } catch (Exception e) {
            log.error("Unexpected exception during manual submission for attempt {}", quizAttemptId, e);
            throw new RuntimeException("Unexpected error during manual submission", e);
        }
    }

    private QuizAttempt validateAndGetAttempt(Long quizAttemptId) {
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

    private QuizScoreCalculation calculateScores(QuizAttempt attempt, List<UserResponseOutDTO> responses) {
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
                correctAnswers = userResponseService.countCorrectAnswers(attempt.getUserId(), attempt.getQuizId(), attempt.getAttempt());
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
            Long totalQuestions = 0L;

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
                        .multiply(BigDecimal.valueOf(100))
                        .divide(maxPossibleScore, 2, BigDecimal.ROUND_HALF_UP);
            }
            calculation.setPercentageScore(percentageScore);

            log.info("Score calculation completed - Total Score: {}, Max Possible: {}, Percentage: {}%",
                    calculation.getTotalScore(), maxPossibleScore, percentageScore);

            return calculation;
        } catch (ResourceNotFoundException e) {
            log.error("Resource not found while calculating scores for attempt {}: {}", attempt.getQuizAttemptId(), e.getMessage());
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

    private QuizAttemptOutDTO completeQuizAttempt(QuizAttempt attempt,
                                                  QuizScoreCalculation calculation,
                                                  String submissionType) {
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

    private String getCompletionStatus(String submissionType) {
        try {
            switch (submissionType) {
                case "AUTO_TIMEOUT":
                    return "TIMED_OUT";
                case "MANUAL":
                    return "COMPLETED";
                default:
                    return "COMPLETED";
            }
        } catch (RuntimeException e) {
            log.error("Runtime exception while getting completion status for submission type {}", submissionType, e);
            throw new RuntimeException("Failed to determine completion status", e);
        } catch (Exception e) {
            log.error("Unexpected exception while getting completion status for submission type {}", submissionType, e);
            throw new RuntimeException("Unexpected error while determining completion status", e);
        }
    }

    /**
     * Inner class to hold score calculation results
     */
    private static class QuizScoreCalculation {
        private BigDecimal totalScore;
        private BigDecimal maxPossibleScore;
        private Long correctAnswers;
        private Long totalQuestions;
        private BigDecimal percentageScore;

        // Getters and setters
        public BigDecimal getTotalScore() {
            return totalScore;
        }

        public void setTotalScore(BigDecimal totalScore) {
            this.totalScore = totalScore;
        }

        public BigDecimal getMaxPossibleScore() {
            return maxPossibleScore;
        }

        public void setMaxPossibleScore(BigDecimal maxPossibleScore) {
            this.maxPossibleScore = maxPossibleScore;
        }

        public Long getCorrectAnswers() {
            return correctAnswers;
        }

        public void setCorrectAnswers(Long correctAnswers) {
            this.correctAnswers = correctAnswers;
        }

        public Long getTotalQuestions() {
            return totalQuestions;
        }

        public void setTotalQuestions(Long totalQuestions) {
            this.totalQuestions = totalQuestions;
        }

        public BigDecimal getPercentageScore() {
            return percentageScore;
        }

        public void setPercentageScore(BigDecimal percentageScore) {
            this.percentageScore = percentageScore;
        }
    }
}