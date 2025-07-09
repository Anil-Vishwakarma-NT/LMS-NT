package com.nt.course_service_lms.service.serviceImpl;

import com.nt.course_service_lms.converters.UserResponseConverter;
import com.nt.course_service_lms.dto.inDTO.UserResponseInDTO;
import com.nt.course_service_lms.dto.inDTO.UserResponseUpdateInDTO;
import com.nt.course_service_lms.dto.outDTO.UserResponseOutDTO;
import com.nt.course_service_lms.entity.QuizQuestion;
import com.nt.course_service_lms.entity.UserResponse;
import com.nt.course_service_lms.exception.ResourceAlreadyExistsException;
import com.nt.course_service_lms.exception.ResourceNotFoundException;
import com.nt.course_service_lms.repository.QuizQuestionRepository;
import com.nt.course_service_lms.repository.UserResponseRepository;
import com.nt.course_service_lms.service.UserResponseService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementation of UserResponseService interface.
 * Provides business logic for managing user responses to quiz questions.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserResponseServiceImpl implements UserResponseService {

    private final UserResponseRepository userResponseRepository;
    private final UserResponseConverter userResponseConverter;
    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Override
    @Transactional
    public List<UserResponseOutDTO> createUserResponse(List<UserResponseInDTO> userResponseInDTOList) {
        log.info("Creating user responses for {} questions", userResponseInDTOList.size());

        if (userResponseInDTOList.isEmpty()) {
            throw new IllegalArgumentException("User response list cannot be empty");
        }

        try {
            // Check for existing responses
            for (UserResponseInDTO dto : userResponseInDTOList) {
                boolean exists = userResponseRepository.existsByUserIdAndQuestionIdAndAttempt(
                        dto.getUserId(), dto.getQuestionId(), dto.getAttempt());
                if (exists) {
                    throw new ResourceAlreadyExistsException(
                            String.format("User response already exists for user ID: %d, question ID: %d, attempt: %d",
                                    dto.getUserId(), dto.getQuestionId(), dto.getAttempt()));
                }
            }

            // Get all question IDs from the DTOs
            Set<Long> questionIds = userResponseInDTOList.stream()
                    .map(UserResponseInDTO::getQuestionId)
                    .collect(Collectors.toSet());

            // Fetch all questions in batch
            List<QuizQuestion> questions = quizQuestionRepository.findAllById(questionIds);

            // Create a map for quick lookup
            Map<Long, QuizQuestion> questionMap = questions.stream()
                    .collect(Collectors.toMap(QuizQuestion::getQuestionId, Function.identity()));

            // Convert all DTOs to entities with answer validation
            List<UserResponse> userResponses = userResponseInDTOList.stream()
                    .map(dto -> {
                        UserResponse entity = userResponseConverter.convertToEntity(dto);

                        // Set timestamp if not provided
                        if (entity.getAnsweredAt() == null) {
                            entity.setAnsweredAt(LocalDateTime.now());
                        }

                        // Get the corresponding question
                        QuizQuestion question = questionMap.get(dto.getQuestionId());
                        if (question == null) {
                            throw new ResourceNotFoundException(
                                    String.format("Question with ID %d not found", dto.getQuestionId()));
                        }

                        // Validate answer and calculate points
                        boolean isCorrect = validateAnswer(dto.getUserAnswer(), question);
                        System.out.println(dto.getQuestionId() + " IS " + isCorrect);
                        entity.setIsCorrect(isCorrect);

                        // Calculate points earned
                        BigDecimal pointsEarned = isCorrect ? question.getPoints() : BigDecimal.ZERO;
                        entity.setPointsEarned(pointsEarned);

                        return entity;
                    })
                    .collect(Collectors.toList());

            // Save all entities in batch
            List<UserResponse> savedResponses = userResponseRepository.saveAll(userResponses);

            log.info("User responses created successfully. Total created: {}", savedResponses.size());

            // Convert all saved entities to DTOs
            return userResponseConverter.convertToOutDTOList(savedResponses);

        } catch (ResourceAlreadyExistsException | ResourceNotFoundException e) {
            log.error("Failed to create user responses: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while creating user responses", e);
            throw new RuntimeException("Failed to create user responses", e);
        }
    }

    /**
     * Validates if the user's answer is correct based on the question type and correct answer.
     * Fixed version with proper JSON array handling
     *
     * @param userAnswer    the user's answer in JSON format
     * @param question      the quiz question entity
     * @return true if the answer is correct, false otherwise
     */
    private boolean validateAnswer(String userAnswer, QuizQuestion question) {
        try {
            String questionType = question.getQuestionType().toLowerCase();
            String correctAnswer = question.getCorrectAnswer();

            log.debug("Validating answer for question {}: type={}, userAnswer={}, correctAnswer={}",
                    question.getQuestionId(), questionType, userAnswer, correctAnswer);

            switch (questionType) {
                case "mcq_single":
                    return validateSingleChoiceAnswer(userAnswer, correctAnswer);

                case "mcq_multiple":
                    return validateMultipleChoiceAnswer(userAnswer, correctAnswer);

                case "short_answer":
                case "text":
                    return validateTextAnswer(userAnswer, correctAnswer);

                default:
                    log.warn("Unknown question type: {}. Defaulting to text comparison.", questionType);
                    return validateTextAnswer(userAnswer, correctAnswer);
            }
        } catch (Exception e) {
            log.error("Error validating answer for question type: {}", question.getQuestionType(), e);
            return false;
        }
    }

    /**
     * Validates single choice answers (radio buttons, dropdowns).
     * Fixed to handle JSON array format properly
     */
    private boolean validateSingleChoiceAnswer(String userAnswer, String correctAnswer) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            String selectedOption = null;

            // Handle JSON array format like ["b"]
            if (userAnswer.trim().startsWith("[") && userAnswer.trim().endsWith("]")) {
                JsonNode userArray = mapper.readTree(userAnswer);
                if (userArray.isArray() && userArray.size() > 0) {
                    selectedOption = userArray.get(0).asText();
                }
            }
            // Handle simple string with quotes
            else if (userAnswer.startsWith("\"") && userAnswer.endsWith("\"")) {
                selectedOption = userAnswer.substring(1, userAnswer.length() - 1);
            }
            // Handle JSON object format
            else if (userAnswer.startsWith("{")) {
                JsonNode userNode = mapper.readTree(userAnswer);
                selectedOption = userNode.has("answer") ? userNode.get("answer").asText() :
                        userNode.has("selected") ? userNode.get("selected").asText() : null;
            }
            // Handle plain text
            else {
                selectedOption = userAnswer.trim();
            }

            // Parse correct answer
            String correctOption = correctAnswer;
            if (correctAnswer.startsWith("[") && correctAnswer.endsWith("]")) {
                JsonNode correctArray = mapper.readTree(correctAnswer);
                if (correctArray.isArray() && correctArray.size() > 0) {
                    correctOption = correctArray.get(0).asText();
                }
            } else if (correctAnswer.startsWith("{")) {
                JsonNode correctNode = mapper.readTree(correctAnswer);
                correctOption = correctNode.has("answer") ? correctNode.get("answer").asText() :
                        correctNode.has("correct") ? correctNode.get("correct").asText() : correctAnswer;
            }

            boolean isCorrect = selectedOption != null &&
                    selectedOption.trim().equalsIgnoreCase(correctOption.trim());

            log.debug("Single choice validation: selected='{}', correct='{}', result={}",
                    selectedOption, correctOption, isCorrect);

            return isCorrect;

        } catch (Exception e) {
            log.error("Error parsing single choice answer: userAnswer={}, correctAnswer={}",
                    userAnswer, correctAnswer, e);
            return false;
        }
    }

    /**
     * Validates multiple choice answers (checkboxes).
     * Fixed to handle JSON array format properly
     */
    private boolean validateMultipleChoiceAnswer(String userAnswer, String correctAnswer) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            // Parse user answer
            Set<String> userSelections = new HashSet<>();

            if (userAnswer.trim().startsWith("[") && userAnswer.trim().endsWith("]")) {
                JsonNode userArray = mapper.readTree(userAnswer);
                if (userArray.isArray()) {
                    for (JsonNode node : userArray) {
                        userSelections.add(node.asText().trim().toLowerCase());
                    }
                }
            } else if (userAnswer.startsWith("{")) {
                JsonNode userNode = mapper.readTree(userAnswer);
                if (userNode.has("selected") && userNode.get("selected").isArray()) {
                    for (JsonNode node : userNode.get("selected")) {
                        userSelections.add(node.asText().trim().toLowerCase());
                    }
                }
            }

            // Parse correct answer
            Set<String> correctSelections = new HashSet<>();

            if (correctAnswer.trim().startsWith("[") && correctAnswer.trim().endsWith("]")) {
                JsonNode correctArray = mapper.readTree(correctAnswer);
                if (correctArray.isArray()) {
                    for (JsonNode node : correctArray) {
                        correctSelections.add(node.asText().trim().toLowerCase());
                    }
                }
            } else if (correctAnswer.startsWith("{")) {
                JsonNode correctNode = mapper.readTree(correctAnswer);
                if (correctNode.has("correct") && correctNode.get("correct").isArray()) {
                    for (JsonNode node : correctNode.get("correct")) {
                        correctSelections.add(node.asText().trim().toLowerCase());
                    }
                }
            }

            boolean isCorrect = userSelections.equals(correctSelections);

            log.debug("Multiple choice validation: userSelections={}, correctSelections={}, result={}",
                    userSelections, correctSelections, isCorrect);

            return isCorrect;

        } catch (Exception e) {
            log.error("Error parsing multiple choice answer: userAnswer={}, correctAnswer={}",
                    userAnswer, correctAnswer, e);
            return false;
        }
    }

    /**
     * Validates text-based answers.
     * Fixed to handle JSON array format properly
     */
    private boolean validateTextAnswer(String userAnswer, String correctAnswer) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            String userText = userAnswer;
            String correctText = correctAnswer;

            // Handle JSON array format like ["sdcvsdf"]
            if (userAnswer.trim().startsWith("[") && userAnswer.trim().endsWith("]")) {
                JsonNode userArray = mapper.readTree(userAnswer);
                if (userArray.isArray() && userArray.size() > 0) {
                    userText = userArray.get(0).asText();
                }
            }
            // Handle JSON object format
            else if (userAnswer.startsWith("{")) {
                JsonNode userNode = mapper.readTree(userAnswer);
                userText = userNode.has("answer") ? userNode.get("answer").asText() : userAnswer;
            }

            // Handle correct answer parsing
            if (correctAnswer.trim().startsWith("[") && correctAnswer.trim().endsWith("]")) {
                JsonNode correctArray = mapper.readTree(correctAnswer);
                if (correctArray.isArray() && correctArray.size() > 0) {
                    correctText = correctArray.get(0).asText();
                }
            } else if (correctAnswer.startsWith("{")) {
                JsonNode correctNode = mapper.readTree(correctAnswer);
                correctText = correctNode.has("answer") ? correctNode.get("answer").asText() : correctAnswer;
            }

            // Remove quotes if present
            userText = userText.replaceAll("^\"|\"$", "");
            correctText = correctText.replaceAll("^\"|\"$", "");

            boolean isCorrect = userText.trim().equalsIgnoreCase(correctText.trim());

            log.debug("Text answer validation: userText='{}', correctText='{}', result={}",
                    userText, correctText, isCorrect);

            return isCorrect;

        } catch (Exception e) {
            log.error("Error parsing text answer: userAnswer={}, correctAnswer={}",
                    userAnswer, correctAnswer, e);
            return userAnswer.trim().equalsIgnoreCase(correctAnswer.trim());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseOutDTO getUserResponseById(Long responseId) {
        log.info("Fetching user response with ID: {}", responseId);

        try {
            UserResponse userResponse = userResponseRepository.findById(responseId)
                    .orElseThrow(() -> {
                        log.warn("User response not found with ID: {}", responseId);
                        return new ResourceNotFoundException("User response not found with ID: " + responseId);
                    });

            log.info("User response found with ID: {}", responseId);
            return userResponseConverter.convertToOutDTO(userResponse);

        } catch (ResourceNotFoundException e) {
            log.error("User response not found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching user response with ID: {}", responseId, e);
            throw new RuntimeException("Failed to fetch user response", e);
        }
    }

    @Override
    public UserResponseOutDTO updateUserResponse(Long responseId, UserResponseUpdateInDTO userResponseUpdateInDTO) {
        log.info("Updating user response with ID: {}", responseId);

        try {
            UserResponse existingUserResponse = userResponseRepository.findById(responseId)
                    .orElseThrow(() -> {
                        log.warn("User response not found with ID: {} for update", responseId);
                        return new ResourceNotFoundException("User response not found with ID: " + responseId);
                    });

            // Update entity with new data
            UserResponse updatedUserResponse = userResponseConverter.updateEntityFromDTO(existingUserResponse, userResponseUpdateInDTO);

            // Save updated entity
            UserResponse savedUserResponse = userResponseRepository.save(updatedUserResponse);
            log.info("User response updated successfully with ID: {}", savedUserResponse.getResponseId());

            // Convert and return DTO
            return userResponseConverter.convertToOutDTO(savedUserResponse);

        } catch (ResourceNotFoundException e) {
            log.error("Failed to update user response - not found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while updating user response with ID: {}", responseId, e);
            throw new RuntimeException("Failed to update user response", e);
        }
    }

    @Override
    public void deleteUserResponse(Long responseId) {
        log.info("Deleting user response with ID: {}", responseId);

        try {
            if (!userResponseRepository.existsById(responseId)) {
                log.warn("User response not found with ID: {} for deletion", responseId);
                throw new ResourceNotFoundException("User response not found with ID: " + responseId);
            }

            userResponseRepository.deleteById(responseId);
            log.info("User response deleted successfully with ID: {}", responseId);

        } catch (ResourceNotFoundException e) {
            log.error("Failed to delete user response - not found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while deleting user response with ID: {}", responseId, e);
            throw new RuntimeException("Failed to delete user response", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseOutDTO> getAllUserResponses(Pageable pageable) {
        log.info("Fetching all user responses with pagination - page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        try {
            Page<UserResponse> userResponsePage = userResponseRepository.findAll(pageable);
            log.info("Found {} user responses", userResponsePage.getTotalElements());

            return userResponsePage.map(userResponseConverter::convertToOutDTO);

        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching all user responses", e);
            throw new RuntimeException("Failed to fetch user responses", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseOutDTO> getUserResponsesByUserId(Long userId) {
        log.info("Fetching user responses for user ID: {}", userId);

        try {
            List<UserResponse> userResponses = userResponseRepository.findByUserId(userId);
            log.info("Found {} user responses for user ID: {}", userResponses.size(), userId);

            return userResponseConverter.convertToOutDTOList(userResponses);

        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching user responses for user ID: {}", userId, e);
            throw new RuntimeException("Failed to fetch user responses", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseOutDTO> getUserResponsesByQuizId(Long quizId) {
        log.info("Fetching user responses for quiz ID: {}", quizId);

        try {
            List<UserResponse> userResponses = userResponseRepository.findByQuizId(quizId);
            log.info("Found {} user responses for quiz ID: {}", userResponses.size(), quizId);

            return userResponseConverter.convertToOutDTOList(userResponses);

        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching user responses for quiz ID: {}", quizId, e);
            throw new RuntimeException("Failed to fetch user responses", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseOutDTO> getUserResponsesByUserIdAndQuizId(Long userId, Long quizId) {
        log.info("Fetching user responses for user ID: {} and quiz ID: {}", userId, quizId);

        try {
            List<UserResponse> userResponses = userResponseRepository.findByUserIdAndQuizId(userId, quizId);
            log.info("Found {} user responses for user ID: {} and quiz ID: {}", userResponses.size(), userId, quizId);

            return userResponseConverter.convertToOutDTOList(userResponses);

        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching user responses for user ID: {} and quiz ID: {}", userId, quizId, e);
            throw new RuntimeException("Failed to fetch user responses", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseOutDTO> getUserResponsesByUserIdAndQuizIdAndAttempt(Long userId, Long quizId, Long attempt) {
        log.info("Fetching user responses for user ID: {}, quiz ID: {}, attempt: {}", userId, quizId, attempt);

        try {
            List<UserResponse> userResponses = userResponseRepository.findByUserIdAndQuizIdAndAttempt(userId, quizId, attempt);
            log.info("Found {} user responses for user ID: {}, quiz ID: {}, attempt: {}",
                    userResponses.size(), userId, quizId, attempt);

            return userResponseConverter.convertToOutDTOList(userResponses);

        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching user responses for user ID: {}, quiz ID: {}, attempt: {}",
                    userId, quizId, attempt, e);
            throw new RuntimeException("Failed to fetch user responses", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseOutDTO> getUserResponsesByUserId(Long userId, Pageable pageable) {
        log.info("Fetching user responses for user ID: {} with pagination - page: {}, size: {}",
                userId, pageable.getPageNumber(), pageable.getPageSize());

        try {
            Page<UserResponse> userResponsePage = userResponseRepository.findByUserId(userId, pageable);
            log.info("Found {} user responses for user ID: {}", userResponsePage.getTotalElements(), userId);

            return userResponsePage.map(userResponseConverter::convertToOutDTO);

        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching user responses for user ID: {} with pagination", userId, e);
            throw new RuntimeException("Failed to fetch user responses", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseOutDTO> getUserResponsesByQuizId(Long quizId, Pageable pageable) {
        log.info("Fetching user responses for quiz ID: {} with pagination - page: {}, size: {}",
                quizId, pageable.getPageNumber(), pageable.getPageSize());

        try {
            Page<UserResponse> userResponsePage = userResponseRepository.findByQuizId(quizId, pageable);
            log.info("Found {} user responses for quiz ID: {}", userResponsePage.getTotalElements(), quizId);

            return userResponsePage.map(userResponseConverter::convertToOutDTO);

        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching user responses for quiz ID: {} with pagination", quizId, e);
            throw new RuntimeException("Failed to fetch user responses", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalScore(Long userId, Long quizId, Long attempt) {
        log.info("Calculating total score for user ID: {}, quiz ID: {}, attempt: {}", userId, quizId, attempt);

        try {
            BigDecimal totalScore = userResponseRepository.getTotalScoreByUserIdAndQuizIdAndAttempt(userId, quizId, attempt);
            if (totalScore == null) {
                totalScore = BigDecimal.ZERO;
            }
            log.info("Total score calculated: {} for user ID: {}, quiz ID: {}, attempt: {}", totalScore, userId, quizId, attempt);
            return totalScore;

        } catch (Exception e) {
            log.error("Unexpected error occurred while calculating total score for user ID: {}, quiz ID: {}, attempt: {}",
                    userId, quizId, attempt, e);
            throw new RuntimeException("Failed to calculate total score", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Long countCorrectAnswers(Long userId, Long quizId, Long attempt) {
        log.info("Counting correct answers for user ID: {}, quiz ID: {}, attempt: {}", userId, quizId, attempt);

        try {
            Long correctCount = userResponseRepository.countCorrectAnswersByUserIdAndQuizIdAndAttempt(userId, quizId, attempt);
            log.info("Correct answers count: {} for user ID: {}, quiz ID: {}, attempt: {}", correctCount, userId, quizId, attempt);
            return correctCount;

        } catch (Exception e) {
            log.error("Unexpected error occurred while counting correct answers for user ID: {}, quiz ID: {}, attempt: {}",
                    userId, quizId, attempt, e);
            throw new RuntimeException("Failed to count correct answers", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Long getMaxAttemptNumber(Long userId, Long quizId) {
        log.info("Getting maximum attempt number for user ID: {} and quiz ID: {}", userId, quizId);

        try {
            Long maxAttempt = userResponseRepository.getMaxAttemptByUserIdAndQuizId(userId, quizId);
            if (maxAttempt == null) {
                maxAttempt = 0L;
            }
            log.info("Maximum attempt number: {} for user ID: {} and quiz ID: {}", maxAttempt, userId, quizId);
            return maxAttempt;

        } catch (Exception e) {
            log.error("Unexpected error occurred while getting maximum attempt number for user ID: {} and quiz ID: {}",
                    userId, quizId, e);
            throw new RuntimeException("Failed to get maximum attempt number", e);
        }
    }
}