package com.example.course_service_lms.service.serviceImpl;

import com.example.course_service_lms.converters.QuizQuestionConverter;
import com.example.course_service_lms.dto.inDTO.QuizQuestionCreateInDTO;
import com.example.course_service_lms.dto.inDTO.QuizQuestionUpdateInDTO;
import com.example.course_service_lms.dto.outDTO.QuizQuestionOutDTO;
import com.example.course_service_lms.entity.QuizQuestion;
import com.example.course_service_lms.exception.ResourceAlreadyExistsException;
import com.example.course_service_lms.exception.ResourceNotFoundException;
import com.example.course_service_lms.repository.QuizQuestionRepository;
import com.example.course_service_lms.service.QuizQuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import static com.example.course_service_lms.constants.QuizQuestionConstants.*;

/**
 * Implementation of the QuizQuestionService interface.
 *
 * <p>This service handles all business logic related to quiz question operations
 * including creation, retrieval, updating, and deletion.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QuizQuestionServiceImpl implements QuizQuestionService {

    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizQuestionConverter quizQuestionConverter;


    @Override
    public QuizQuestionOutDTO createQuestion(final QuizQuestionCreateInDTO questionCreateInDTO) {
        try {
            log.info("Attempting to create a new question for quiz ID: {}", questionCreateInDTO.getQuizId());

            // Check if question with same position already exists in the quiz
            if (quizQuestionRepository.existsByQuizIdAndPosition(
                    questionCreateInDTO.getQuizId(),
                    questionCreateInDTO.getPosition())) {
                log.error("Question with position '{}' already exists in quiz '{}'",
                        questionCreateInDTO.getPosition(), questionCreateInDTO.getQuizId());
                throw new ResourceAlreadyExistsException(
                        String.format(POSITION_EXISTS, questionCreateInDTO.getPosition(), questionCreateInDTO.getQuizId()));
            }

            // Convert DTO to Entity using converter
            QuizQuestion question = quizQuestionConverter.convertCreateInDTOToEntity(questionCreateInDTO);

            // Save question entity
            QuizQuestion savedQuestion = quizQuestionRepository.save(question);
            log.info("Question created successfully with ID: {} for quiz ID: {}",
                    savedQuestion.getQuestionId(), savedQuestion.getQuizId());

            // Convert entity to output DTO
            return quizQuestionConverter.convertEntityToOutDTO(savedQuestion);
        } catch (ResourceAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error creating question for quiz ID {}: {}", questionCreateInDTO.getQuizId(), e.getMessage(), e);
            throw new RuntimeException(GENERAL_ERROR, e);
        }
    }

    @Override
    public List<QuizQuestionOutDTO> getAllQuestionsByQuizId(final Integer quizId) {
        try {
            log.info("Retrieving all questions for quiz ID: {}", quizId);

            List<QuizQuestion> questions = quizQuestionRepository.findByQuizIdOrderByPosition(quizId);

            if (questions.isEmpty()) {
                log.info("No questions found for quiz ID: {}", quizId);
            } else {
                log.info("Found {} questions for quiz ID: {}", questions.size(), quizId);
            }

            return quizQuestionConverter.convertEntityListToOutDTOList(questions);
        } catch (Exception e) {
            log.error("Error retrieving questions for quiz ID {}: {}", quizId, e.getMessage(), e);
            throw new RuntimeException(GENERAL_ERROR, e);
        }
    }

    @Override
    public QuizQuestionOutDTO getQuestionById(final Integer questionId) {
        try {
            log.info("Retrieving question with ID: {}", questionId);

            QuizQuestion question = quizQuestionRepository.findById(questionId)
                    .orElseThrow(() -> {
                        log.error("Question not found with ID: {}", questionId);
                        return new ResourceNotFoundException(String.format(QUESTION_NOT_FOUND, questionId));
                    });

            log.info("Question retrieved successfully with ID: {}", questionId);
            return quizQuestionConverter.convertEntityToOutDTO(question);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error retrieving question with ID {}: {}", questionId, e.getMessage(), e);
            throw new RuntimeException(GENERAL_ERROR, e);
        }
    }

    @Override
    public QuizQuestionOutDTO updateQuestion(final Integer questionId, final QuizQuestionUpdateInDTO questionUpdateInDTO) {
        try {
            log.info("Attempting to update question with ID: {}", questionId);

            // Find existing question
            QuizQuestion existingQuestion = quizQuestionRepository.findById(questionId)
                    .orElseThrow(() -> {
                        log.error("Question not found with ID: {}", questionId);
                        return new ResourceNotFoundException(String.format(QUESTION_NOT_FOUND, questionId));
                    });

            // Check if position is being changed and if new position already exists
            if (!existingQuestion.getPosition().equals(questionUpdateInDTO.getPosition())) {
                if (quizQuestionRepository.existsByQuizIdAndPositionAndQuestionIdNot(
                        existingQuestion.getQuizId(),
                        questionUpdateInDTO.getPosition(),
                        questionId)) {
                    log.error("Question with position '{}' already exists in quiz '{}'",
                            questionUpdateInDTO.getPosition(), existingQuestion.getQuizId());
                    throw new ResourceAlreadyExistsException(
                            String.format(POSITION_EXISTS, questionUpdateInDTO.getPosition(), existingQuestion.getQuizId()));
                }
            }

            // Update entity using converter
            QuizQuestion updatedQuestion = quizQuestionConverter.updateEntityFromUpdateInDTO(existingQuestion, questionUpdateInDTO);

            // Save updated entity
            QuizQuestion savedQuestion = quizQuestionRepository.save(updatedQuestion);
            log.info("Question updated successfully with ID: {}", savedQuestion.getQuestionId());

            // Convert entity to output DTO
            return quizQuestionConverter.convertEntityToOutDTO(savedQuestion);
        } catch (ResourceNotFoundException | ResourceAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating question with ID {}: {}", questionId, e.getMessage(), e);
            throw new RuntimeException(GENERAL_ERROR, e);
        }
    }

    @Override
    public void deleteQuestion(final Integer questionId) {
        try {
            log.info("Attempting to delete question with ID: {}", questionId);

            // Check if question exists
            if (!quizQuestionRepository.existsById(questionId)) {
                log.error("Question not found with ID: {}", questionId);
                throw new ResourceNotFoundException(String.format(QUESTION_NOT_FOUND, questionId));
            }

            // Delete the question
            quizQuestionRepository.deleteById(questionId);
            log.info("Question deleted successfully with ID: {}", questionId);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error deleting question with ID {}: {}", questionId, e.getMessage(), e);
            throw new RuntimeException(GENERAL_ERROR, e);
        }
    }
}
