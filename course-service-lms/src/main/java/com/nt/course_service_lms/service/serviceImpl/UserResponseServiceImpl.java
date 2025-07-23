package com.nt.course_service_lms.service.serviceImpl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    /**
     * Repository for user response data access operations.
     * Used for retrieving and validating user responses.
     */
    private final UserResponseRepository userResponseRepository;
    /**
     * User convertor object.
     */
    private final UserResponseConverter userResponseConverter;
    /**
     * Repository for quiz question data access operations.
     * Used for retrieving and validating quiz question.
     */
    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    /**
     * Creates multiple user responses for quiz questions in batch.
     *
     * <p>This method validates that no duplicate responses exist for the same user, question, and attempt
     * combination before creating new responses. It calculates points earned based on answer correctness
     * and sets timestamps for each response.</p>
     *
     * <p>The method performs the following operations:</p>
     * <ul>
     *   <li>Validates input list is not empty</li>
     *   <li>Checks for existing responses to prevent duplicates</li>
     *   <li>Batch fetches all referenced questions for validation</li>
     *   <li>Validates each user answer against the correct answer</li>
     *   <li>Calculates points earned based on correctness</li>
     *   <li>Sets timestamps if not provided</li>
     *   <li>Saves all responses in batch for performance</li>
     * </ul>
     *
     * @param userResponseInDTOList List of user response input DTOs containing user answers
     * @return List of created user response output DTOs with generated IDs and calculated scores
     * @throws IllegalArgumentException       if the input list is empty
     * @throws ResourceAlreadyExistsException if a response already exists for the same user, question, and attempt
     * @throws ResourceNotFoundException      if any referenced question is not found
     * @throws RuntimeException               if any unexpected error occurs during processing
     */
    @Override
    @Transactional
    public List<UserResponseOutDTO> createUserResponse(final List<UserResponseInDTO> userResponseInDTOList) {
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
     *
     * <p>This method supports multiple question types and answer formats:</p>
     * <ul>
     *   <li><strong>mcq_single</strong> - Single choice questions (radio buttons, dropdowns)</li>
     *   <li><strong>mcq_multiple</strong> - Multiple choice questions (checkboxes)</li>
     *   <li><strong>short_answer</strong> - Short text answers</li>
     *   <li><strong>text</strong> - Free text answers</li>
     * </ul>
     *
     * <p>The method handles various JSON formats for both user answers and correct answers,
     * including arrays, objects, and plain text. It performs case-insensitive comparison
     * for text-based answers.</p>
     *
     * @param userAnswer the user's answer in JSON format (e.g., ["a"], {"answer": "text"}, or plain text)
     * @param question   the quiz question entity containing the correct answer and question type
     * @return true if the answer is correct, false otherwise
     */
    private boolean validateAnswer(final String userAnswer, final QuizQuestion question) {
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
     * Validates single choice answers for radio buttons and dropdown questions.
     *
     * <p>This method handles multiple input formats for user answers:</p>
     * <ul>
     *   <li>JSON array format: ["a"], ["option1"]</li>
     *   <li>JSON object format: {"answer": "a"}, {"selected": "option1"}</li>
     *   <li>Quoted string format: "a"</li>
     *   <li>Plain text format: a</li>
     * </ul>
     *
     * <p>The correct answer is parsed similarly and comparison is case-insensitive.</p>
     *
     * @param userAnswer    the user's selected answer in various JSON or text formats
     * @param correctAnswer the correct answer in similar formats
     * @return true if the user's selection matches the correct answer, false otherwise
     */
    private boolean validateSingleChoiceAnswer(final String userAnswer, final String correctAnswer) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            String selectedOption = null;

            // Handle JSON array format like ["b"]
            if (userAnswer.trim().startsWith("[") && userAnswer.trim().endsWith("]")) {
                JsonNode userArray = mapper.readTree(userAnswer);
                if (userArray.isArray() && userArray.size() > 0) {
                    selectedOption = userArray.get(0).asText();
                }
            } else if (userAnswer.startsWith("\"") && userAnswer.endsWith("\"")) {
                // Handle simple string with quotes
                selectedOption = userAnswer.substring(1, userAnswer.length() - 1);
            } else if (userAnswer.startsWith("{")) {
                // Handle JSON object format
                JsonNode userNode = mapper.readTree(userAnswer);
                selectedOption = userNode.has("answer") ? userNode.get("answer").asText()
                        : userNode.has("selected") ? userNode.get("selected").asText() : null;
            } else {
                // Handle plain text
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
                correctOption = correctNode.has("answer") ? correctNode.get("answer").asText()
                        : correctNode.has("correct") ? correctNode.get("correct").asText() : correctAnswer;
            }

            boolean isCorrect = selectedOption != null
                    && selectedOption.trim().equalsIgnoreCase(correctOption.trim());

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
     * Validates multiple choice answers for checkbox questions.
     *
     * <p>This method handles multiple input formats for user answers:</p>
     * <ul>
     *   <li>JSON array format: ["a", "b", "c"]</li>
     *   <li>JSON object format: {"selected": ["a", "b", "c"]}</li>
     * </ul>
     *
     * <p>The correct answer is parsed similarly. The validation requires an exact match
     * of all selected options (case-insensitive). Order doesn't matter as both sets
     * are compared for equality.</p>
     *
     * @param userAnswer    the user's selected options in JSON array or object format
     * @param correctAnswer the correct options in similar formats
     * @return true if the user's selections exactly match the correct selections, false otherwise
     */
    private boolean validateMultipleChoiceAnswer(final String userAnswer, final String correctAnswer) {
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
     * Validates text-based answers for short answer and free text questions.
     *
     * <p>This method handles multiple input formats for user answers:</p>
     * <ul>
     *   <li>JSON array format: ["text answer"]</li>
     *   <li>JSON object format: {"answer": "text answer"}</li>
     *   <li>Plain text format: text answer</li>
     * </ul>
     *
     * <p>The correct answer is parsed similarly. The validation performs case-insensitive
     * string comparison after trimming whitespace and removing surrounding quotes.</p>
     *
     * @param userAnswer    the user's text answer in various JSON or plain text formats
     * @param correctAnswer the correct text answer in similar formats
     * @return true if the user's text matches the correct answer (case-insensitive), false otherwise
     */
    private boolean validateTextAnswer(final String userAnswer, final String correctAnswer) {
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
            } else if (userAnswer.startsWith("{")) {
                // Handle JSON object format
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

    /**
     * Retrieves a single user response by its unique identifier.
     *
     * <p>This method performs a read-only transaction to fetch a user response
     * from the database and convert it to a DTO for client consumption.</p>
     *
     * @param responseId the unique identifier of the user response to retrieve
     * @return UserResponseOutDTO containing the user response data
     * @throws ResourceNotFoundException if no user response exists with the given ID
     * @throws RuntimeException          if any unexpected error occurs during retrieval
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponseOutDTO getUserResponseById(final Long responseId) {
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

    /**
     * Updates an existing user response with new data.
     *
     * <p>This method retrieves the existing user response, applies the updates
     * from the DTO, and saves the modified entity back to the database.</p>
     *
     * <p>Note: This method does not re-validate answers or recalculate points.
     * It simply updates the provided fields in the existing response.</p>
     *
     * @param responseId              the unique identifier of the user response to update
     * @param userResponseUpdateInDTO the DTO containing the updated field values
     * @return UserResponseOutDTO containing the updated user response data
     * @throws ResourceNotFoundException if no user response exists with the given ID
     * @throws RuntimeException          if any unexpected error occurs during update
     */
    @Override
    public UserResponseOutDTO updateUserResponse(final Long responseId, final UserResponseUpdateInDTO userResponseUpdateInDTO) {
        log.info("Updating user response with ID: {}", responseId);

        try {
            UserResponse existingUserResponse = userResponseRepository.findById(responseId)
                    .orElseThrow(() -> {
                        log.warn("User response not found with ID: {} for update", responseId);
                        return new ResourceNotFoundException("User response not found with ID: " + responseId);
                    });

            // Update entity with new data
            UserResponse updatedUserResponse = userResponseConverter.updateEntityFromDTO(
                    existingUserResponse,
                    userResponseUpdateInDTO
            );

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

    /**
     * Deletes a user response by its unique identifier.
     *
     * <p>This method first verifies that the user response exists before attempting
     * to delete it. If the response doesn't exist, it throws a ResourceNotFoundException.</p>
     *
     * <p>Note: This operation is permanent and cannot be undone. Consider implementing
     * soft deletion if audit trails are required.</p>
     *
     * @param responseId the unique identifier of the user response to delete
     * @throws ResourceNotFoundException if no user response exists with the given ID
     * @throws RuntimeException          if any unexpected error occurs during deletion
     */
    @Override
    public void deleteUserResponse(final Long responseId) {
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

    /**
     * Retrieves all user responses with pagination support.
     *
     * @param pageable the pagination information including page number and size
     * @return a paginated list of user responses converted to DTOs
     * @throws RuntimeException if an unexpected error occurs during retrieval
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseOutDTO> getAllUserResponses(final Pageable pageable) {
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

    /**
     * Retrieves all user responses for a specific user.
     *
     * @param userId the unique identifier of the user
     * @return a list of user responses for the specified user converted to DTOs
     * @throws RuntimeException if an unexpected error occurs during retrieval
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserResponseOutDTO> getUserResponsesByUserId(final Long userId) {
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

    /**
     * Retrieves all user responses for a specific quiz.
     *
     * @param quizId the unique identifier of the quiz
     * @return a list of user responses for the specified quiz converted to DTOs
     * @throws RuntimeException if an unexpected error occurs during retrieval
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserResponseOutDTO> getUserResponsesByQuizId(final Long quizId) {
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

    /**
     * Retrieves all user responses for a specific user and quiz combination.
     *
     * @param userId the unique identifier of the user
     * @param quizId the unique identifier of the quiz
     * @return a list of user responses for the specified user and quiz converted to DTOs
     * @throws RuntimeException if an unexpected error occurs during retrieval
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserResponseOutDTO> getUserResponsesByUserIdAndQuizId(final Long userId, final Long quizId) {
        log.info("Fetching user responses for user ID: {} and quiz ID: {}", userId, quizId);

        try {
            List<UserResponse> userResponses = userResponseRepository.findByUserIdAndQuizId(userId, quizId);
            log.info("Found {} user responses for user ID: {} and quiz ID: {}", userResponses.size(), userId, quizId);

            return userResponseConverter.convertToOutDTOList(userResponses);

        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching user responses for user ID: {} and quiz ID: {}",
                    userId, quizId, e);
            throw new RuntimeException("Failed to fetch user responses", e);
        }
    }

    /**
     * Retrieves all user responses for a specific user, quiz, and attempt combination.
     *
     * @param userId  the unique identifier of the user
     * @param quizId  the unique identifier of the quiz
     * @param attempt the attempt number for the quiz
     * @return a list of user responses for the specified user, quiz, and attempt converted to DTOs
     * @throws RuntimeException if an unexpected error occurs during retrieval
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserResponseOutDTO> getUserResponsesByUserIdAndQuizIdAndAttempt(
            final Long userId,
            final Long quizId,
            final Long attempt
    ) {
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

    /**
     * Retrieves all user responses for a specific user with pagination support.
     *
     * @param userId   the unique identifier of the user
     * @param pageable the pagination information including page number and size
     * @return a paginated list of user responses for the specified user converted to DTOs
     * @throws RuntimeException if an unexpected error occurs during retrieval
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseOutDTO> getUserResponsesByUserId(final Long userId, final Pageable pageable) {
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

    /**
     * Retrieves all user responses for a specific quiz with pagination support.
     *
     * @param quizId   the unique identifier of the quiz
     * @param pageable the pagination information including page number and size
     * @return a paginated list of user responses for the specified quiz converted to DTOs
     * @throws RuntimeException if an unexpected error occurs during retrieval
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseOutDTO> getUserResponsesByQuizId(final Long quizId, final Pageable pageable) {
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

    /**
     * Calculates the total score for a specific user's quiz attempt.
     *
     * @param userId  the unique identifier of the user
     * @param quizId  the unique identifier of the quiz
     * @param attempt the attempt number for the quiz
     * @return the total score as a BigDecimal, or BigDecimal.ZERO if no score is found
     * @throws RuntimeException if an unexpected error occurs during calculation
     */
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalScore(final Long userId, final Long quizId, final Long attempt) {
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

    /**
     * Counts the number of correct answers for a specific user's quiz attempt.
     *
     * @param userId  the unique identifier of the user
     * @param quizId  the unique identifier of the quiz
     * @param attempt the attempt number for the quiz
     * @return the count of correct answers as a Long
     * @throws RuntimeException if an unexpected error occurs during counting
     */
    @Override
    @Transactional(readOnly = true)
    public Long countCorrectAnswers(final Long userId, final Long quizId, final Long attempt) {
        log.info("Counting correct answers for user ID: {}, quiz ID: {}, attempt: {}", userId, quizId, attempt);

        try {
            Long correctCount = userResponseRepository.countCorrectAnswersByUserIdAndQuizIdAndAttempt(userId, quizId, attempt);
            log.info("Correct answers count: {} for user ID: {}, quiz ID: {}, attempt: {}",
                    correctCount,
                    userId,
                    quizId,
                    attempt
            );
            return correctCount;

        } catch (Exception e) {
            log.error("Unexpected error occurred while counting correct answers for user ID: {}, quiz ID: {}, attempt: {}",
                    userId, quizId, attempt, e);
            throw new RuntimeException("Failed to count correct answers", e);
        }
    }

    /**
     * Retrieves the maximum attempt number for a specific user and quiz combination.
     *
     * @param userId the unique identifier of the user
     * @param quizId the unique identifier of the quiz
     * @return the maximum attempt number as a Long, or 0L if no attempts are found
     * @throws RuntimeException if an unexpected error occurs during retrieval
     */
    @Override
    @Transactional(readOnly = true)
    public Long getMaxAttemptNumber(final Long userId, final Long quizId) {
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
