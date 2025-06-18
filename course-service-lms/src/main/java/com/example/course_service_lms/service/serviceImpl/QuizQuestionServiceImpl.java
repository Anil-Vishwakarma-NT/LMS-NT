package com.example.course_service_lms.service.serviceImpl;

import com.example.course_service_lms.converters.QuizQuestionConverter;
import com.example.course_service_lms.dto.inDTO.QuizQuestionInDTO;
import com.example.course_service_lms.dto.inDTO.QuizQuestionUpdateInDTO;
import com.example.course_service_lms.dto.inDTO.UpdateQuizQuestionInDTO;
import com.example.course_service_lms.dto.outDTO.QuizQuestionOutDTO;
import com.example.course_service_lms.entity.QuizQuestion;
import com.example.course_service_lms.exception.ResourceAlreadyExistsException;
import com.example.course_service_lms.exception.ResourceNotFoundException;
import com.example.course_service_lms.exception.ResourceNotValidException;
import com.example.course_service_lms.repository.QuizQuestionRepository;
import com.example.course_service_lms.repository.QuizRepository;
import com.example.course_service_lms.service.QuizQuestionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.course_service_lms.constants.QuizQuestionConstants.*;

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
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public QuizQuestionOutDTO createQuestion(QuizQuestionInDTO questionInDTO) {
        log.info("Creating new question for quiz ID: {}", questionInDTO.getQuizId());

        // Validate quiz exists
        validateQuizExists(questionInDTO.getQuizId());

        // Validate question position is not already taken
        validateQuestionPosition(questionInDTO.getQuizId(), questionInDTO.getPosition(), null);

        // Validate question data
        validateQuestionData(questionInDTO);

        // Convert DTO to entity
        QuizQuestion question = convertToEntity(questionInDTO);
        question.setCreatedAt(LocalDateTime.now());
        question.setUpdatedAt(LocalDateTime.now());

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
                .map(this::convertToOutDTO)
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
            if(questions.isEmpty()) {
                throw new ResourceNotFoundException("No Questions Found");
            }
            return questions.stream()
                    .map(this::convertToOutDTO)
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
        log.info("Updating question with ID: {}", questionId);

        // Find existing question
        QuizQuestion existingQuestion = findQuestionById(questionId);

        // Use the existing question's quiz ID for validations
        Long quizId = existingQuestion.getQuizId();

        // Validate question position (exclude current question from position check)
        validateQuestionPosition(quizId, questionInDTO.getPosition(), questionId);

        // Validate question data
        validateUpdateQuestionData(questionInDTO);

        // Update entity fields (preserving the existing quizId)
        updateQuestionFromUpdateDTO(existingQuestion, questionInDTO);
        existingQuestion.setUpdatedAt(LocalDateTime.now());

        // Save updated question
        QuizQuestion updatedQuestion = quizQuestionRepository.save(existingQuestion);
        log.info("Successfully updated question with ID: {}", updatedQuestion.getQuestionId());

        return convertToOutDTO(updatedQuestion);
    }

    @Override
    public void deleteQuestion(Long questionId) {
        log.info("Deleting question with ID: {}", questionId);

        // Verify question exists
        QuizQuestion question = findQuestionById(questionId);

        // Delete the question
        quizQuestionRepository.delete(question);
        log.info("Successfully deleted question with ID: {}", questionId);
    }

    // Private helper methods

    private void validateQuizExists(Long quizId) {
        if (!quizRepository.existsById(quizId)) {
            throw new ResourceNotFoundException("Quiz not found with ID: " + quizId);
        }
    }

    private void validateQuestionPosition(Long quizId, Integer position, Long excludeQuestionId) {
        Optional<QuizQuestion> existingQuestion = quizQuestionRepository.findByQuizIdAndPosition(quizId, position);

        if (existingQuestion.isPresent() &&
                (excludeQuestionId == null || !existingQuestion.get().getQuestionId().equals(excludeQuestionId))) {
            throw new ResourceAlreadyExistsException(
                    "Question already exists at position " + position + " for quiz ID: " + quizId);
        }
    }

    private void validateQuestionData(QuizQuestionInDTO questionInDTO) {
        // Validate question type specific requirements
        String questionType = questionInDTO.getQuestionType();

        if ("MULTIPLE_CHOICE".equals(questionType) &&
                (questionInDTO.getOptions() == null || questionInDTO.getOptions().trim().isEmpty())) {
            throw new ResourceNotValidException("Options are required for multiple choice questions");
        }

        if ("TRUE_FALSE".equals(questionType)) {
            String correctAnswer = questionInDTO.getCorrectAnswer();
            if (correctAnswer == null ||
                    (!correctAnswer.equalsIgnoreCase("true") && !correctAnswer.equalsIgnoreCase("false"))) {
                throw new ResourceNotValidException("Correct answer for true/false questions must be 'true' or 'false'");
            }
        }

        // Validate points are reasonable
        if (questionInDTO.getPoints().compareTo(new java.math.BigDecimal("0")) < 0) {
            throw new ResourceNotValidException("Points cannot be negative");
        }
    }

    private void validateUpdateQuestionData(UpdateQuizQuestionInDTO questionInDTO) {
        // Validate question type specific requirements
        String questionType = questionInDTO.getQuestionType();

        if ("MCQ_SINGLE".equals(questionType) || "MCQ_MULTIPLE".equals(questionType)) {
            if (questionInDTO.getOptions() == null || questionInDTO.getOptions().trim().isEmpty()) {
                throw new ResourceNotValidException("Options are required for multiple choice questions");
            }
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

    private QuizQuestion convertToEntity(QuizQuestionInDTO dto) {
        QuizQuestion question = new QuizQuestion();
        question.setQuizId(dto.getQuizId());
        question.setQuestionText(dto.getQuestionText());
        question.setQuestionType(dto.getQuestionType());
        question.setOptions(dto.getOptions());
        question.setCorrectAnswer(dto.getCorrectAnswer());
        question.setPoints(dto.getPoints());
        question.setExplanation(dto.getExplanation());
        question.setRequired(dto.getRequired());
        question.setPosition(dto.getPosition());
        return question;
    }

    private void updateQuestionFromDTO(QuizQuestion question, QuizQuestionInDTO dto) {
        question.setQuizId(dto.getQuizId());
        question.setQuestionText(dto.getQuestionText());
        question.setQuestionType(dto.getQuestionType());
        question.setOptions(dto.getOptions());
        question.setCorrectAnswer(dto.getCorrectAnswer());
        question.setPoints(dto.getPoints());
        question.setExplanation(dto.getExplanation());
        question.setRequired(dto.getRequired());
        question.setPosition(dto.getPosition());
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

    private QuizQuestionOutDTO convertToOutDTO(QuizQuestion question) {
        return new QuizQuestionOutDTO(
                question.getQuestionId(),
                question.getQuizId(),
                question.getQuestionText(),
                question.getQuestionType(),
                question.getOptions(),
                question.getCorrectAnswer(),
                question.getPoints(),
                question.getExplanation(),
                question.getRequired(),
                question.getPosition(),
                question.getCreatedAt(),
                question.getUpdatedAt()
        );
    }
}