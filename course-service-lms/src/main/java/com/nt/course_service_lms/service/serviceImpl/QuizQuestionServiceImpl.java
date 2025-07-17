//package com.example.course_service_lms.service.serviceImpl;
//
//import com.example.course_service_lms.converters.QuizQuestionConverter;
//import com.example.course_service_lms.dto.inDTO.QuizQuestionInDTO;
//import com.example.course_service_lms.dto.inDTO.QuizQuestionUpdateInDTO;
//import com.example.course_service_lms.dto.inDTO.UpdateQuizQuestionInDTO;
//import com.example.course_service_lms.dto.outDTO.QuizQuestionOutDTO;
//import com.example.course_service_lms.entity.QuizQuestion;
//import com.example.course_service_lms.exception.ResourceAlreadyExistsException;
//import com.example.course_service_lms.exception.ResourceNotFoundException;
//import com.example.course_service_lms.exception.ResourceNotValidException;
//import com.example.course_service_lms.repository.QuizQuestionRepository;
//import com.example.course_service_lms.repository.QuizRepository;
//import com.example.course_service_lms.service.QuizQuestionService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//import static com.example.course_service_lms.constants.QuizQuestionConstants.*;
//import static com.example.course_service_lms.converters.QuizQuestionConverter.convertToOutDTO;
//
///**
// * Implementation of QuizQuestionService for managing quiz questions.
// */
//@Service
//@RequiredArgsConstructor
//@Slf4j
//@Transactional
//public class QuizQuestionServiceImpl implements QuizQuestionService {
//
//    private final QuizQuestionRepository quizQuestionRepository;
//    private final QuizRepository quizRepository;
//    ObjectMapper objectMapper = new ObjectMapper();
//
//    @Override
//    public QuizQuestionOutDTO createQuestion(QuizQuestionInDTO questionInDTO) {
//        log.info("Creating new question for quiz ID: {}", questionInDTO.getQuizId());
//
//        // Validate quiz exists
//        validateQuizExists(questionInDTO.getQuizId());
//
//        // Validate question position is not already taken
//        //validateQuestionPosition(questionInDTO.getQuizId(), questionInDTO.getPosition(), null);
//
//        // Auto-assign the next available position
//        Integer nextPosition = getNextAvailablePosition(questionInDTO.getQuizId());
//        log.info("Auto-assigning position {} to new question for quiz ID: {}", nextPosition, questionInDTO.getQuizId());
//
//        // Validate question data
//        validateQuestionData(questionInDTO);
//
//        // Convert DTO to entity
//        QuizQuestion question = QuizQuestionConverter.convertToEntity(questionInDTO);
//        question.setPosition(nextPosition);
//        question.setCreatedAt(LocalDateTime.now());
//        question.setUpdatedAt(LocalDateTime.now());
//
//        // Save question
//        QuizQuestion savedQuestion = quizQuestionRepository.save(question);
//        log.info("Successfully created question with ID: {}", savedQuestion.getQuestionId());
//
//        return convertToOutDTO(savedQuestion);
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<QuizQuestionOutDTO> getAllQuestions() {
//        log.info("Retrieving all questions");
//
//        List<QuizQuestion> questions = quizQuestionRepository.findAll();
//        return questions.stream()
//                .map(QuizQuestionConverter::convertToOutDTO)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public List<QuizQuestionOutDTO> getQuestionsByQuizId(Long quizId) {
//        try {
//            log.info("Retrieving questions for quiz ID: {}", quizId);
//
//            // Validate quiz exists
//            validateQuizExists(quizId);
//
//            List<QuizQuestion> questions = quizQuestionRepository.findByQuizIdOrderByPosition(quizId);
//            if(questions.isEmpty()) {
//                throw new ResourceNotFoundException("No Questions Found");
//            }
//            return questions.stream()
//                    .map(QuizQuestionConverter::convertToOutDTO)
//                    .collect(Collectors.toList());
//        } catch (ResourceNotFoundException e) {
//            throw e;
//        }
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public QuizQuestionOutDTO getQuestionById(Long questionId) {
//        log.info("Retrieving question with ID: {}", questionId);
//
//        QuizQuestion question = findQuestionById(questionId);
//        return convertToOutDTO(question);
//    }
//
//    @Override
//    public QuizQuestionOutDTO updateQuestion(Long questionId, UpdateQuizQuestionInDTO questionInDTO) {
//        log.info("Updating question with ID: {} to position: {}", questionId, questionInDTO.getPosition());
//
//        // Find existing question
//        QuizQuestion existingQuestion = findQuestionById(questionId);
//        Long quizId = existingQuestion.getQuizId();
//        Integer oldPosition = existingQuestion.getPosition();
//        Integer newPosition = questionInDTO.getPosition();
//
//        // Validate question data
//        validateUpdateQuestionData(questionInDTO);
//
//        // Validate new position is valid (not beyond the max position + 1)
//        validatePositionRange(quizId, newPosition, questionId);
//
//        // Handle position reordering if position has changed
//        if (!oldPosition.equals(newPosition)) {
//            log.info("Position change detected: {} -> {}. Reordering questions...", oldPosition, newPosition);
//            reorderQuestionsForUpdate(quizId, questionId, oldPosition, newPosition);
//        }
//
//        // Update entity fields
//        updateQuestionFromUpdateDTO(existingQuestion, questionInDTO);
//        existingQuestion.setUpdatedAt(LocalDateTime.now());
//
//        // Save updated question
//        QuizQuestion updatedQuestion = quizQuestionRepository.save(existingQuestion);
//        log.info("Successfully updated question with ID: {} to position: {}", updatedQuestion.getQuestionId(), newPosition);
//
//        return convertToOutDTO(updatedQuestion);
//    }
//
//    @Override
//    public void deleteQuestion(Long questionId) {
//        log.info("Deleting question with ID: {}", questionId);
//
//        // Verify question exists
//        QuizQuestion question = findQuestionById(questionId);
//        Long quizId = question.getQuizId();
//        Integer deletedPosition = question.getPosition();
//
//        // Delete the question
//        quizQuestionRepository.delete(question);
//
//        // Reorder remaining questions to fill the gap
//        reorderQuestionsAfterDelete(quizId, deletedPosition);
//
//        log.info("Successfully deleted question with ID: {} and reordered remaining questions", questionId);
//    }
//
//    // Private helper methods
//
//    private Integer getNextAvailablePosition(Long quizId) {
//        List<QuizQuestion> existingQuestions = quizQuestionRepository.findByQuizIdOrderByPosition(quizId);
//
//        if (existingQuestions.isEmpty()) {
//            return 1; // First question
//        }
//
//        // Return the next position after the last question
//        Integer maxPosition = existingQuestions.stream()
//                .mapToInt(QuizQuestion::getPosition)
//                .max()
//                .orElse(0);
//
//        return maxPosition + 1;
//    }
//
//    private void validateQuizExists(Long quizId) {
//        if (!quizRepository.existsById(quizId)) {
//            throw new ResourceNotFoundException("Quiz not found with ID: " + quizId);
//        }
//    }
//
//
//    private void validatePositionRange(Long quizId, Integer newPosition, Long excludeQuestionId) {
//        // Get total count of questions for this quiz (excluding the current question being updated)
//        List<QuizQuestion> allQuestions = quizQuestionRepository.findByQuizIdOrderByPosition(quizId);
//        long totalQuestions = allQuestions.stream()
//                .filter(q -> !q.getQuestionId().equals(excludeQuestionId))
//                .count();
//
//        if (newPosition < 1 || newPosition > totalQuestions + 1) {
//            throw new ResourceNotValidException(
//                    String.format("Position must be between 1 and %d for quiz ID: %d", totalQuestions + 1, quizId));
//        }
//    }
//
//    private void reorderQuestionsForUpdate(Long quizId, Long questionId, Integer oldPosition, Integer newPosition) {
//        List<QuizQuestion> questions = quizQuestionRepository.findByQuizIdOrderByPosition(quizId);
//
//        // Filter out the question being updated
//        List<QuizQuestion> otherQuestions = questions.stream()
//                .filter(q -> !q.getQuestionId().equals(questionId))
//                .collect(Collectors.toList());
//
//        if (newPosition < oldPosition) {
//            // Moving up: shift questions down from newPosition to oldPosition-1
//            log.info("Moving question up from position {} to {}. Shifting questions down.", oldPosition, newPosition);
//
//            for (QuizQuestion question : otherQuestions) {
//                if (question.getPosition() >= newPosition && question.getPosition() < oldPosition) {
//                    question.setPosition(question.getPosition() + 1);
//                    question.setUpdatedAt(LocalDateTime.now());
//                    quizQuestionRepository.save(question);
//                    log.debug("Shifted question ID {} from position {} to {}",question.getQuestionId(), question.getPosition() - 1, question.getPosition());
//                }
//            }
//        } else {
//            // Moving down: shift questions up from oldPosition+1 to newPosition
//            log.info("Moving question down from position {} to {}. Shifting questions up.", oldPosition, newPosition);
//
//            for (QuizQuestion question : otherQuestions) {
//                if (question.getPosition() > oldPosition && question.getPosition() <= newPosition) {
//                    question.setPosition(question.getPosition() - 1);
//                    question.setUpdatedAt(LocalDateTime.now());
//                    quizQuestionRepository.save(question);
//                    log.debug("Shifted question ID {} from position {} to {}",question.getQuestionId(), question.getPosition() + 1, question.getPosition());
//                }
//            }
//        }
//    }
//
//    private void reorderQuestionsAfterDelete(Long quizId, Integer deletedPosition) {
//        List<QuizQuestion> questions = quizQuestionRepository.findByQuizIdOrderByPosition(quizId);
//
//        // Shift all questions with position > deletedPosition up by 1
//        for (QuizQuestion question : questions) {
//            if (question.getPosition() > deletedPosition) {
//                question.setPosition(question.getPosition() - 1);
//                question.setUpdatedAt(LocalDateTime.now());
//                quizQuestionRepository.save(question);
//                log.debug("Shifted question ID {} from position {} to {} after deletion",
//                        question.getQuestionId(), question.getPosition() + 1, question.getPosition());
//            }
//        }
//    }
//
//    private void validateQuestionData(QuizQuestionInDTO questionInDTO) {
//        // Validate question type specific requirements
//        String questionType = questionInDTO.getQuestionType();
//
//        if ("MULTIPLE_CHOICE".equals(questionType) &&
//                (questionInDTO.getOptions() == null || questionInDTO.getOptions().trim().isEmpty())) {
//            throw new ResourceNotValidException("Options are required for multiple choice questions");
//        }
//
//        if ("TRUE_FALSE".equals(questionType)) {
//            String correctAnswer = questionInDTO.getCorrectAnswer();
//            if (correctAnswer == null ||
//                    (!correctAnswer.equalsIgnoreCase("true") && !correctAnswer.equalsIgnoreCase("false"))) {
//                throw new ResourceNotValidException("Correct answer for true/false questions must be 'true' or 'false'");
//            }
//        }
//
//        // Validate points are reasonable
//        if (questionInDTO.getPoints().compareTo(new java.math.BigDecimal("0")) < 0) {
//            throw new ResourceNotValidException("Points cannot be negative");
//        }
//    }
//
//    private void validateUpdateQuestionData(UpdateQuizQuestionInDTO questionInDTO) {
//        // Validate question type specific requirements
//        String questionType = questionInDTO.getQuestionType();
//
//        if ("MCQ_SINGLE".equals(questionType) || "MCQ_MULTIPLE".equals(questionType)) {
//            if (questionInDTO.getOptions() == null || questionInDTO.getOptions().trim().isEmpty()) {
//                throw new ResourceNotValidException("Options are required for multiple choice questions");
//            }
//        }
//
//        // Validate points are reasonable (already validated by @DecimalMin annotation, but adding for completeness)
//        if (questionInDTO.getPoints().compareTo(new java.math.BigDecimal("0")) < 0) {
//            throw new ResourceNotValidException("Points cannot be negative");
//        }
//    }
//
//    private QuizQuestion findQuestionById(Long questionId) {
//        return quizQuestionRepository.findById(questionId)
//                .orElseThrow(() -> new ResourceNotFoundException("Question not found with ID: " + questionId));
//    }
//
//    private void updateQuestionFromUpdateDTO(QuizQuestion question, UpdateQuizQuestionInDTO dto) {
//        // Note: quizId is not updated since UpdateQuizQuestionInDTO doesn't contain it
//        // The existing question's quizId is preserved
//        question.setQuestionText(dto.getQuestionText());
//        question.setQuestionType(dto.getQuestionType());
//        question.setOptions(dto.getOptions());
//        question.setCorrectAnswer(dto.getCorrectAnswer());
//        question.setPoints(dto.getPoints());
//        question.setExplanation(dto.getExplanation());
//        question.setRequired(dto.getRequired());
//        question.setPosition(dto.getPosition());
//    }
//}

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
 * Implementation of QuizQuestionService for managing quiz questions.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class QuizQuestionServiceImpl implements QuizQuestionService {

    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizRepository quizRepository;
    private final ObjectMapper objectMapper;

    @Override
    public QuizQuestionOutDTO createQuestion(QuizQuestionInDTO questionInDTO) {
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

    @Override
    @Transactional(readOnly = true)
    public List<QuizQuestionOutDTO> getAllQuestions() {
        log.info("Retrieving all questions");

        List<QuizQuestion> questions = quizQuestionRepository.findAll();
        return questions.stream()
                .map(QuizQuestionConverter::convertToOutDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizQuestionOutDTO> getQuestionsByQuizId(Long quizId) {
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

    @Override
    @Transactional(readOnly = true)
    public QuizQuestionOutDTO getQuestionById(Long questionId) {
        log.info("Retrieving question with ID: {}", questionId);

        QuizQuestion question = findQuestionById(questionId);
        return convertToOutDTO(question);
    }

    @Override
    public QuizQuestionOutDTO updateQuestion(Long questionId, UpdateQuizQuestionInDTO questionInDTO) {
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

    @Override
    public void deleteQuestion(Long questionId) {
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

    private Integer getNextAvailablePosition(Long quizId) {
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

    private void validateQuizExists(Long quizId) {
        if (!quizRepository.existsById(quizId)) {
            throw new ResourceNotFoundException("Quiz not found with ID: " + quizId);
        }
    }

    private void validatePositionRange(Long quizId, Integer newPosition, Long excludeQuestionId) {
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

    private void reorderQuestionsForUpdate(Long quizId, Long questionId, Integer oldPosition, Integer newPosition) {
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
                    log.debug("Shifted question ID {} from position {} to {}", question.getQuestionId(), question.getPosition() - 1, question.getPosition());
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
                    log.debug("Shifted question ID {} from position {} to {}", question.getQuestionId(), question.getPosition() + 1, question.getPosition());
                }
            }
        }
    }

    private void reorderQuestionsAfterDelete(Long quizId, Integer deletedPosition) {
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

    private void validateQuestionData(QuizQuestionInDTO questionInDTO) {
        // Validate question type specific requirements
        String questionType = questionInDTO.getQuestionType();

        if (("MCQ_SINGLE".equals(questionType) || "MCQ_MULTIPLE".equals(questionType)) &&
                (questionInDTO.getOptions() == null || questionInDTO.getOptions().trim().isEmpty())) {
            throw new ResourceNotValidException("Options are required for multiple choice questions");
        }

        // Validate points are reasonable
        if (questionInDTO.getPoints().compareTo(new java.math.BigDecimal("0")) < 0) {
            throw new ResourceNotValidException("Points cannot be negative");
        }
    }

    private void validateUpdateQuestionData(UpdateQuizQuestionInDTO questionInDTO) {
        // Validate question type specific requirements
        String questionType = questionInDTO.getQuestionType();

        if (("MCQ_SINGLE".equals(questionType) || "MCQ_MULTIPLE".equals(questionType)) &&
                (questionInDTO.getOptions() == null || questionInDTO.getOptions().trim().isEmpty())) {
            throw new ResourceNotValidException("Options are required for multiple choice questions");
        }

        // Validate points are reasonable (already validated by @DecimalMin annotation, but adding for completeness)
        if (questionInDTO.getPoints().compareTo(new java.math.BigDecimal("0")) < 0) {
            throw new ResourceNotValidException("Points cannot be negative");
        }
    }

    private QuizQuestion findQuestionById(Long questionId) {
        return quizQuestionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with ID: " + questionId));
    }

    private void updateQuestionFromUpdateDTO(QuizQuestion question, UpdateQuizQuestionInDTO dto) {
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