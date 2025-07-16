package com.nt.course_service_lms.service;

import com.nt.course_service_lms.dto.inDTO.QuizCreateInDTO;
import com.nt.course_service_lms.dto.inDTO.QuizUpdateInDTO;
import com.nt.course_service_lms.dto.outDTO.QuizOutDTO;

import java.util.List;

/**
 * Service interface for handling business logic related to Quiz entities.
 * <p>
 * Provides methods for creating, retrieving, updating, and deleting quizzes,
 * as well as checking for their existence by ID.
 * </p>
 */
public interface QuizService {

    /**
     * Creates a new quiz based on the provided {@link QuizCreateInDTO}.
     *
     * @param quizCreateInDTO the data transfer object containing quiz details
     * @return the newly created {@link QuizOutDTO}
     */
    QuizOutDTO createQuiz(QuizCreateInDTO quizCreateInDTO);

    /**
     * Retrieves all existing quizzes.
     *
     * @return a list of all {@link QuizOutDTO}
     */
    List<QuizOutDTO> getAllQuizzes();

    /**
     * Retrieves a quiz by its ID.
     *
     * @param id the ID of the quiz to retrieve
     * @return the {@link QuizOutDTO} if found
     */
    QuizOutDTO getQuizById(Long id);

    /**
     * Retrieves all quizzes for a specific course.
     *
     * @param courseId the ID of the course
     * @return a list of {@link QuizOutDTO} for the course
     */
    List<QuizOutDTO> getQuizzesByCourse(Long courseId);

    /**
     * Retrieves all quizzes for a specific course content.
     *
     * @param courseContentId the ID of the course content
     * @return a list of {@link QuizOutDTO} for the course content
     */
    List<QuizOutDTO> getQuizzesByCourseContent(Long courseContentId);

    /**
     * Updates the details of an existing quiz.
     *
     * @param quizId          the ID of the quiz to update
     * @param quizUpdateInDTO the updated quiz data
     * @return the updated {@link QuizOutDTO}
     */
    QuizOutDTO updateQuiz(Long quizId, QuizUpdateInDTO quizUpdateInDTO);

    /**
     * Soft deletes the quiz with the given ID by setting isActive to false.
     *
     * @param id the ID of the quiz to delete
     */
    void deleteQuiz(Long id);
}
