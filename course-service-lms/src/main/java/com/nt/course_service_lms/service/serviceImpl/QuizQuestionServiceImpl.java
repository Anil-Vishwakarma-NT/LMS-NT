package com.nt.course_service_lms.service.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.course_service_lms.converters.QuizQuestionConverter;
import com.nt.course_service_lms.dto.inDTO.QuizQuestionInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateQuizQuestionInDTO;
import com.nt.course_service_lms.dto.outDTO.QuizQuestionOutDTO;
import com.nt.course_service_lms.entity.QuizQuestion;
import com.nt.course_service_lms.exception.ResourceNotFoundException;
import com.nt.course_service_lms.exception.ResourceNotValidException;
import com.nt.course_service_lms.repository.QuizQuestionRepository;
import com.nt.course_service_lms.repository.QuizRepository;
import com.nt.course_service_lms.service.QuizQuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.nt.course_service_lms.converters.QuizQuestionConverter.convertToOutDTO;

/**
 * Implementation of QuizQuestionService for managing quiz questions in the Learning Management System.
 *
 * <p>This service provides comprehensive CRUD operations for quiz questions with the following features:</p>
 * <ul>
 *   <li>Automatic position management for questions within a quiz</li>
 *   <li>JSON validation for options and correct answers</li>
 *   <li>Transactional operations for data consistency</li>
 *   <li>Position-based reordering when questions are updated or deleted</li>
 *   <li>Comprehensive validation for question data integrity</li>
 * </ul>
 *
 * <p>All operations are transactional and include proper logging for debugging and monitoring purposes.</p>
 *
 * @author Your Name
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class QuizQuestionServiceImpl implements QuizQuestionService {

    /**
     * Repository for quiz question data access operations.
     */
    private final QuizQuestionRepository quizQuestionRepository;

    /**
     * Repository for quiz data access operations - used for validation.
     */
    private final QuizRepository quizRepository;

    /**
     * ObjectMapper for JSON serialization/deserialization and validation.
     */
    private final ObjectMapper objectMapper;

    /**
     * Creates a new quiz question with automatic position assignment.
     *
     * <p>This method performs the following operations:</p>
     * <ul>
     *   <li>Validates that the parent quiz exists</li>
     *   <li>Auto-assigns the next available position in the quiz</li>
     *   <li>Validates question data including JSON format for options and correct answers</li>
     *   <li>Persists the question with created/updated timestamps</li>
     * </ul>
     *
     * @param questionInDTO the input data transfer object containing question details
     * @return QuizQuestionOutDTO the created question as an output DTO
     * @throws ResourceNotFoundException if the parent quiz is not found
     * @throws ResourceNotValidException if the question data is invalid or JSON format is incorrect
     */
    @Override
    public QuizQuestionOutDTO createQuestion(final QuizQuestionInDTO questionInDTO) {
        log.info("Creating new question for quiz ID: {}", questionInDTO.getQuizId());

        // Validate quiz exists
        validateQuizExists(questionInDTO.getQuizId());

        // Auto-assign the next available position
        Integer nextPosition = getNextAvailablePosition(questionInDTO.getQuizId());
        log.info("Auto-assigning position {} to new question for quiz ID: {}", nextPosition, questionInDTO.getQuizId());

        // Validate question data
        validateQuestionData(questionInDTO);

        // Convert DTO to entity
        QuizQuestion question = QuizQuestionConverter.convertToEntity(questionInDTO);
        question.setPosition(nextPosition);
        question.setCreatedAt(LocalDateTime.now());
        question.setUpdatedAt(LocalDateTime.now());

        // Handle JSON serialization for options and correctAnswer if they are JSON objects/arrays
        try {
            // If options is a JSON string, validate it
            if (question.getOptions() != null && !question.getOptions().trim().isEmpty()) {
                // Try to parse to validate JSON format
                objectMapper.readTree(question.getOptions());
            }

            // If correctAnswer is a JSON string, validate it
            if (question.getCorrectAnswer() != null && !question.getCorrectAnswer().trim().isEmpty()) {
                // Try to parse to validate JSON format
                objectMapper.readTree(question.getCorrectAnswer());
            }
        } catch (JsonProcessingException e) {
            log.error("Invalid JSON format in options or correctAnswer: {}", e.getMessage());
            throw new ResourceNotValidException("Invalid JSON format in options or correct answer");
        }

        // Save question
        QuizQuestion savedQuestion = quizQuestionRepository.save(question);
        log.info("Successfully created question with ID: {}", savedQuestion.getQuestionId());

        return convertToOutDTO(savedQuestion);
    }

    /**
     * Retrieves all quiz questions from the system.
     *
     * <p>This method returns all questions regardless of their parent quiz.
     * It's primarily used for administrative purposes or system-wide operations.</p>
     *
     * @return List&lt;QuizQuestionOutDTO&gt; list of all quiz questions as output DTOs
     */
    @Override
    @Transactional(readOnly = true)
    public List<QuizQuestionOutDTO> getAllQuestions() {
        log.info("Retrieving all questions");

        List<QuizQuestion> questions = quizQuestionRepository.findAll();
        return questions.stream()
                .map(QuizQuestionConverter::convertToOutDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all questions for a specific quiz, ordered by position.
     *
     * <p>This method validates that the quiz exists and returns questions in their proper order.
     * It's the primary method for displaying quiz questions to users.</p>
     *
     * @param quizId the ID of the quiz whose questions to retrieve
     * @return List&lt;QuizQuestionOutDTO&gt; list of questions for the specified quiz, ordered by position
     * @throws ResourceNotFoundException if the quiz is not found or no questions exist for the quiz
     */
    @Override
    @Transactional(readOnly = true)
    public List<QuizQuestionOutDTO> getQuestionsByQuizId(final Long quizId) {
        try {
            log.info("Retrieving questions for quiz ID: {}", quizId);

            // Validate quiz exists
            validateQuizExists(quizId);

            List<QuizQuestion> questions = quizQuestionRepository.findByQuizIdOrderByPosition(quizId);
            if (questions.isEmpty()) {
                throw new ResourceNotFoundException("No Questions Found");
            }
            return questions.stream()
                    .map(QuizQuestionConverter::convertToOutDTO)
                    .collect(Collectors.toList());
        } catch (ResourceNotFoundException e) {
            throw e;
        }
    }

    /**
     * Retrieves a specific quiz question by its ID.
     *
     * @param questionId the unique identifier of the question to retrieve
     * @return QuizQuestionOutDTO the question as an output DTO
     * @throws ResourceNotFoundException if no question exists with the given ID
     */
    @Override
    @Transactional(readOnly = true)
    public QuizQuestionOutDTO getQuestionById(final Long questionId) {
        log.info("Retrieving question with ID: {}", questionId);

        QuizQuestion question = findQuestionById(questionId);
        return convertToOutDTO(question);
    }

    /**
     * Updates an existing quiz question with new data and handles position reordering.
     *
     * <p>This method performs complex position management:</p>
     * <ul>
     *   <li>If the position changes, it reorders other questions accordingly</li>
     *   <li>Validates that the new position is within valid range</li>
     *   <li>Validates JSON format for updated options and correct answers</li>
     *   <li>Updates timestamps appropriately</li>
     * </ul>
     *
     * @param questionId    the ID of the question to update
     * @param questionInDTO the updated question data
     * @return QuizQuestionOutDTO the updated question as an output DTO
     * @throws ResourceNotFoundException if the question is not found
     * @throws ResourceNotValidException if the update data is invalid or position is out of range
     */
    @Override
    public QuizQuestionOutDTO updateQuestion(final Long questionId, final UpdateQuizQuestionInDTO questionInDTO) {
        log.info("Updating question with ID: {} to position: {}", questionId, questionInDTO.getPosition());

        // Find existing question
        QuizQuestion existingQuestion = findQuestionById(questionId);
        Long quizId = existingQuestion.getQuizId();
        Integer oldPosition = existingQuestion.getPosition();
        Integer newPosition = questionInDTO.getPosition();

        // Validate question data
        validateUpdateQuestionData(questionInDTO);

        // Validate new position is valid (not beyond the max position + 1)
        validatePositionRange(quizId, newPosition, questionId);

        // Handle position reordering if position has changed
        if (!oldPosition.equals(newPosition)) {
            log.info("Position change detected: {} -> {}. Reordering questions...", oldPosition, newPosition);
            reorderQuestionsForUpdate(quizId, questionId, oldPosition, newPosition);
        }

        // Update entity fields
        updateQuestionFromUpdateDTO(existingQuestion, questionInDTO);
        existingQuestion.setUpdatedAt(LocalDateTime.now());

        // Validate JSON format for updated data
        try {
            if (existingQuestion.getOptions() != null && !existingQuestion.getOptions().trim().isEmpty()) {
                objectMapper.readTree(existingQuestion.getOptions());
            }

            if (existingQuestion.getCorrectAnswer() != null && !existingQuestion.getCorrectAnswer().trim().isEmpty()) {
                objectMapper.readTree(existingQuestion.getCorrectAnswer());
            }
        } catch (JsonProcessingException e) {
            log.error("Invalid JSON format in updated options or correctAnswer: {}", e.getMessage());
            throw new ResourceNotValidException("Invalid JSON format in options or correct answer");
        }

        // Save updated question
        QuizQuestion updatedQuestion = quizQuestionRepository.save(existingQuestion);
        log.info("Successfully updated question with ID: {} to position: {}", updatedQuestion.getQuestionId(), newPosition);

        return convertToOutDTO(updatedQuestion);
    }

    /**
     * Deletes a quiz question and reorders remaining questions to fill the gap.
     *
     * <p>This method ensures that question positions remain sequential after deletion
     * by shifting all subsequent questions up by one position.</p>
     *
     * @param questionId the ID of the question to delete
     * @throws ResourceNotFoundException if the question is not found
     */
    @Override
    public void deleteQuestion(final Long questionId) {
        log.info("Deleting question with ID: {}", questionId);

        // Verify question exists
        QuizQuestion question = findQuestionById(questionId);
        Long quizId = question.getQuizId();
        Integer deletedPosition = question.getPosition();

        // Delete the question
        quizQuestionRepository.delete(question);

        // Reorder remaining questions to fill the gap
        reorderQuestionsAfterDelete(quizId, deletedPosition);

        log.info("Successfully deleted question with ID: {} and reordered remaining questions", questionId);
    }

    // Private helper methods

    /**
     * Calculates the next available position for a new question in a quiz.
     *
     * <p>This method finds the highest position number in the quiz and returns the next number.
     * If no questions exist in the quiz, it returns 1.</p>
     *
     * @param quizId the ID of the quiz to get the next position for
     * @return Integer the next available position number
     */
    private Integer getNextAvailablePosition(final Long quizId) {
        List<QuizQuestion> existingQuestions = quizQuestionRepository.findByQuizIdOrderByPosition(quizId);

        if (existingQuestions.isEmpty()) {
            return 1; // First question
        }

        // Return the next position after the last question
        Integer maxPosition = existingQuestions.stream()
                .mapToInt(QuizQuestion::getPosition)
                .max()
                .orElse(0);

        return maxPosition + 1;
    }

    /**
     * Validates that a quiz exists in the system.
     *
     * @param quizId the ID of the quiz to validate
     * @throws ResourceNotFoundException if the quiz does not exist
     */
    private void validateQuizExists(final Long quizId) {
        if (!quizRepository.existsById(quizId)) {
            throw new ResourceNotFoundException("Quiz not found with ID: " + quizId);
        }
    }

    /**
     * Validates that a position is within the valid range for a quiz.
     *
     * <p>Valid positions are from 1 to the total number of questions + 1 (to allow insertion).
     * The excludeQuestionId parameter is used when updating a question to exclude it from the count.</p>
     *
     * @param quizId            the ID of the quiz to validate position for
     * @param newPosition       the position to validate
     * @param excludeQuestionId the ID of a question to exclude from the count (for updates)
     * @throws ResourceNotValidException if the position is out of valid range
     */
    private void validatePositionRange(final Long quizId, final Integer newPosition, final Long excludeQuestionId) {
        // Get total count of questions for this quiz (excluding the current question being updated)
        List<QuizQuestion> allQuestions = quizQuestionRepository.findByQuizIdOrderByPosition(quizId);
        long totalQuestions = allQuestions.stream()
                .filter(q -> !q.getQuestionId().equals(excludeQuestionId))
                .count();

        if (newPosition < 1 || newPosition > totalQuestions + 1) {
            throw new ResourceNotValidException(
                    String.format("Position must be between 1 and %d for quiz ID: %d", totalQuestions + 1, quizId));
        }
    }

    /**
     * Reorders questions when a question's position is updated.
     *
     * <p>This method handles two scenarios:</p>
     * <ul>
     *   <li>Moving up: shifts questions down from newPosition to oldPosition-1</li>
     *   <li>Moving down: shifts questions up from oldPosition+1 to newPosition</li>
     * </ul>
     *
     * @param quizId      the ID of the quiz containing the questions
     * @param questionId  the ID of the question being moved (excluded from reordering)
     * @param oldPosition the current position of the question
     * @param newPosition the desired position of the question
     */
    private void reorderQuestionsForUpdate(
            final Long quizId,
            final Long questionId,
            final Integer oldPosition,
            final Integer newPosition
    ) {
        List<QuizQuestion> questions = quizQuestionRepository.findByQuizIdOrderByPosition(quizId);

        // Filter out the question being updated
        List<QuizQuestion> otherQuestions = questions.stream()
                .filter(q -> !q.getQuestionId().equals(questionId))
                .collect(Collectors.toList());

        if (newPosition < oldPosition) {
            // Moving up: shift questions down from newPosition to oldPosition-1
            log.info("Moving question up from position {} to {}. Shifting questions down.", oldPosition, newPosition);

            for (QuizQuestion question : otherQuestions) {
                if (question.getPosition() >= newPosition && question.getPosition() < oldPosition) {
                    question.setPosition(question.getPosition() + 1);
                    question.setUpdatedAt(LocalDateTime.now());
                    quizQuestionRepository.save(question);
                    log.debug("Shifted question ID {} from position {} to {}",
                            question.getQuestionId(),
                            question.getPosition() - 1,
                            question.getPosition()
                    );
                }
            }
        } else {
            // Moving down: shift questions up from oldPosition+1 to newPosition
            log.info("Moving question down from position {} to {}. Shifting questions up.", oldPosition, newPosition);

            for (QuizQuestion question : otherQuestions) {
                if (question.getPosition() > oldPosition && question.getPosition() <= newPosition) {
                    question.setPosition(question.getPosition() - 1);
                    question.setUpdatedAt(LocalDateTime.now());
                    quizQuestionRepository.save(question);
                    log.debug("Shifted question ID {} from position {} to {}",
                            question.getQuestionId(),
                            question.getPosition() + 1,
                            question.getPosition()
                    );
                }
            }
        }
    }

    /**
     * Reorders questions after a question has been deleted.
     *
     * <p>This method shifts all questions with positions greater than the deleted position
     * up by one position to maintain sequential numbering.</p>
     *
     * @param quizId          the ID of the quiz containing the remaining questions
     * @param deletedPosition the position of the deleted question
     */
    private void reorderQuestionsAfterDelete(final Long quizId, final Integer deletedPosition) {
        List<QuizQuestion> questions = quizQuestionRepository.findByQuizIdOrderByPosition(quizId);

        // Shift all questions with position > deletedPosition up by 1
        for (QuizQuestion question : questions) {
            if (question.getPosition() > deletedPosition) {
                question.setPosition(question.getPosition() - 1);
                question.setUpdatedAt(LocalDateTime.now());
                quizQuestionRepository.save(question);
                log.debug("Shifted question ID {} from position {} to {} after deletion",
                        question.getQuestionId(), question.getPosition() + 1, question.getPosition());
            }
        }
    }

    /**
     * Validates question data for creation operations.
     *
     * <p>This method performs the following validations:</p>
     * <ul>
     *   <li>Ensures options are provided for multiple choice questions</li>
     *   <li>Validates that points are not negative</li>
     * </ul>
     *
     * @param questionInDTO the question data to validate
     * @throws ResourceNotValidException if validation fails
     */
    private void validateQuestionData(final QuizQuestionInDTO questionInDTO) {
        // Validate question type specific requirements
        String questionType = questionInDTO.getQuestionType();

        if (("MCQ_SINGLE".equals(questionType) || "MCQ_MULTIPLE".equals(questionType))
                && (questionInDTO.getOptions() == null || questionInDTO.getOptions().trim().isEmpty())) {
            throw new ResourceNotValidException("Options are required for multiple choice questions");
        }

        // Validate points are reasonable
        if (questionInDTO.getPoints().compareTo(new java.math.BigDecimal("0")) < 0) {
            throw new ResourceNotValidException("Points cannot be negative");
        }
    }

    /**
     * Validates question data for update operations.
     *
     * <p>This method performs the same validations as {@link #validateQuestionData(QuizQuestionInDTO)}
     * but for update DTOs.</p>
     *
     * @param questionInDTO the question update data to validate
     * @throws ResourceNotValidException if validation fails
     */
    private void validateUpdateQuestionData(final UpdateQuizQuestionInDTO questionInDTO) {
        // Validate question type specific requirements
        String questionType = questionInDTO.getQuestionType();

        if (("MCQ_SINGLE".equals(questionType) || "MCQ_MULTIPLE".equals(questionType))
                && (questionInDTO.getOptions() == null || questionInDTO.getOptions().trim().isEmpty())) {
            throw new ResourceNotValidException("Options are required for multiple choice questions");
        }

        // Validate points are reasonable (already validated by @DecimalMin annotation, but adding for completeness)
        if (questionInDTO.getPoints().compareTo(new java.math.BigDecimal("0")) < 0) {
            throw new ResourceNotValidException("Points cannot be negative");
        }
    }

    /**
     * Finds a quiz question by its ID.
     *
     * @param questionId the ID of the question to find
     * @return QuizQuestion the found question entity
     * @throws ResourceNotFoundException if no question exists with the given ID
     */
    private QuizQuestion findQuestionById(final Long questionId) {
        return quizQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with ID: " + questionId));
    }

    /**
     * Updates a QuizQuestion entity with data from an UpdateQuizQuestionInDTO.
     *
     * <p>This method preserves the existing quizId and updates all other fields
     * from the provided DTO. The updated timestamp is set separately.</p>
     *
     * @param question the question entity to update
     * @param dto      the DTO containing the updated data
     */
    private void updateQuestionFromUpdateDTO(final QuizQuestion question, final UpdateQuizQuestionInDTO dto) {
        // Note: quizId is not updated since UpdateQuizQuestionInDTO doesn't contain it
        // The existing question's quizId is preserved
        question.setQuestionText(dto.getQuestionText());
        question.setQuestionType(dto.getQuestionType());
        question.setOptions(dto.getOptions());
        question.setCorrectAnswer(dto.getCorrectAnswer());
        question.setPoints(dto.getPoints());
        question.setExplanation(dto.getExplanation());
        question.setRequired(dto.getRequired());
        question.setPosition(dto.getPosition());
    }
}
