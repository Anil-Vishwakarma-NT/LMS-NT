package com.nt.course_service_lms.service.serviceImpl;

import com.nt.course_service_lms.converters.QuizConverter;
import com.nt.course_service_lms.dto.inDTO.QuizCreateInDTO;
import com.nt.course_service_lms.dto.inDTO.QuizUpdateInDTO;
import com.nt.course_service_lms.dto.outDTO.QuizOutDTO;
import com.nt.course_service_lms.entity.Quiz;
import com.nt.course_service_lms.exception.ResourceAlreadyExistsException;
import com.nt.course_service_lms.exception.ResourceNotFoundException;
import com.nt.course_service_lms.repository.CourseContentRepository;
import com.nt.course_service_lms.repository.CourseRepository;
import com.nt.course_service_lms.repository.QuizRepository;
import com.nt.course_service_lms.service.QuizService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.nt.course_service_lms.constants.QuizConstants.GENERAL_ERROR;
import static com.nt.course_service_lms.constants.QuizConstants.NO_QUIZ_FOR_COURSE_CONTENT;
import static com.nt.course_service_lms.constants.QuizConstants.NO_QUIZ_FOR_COURSE_ID;
import static com.nt.course_service_lms.constants.QuizConstants.NO_QUIZ_FOUND;
import static com.nt.course_service_lms.constants.QuizConstants.NO_QUIZ_WITH_ID;
import static com.nt.course_service_lms.constants.QuizConstants.QUIZ_EXISTS;


/**
 * Implementation of the {@link QuizService} interface that manages quiz-related operations.
 * Provides functionality for creating, updating, deleting, and retrieving quizzes.
 * Handles validation, error handling, and logging for these operations.
 *
 * @see QuizService
 */
@Service
@Slf4j
public class QuizServiceImpl implements QuizService {

    /**
     * The repository responsible for performing CRUD operations on the {@link Quiz} entity.
     */
    @Autowired
    private QuizRepository quizRepository;

    /**
     * Converter for handling Quiz entity and DTO conversions.
     */
    @Autowired
    private QuizConverter quizConverter;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseContentRepository courseContentRepository;

    /**
     * Creates a new quiz based on the provided DTO.
     * <p>
     * Checks for duplicate quiz title within the same parent before saving the new quiz.
     * </p>
     *
     * @param quizCreateInDTO the DTO containing the quiz details
     * @return the created {@link QuizOutDTO}
     * @throws ResourceAlreadyExistsException if a quiz with the same title already exists for the parent
     * @throws RuntimeException               if there is a general error during quiz creation
     */
    @Override
    public QuizOutDTO createQuiz(final QuizCreateInDTO quizCreateInDTO) {
        try {
            log.info("Attempting to create a new quiz: {}", quizCreateInDTO.getTitle());
            String parentType = quizCreateInDTO.getParentType();
            Long parentId = quizCreateInDTO.getParentId();

            switch (parentType) {
                case "course":
                    if (!courseRepository.existsById(parentId)) {
                        throw new ResourceNotFoundException("Course Not Found");
                    }
                    break;

                case "course-content":
                    if (!courseContentRepository.existsById(parentId)) {
                        throw new ResourceNotFoundException("Course Content Not Found");
                    }
                    break;
            }

            // Check for duplicate quiz title within the same parent
            if (quizRepository.existsByTitleAndParentTypeAndParentId(
                    quizCreateInDTO.getTitle(),
                    quizCreateInDTO.getParentType(),
                    quizCreateInDTO.getParentId())) {
                log.error("Quiz with title '{}' already exists for parent type '{}' and parent ID '{}'", quizCreateInDTO.getTitle(), quizCreateInDTO.getParentType(), quizCreateInDTO.getParentId());
                throw new ResourceAlreadyExistsException(String.format(QUIZ_EXISTS, quizCreateInDTO.getTitle()));
            }

            // Convert DTO to Entity using converter
            Quiz quiz = quizConverter.toEntity(quizCreateInDTO);

            // Save quiz entity
            Quiz savedQuiz = quizRepository.save(quiz);
            log.info("Quiz '{}' created successfully with ID: {}", savedQuiz.getTitle(), savedQuiz.getQuizId());

            // Convert entity to output DTO
            return quizConverter.toOutDTO(savedQuiz);
        } catch (ResourceNotFoundException | ResourceAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error creating quiz: {}", e.getMessage(), e);
            throw new RuntimeException(GENERAL_ERROR, e);
        }
    }

    /**
     * Retrieves all available active quizzes.
     *
     * @return a list of all active {@link QuizOutDTO}
     * @throws ResourceNotFoundException if no quizzes are found in the system
     */
    @Override
    public List<QuizOutDTO> getAllQuizzes() {
        try {
            log.info("Fetching all active quizzes");

            List<Quiz> quizzes = quizRepository.findByIsActiveTrue();

            if (quizzes.isEmpty()) {
                log.warn("No active quizzes found in the system");
                throw new ResourceNotFoundException(NO_QUIZ_FOUND);
            }

            log.info("Successfully retrieved {} active quizzes", quizzes.size());

            // Convert entities to output DTOs
            return quizzes.stream()
                    .map(quizConverter::toOutDTO)
                    .collect(Collectors.toList());
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching all quizzes: {}", e.getMessage(), e);
            throw new RuntimeException(GENERAL_ERROR, e);
        }
    }

    /**
     * Retrieves a quiz by its ID.
     *
     * @param quizId the ID of the quiz to retrieve
     * @return the {@link QuizOutDTO} if found
     * @throws ResourceNotFoundException if quiz not found
     */
    @Override
    public QuizOutDTO getQuizById(final Long quizId) {
        try {
            log.info("Fetching quiz with ID: {}", quizId);

            Quiz quiz = quizRepository.findById(quizId)
                    .orElseThrow(() -> {
                        log.warn("Quiz with ID {} not found", quizId);
                        return new ResourceNotFoundException(String.format(NO_QUIZ_WITH_ID, quizId));
                    });

            log.info("Successfully retrieved quiz: {}", quiz.getTitle());

            // Convert entity to output DTO
            return quizConverter.toOutDTO(quiz);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching quiz with ID {}: {}", quizId, e.getMessage(), e);
            throw new RuntimeException(GENERAL_ERROR, e);
        }
    }

    /**
     * Retrieves all quizzes for a specific course.
     *
     * @param courseId the ID of the course
     * @return a list of {@link QuizOutDTO} for the course
     */
    @Override
    public List<QuizOutDTO> getQuizzesByCourse(final Long courseId) {
        try {
            log.info("Fetching quizzes for course ID: {}", courseId);

            List<Quiz> quizzes = quizRepository.findByParentTypeAndParentIdAndIsActiveTrue("course", courseId);

            if (quizzes.isEmpty()) {
                log.warn("No active quizzes found for course ID: {}", courseId);
                throw new ResourceNotFoundException(String.format(NO_QUIZ_FOR_COURSE_ID, courseId));
            }

            log.info("Successfully retrieved {} quizzes for course ID: {}", quizzes.size(), courseId);

            return quizzes.stream()
                    .map(quizConverter::toOutDTO)
                    .collect(Collectors.toList());
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching quizzes for course ID {}: {}", courseId, e.getMessage(), e);
            throw new RuntimeException(GENERAL_ERROR, e);
        }
    }

    /**
     * Retrieves all quizzes for a specific course content.
     *
     * @param courseContentId the ID of the course content
     * @return a list of {@link QuizOutDTO} for the course content
     */
    @Override
    public List<QuizOutDTO> getQuizzesByCourseContent(final Long courseContentId) {
        try {
            log.info("Fetching quizzes for course content ID: {}", courseContentId);

            List<Quiz> quizzes = quizRepository.findByParentTypeAndParentIdAndIsActiveTrue("course-content", courseContentId);

            if (quizzes.isEmpty()) {
                log.warn("No active quizzes found for course content ID: {}", courseContentId);
                throw new ResourceNotFoundException(String.format(NO_QUIZ_FOR_COURSE_CONTENT, courseContentId));
            }

            log.info("Successfully retrieved {} quizzes for course content ID: {}", quizzes.size(), courseContentId);

            return quizzes.stream()
                    .map(quizConverter::toOutDTO)
                    .collect(Collectors.toList());
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching quizzes for course content ID {}: {}", courseContentId, e.getMessage(), e);
            throw new RuntimeException(GENERAL_ERROR, e);
        }
    }

    /**
     * Updates the quiz with the given ID using the provided DTO.
     * <p>
     * Validates that the new quiz title is unique within the same parent (excluding the same quiz) before updating.
     * </p>
     *
     * @param quizId          the ID of the quiz to update
     * @param quizUpdateInDTO the DTO containing the updated quiz data
     * @return the updated {@link QuizOutDTO}
     * @throws ResourceNotFoundException      if the quiz with the specified ID does not exist
     * @throws ResourceAlreadyExistsException if the new quiz title already exists for the same parent
     * @throws RuntimeException               if there is a general error during the update
     */
    @Override
    public QuizOutDTO updateQuiz(final Long quizId, final QuizUpdateInDTO quizUpdateInDTO) {
        try {
            log.info("Attempting to update quiz with ID: {}", quizId);

            // Check if the quiz exists
            Quiz existingQuiz = quizRepository.findById(quizId).orElseThrow(() -> {
                log.error("Quiz with ID {} not found", quizId);
                return new ResourceNotFoundException(String.format(NO_QUIZ_FOUND, quizId));
            });

            // Validate if the updated title is unique within the same parent (excluding the same quiz)
            if (quizUpdateInDTO.getTitle() != null &&
                    !existingQuiz.getTitle().equalsIgnoreCase(quizUpdateInDTO.getTitle()) &&
                    quizRepository.existsByTitleAndParentTypeAndParentId(
                            quizUpdateInDTO.getTitle(),
                            existingQuiz.getParentType(),
                            existingQuiz.getParentId())) {
                log.error("Quiz with title '{}' already exists for parent type '{}' and parent ID '{}'", quizUpdateInDTO.getTitle(), existingQuiz.getParentType(), existingQuiz.getParentId());
                throw new ResourceAlreadyExistsException(String.format(QUIZ_EXISTS, quizUpdateInDTO.getTitle()));
            }

            // Update the quiz entity using converter
            Quiz updatedQuiz = quizConverter.updateEntity(existingQuiz, quizUpdateInDTO);
            Quiz savedQuiz = quizRepository.save(updatedQuiz);

            // Convert the saved quiz to QuizOutDTO
            QuizOutDTO quizOutDTO = quizConverter.toOutDTO(savedQuiz);

            log.info("Successfully updated quiz with ID: {}", savedQuiz.getQuizId());
            return quizOutDTO;

        } catch (ResourceNotFoundException | ResourceAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating quiz with ID {}: {}", quizId, e.getMessage(), e);
            throw new RuntimeException(GENERAL_ERROR, e);
        }
    }

    /**
     * Soft deletes the quiz with the specified ID by setting isActive to false.
     *
     * @param id the ID of the quiz to delete
     * @throws ResourceNotFoundException if the quiz with the specified ID does not exist
     * @throws RuntimeException          if there is a general error during the deletion
     */
    @Override
    public void deleteQuiz(final Long id) {
        try {
            log.info("Attempting to soft delete quiz with ID: {}", id);

            // Check if the quiz exists
            Quiz existingQuiz = quizRepository.findById(id).orElseThrow(() -> {
                log.error("Quiz with ID {} not found", id);
                return new ResourceNotFoundException(String.format(NO_QUIZ_FOUND, id));
            });

            // Soft delete the quiz by setting isActive to false
            existingQuiz.setIsActive(false);
            quizRepository.save(existingQuiz);
            log.info("Successfully soft deleted quiz with ID: {}", id);

        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error soft deleting quiz with ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException(GENERAL_ERROR, e);
        }
    }
}