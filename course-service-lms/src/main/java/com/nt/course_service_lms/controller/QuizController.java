package com.nt.course_service_lms.controller;

import com.nt.course_service_lms.dto.inDTO.QuizCreateInDTO;
import com.nt.course_service_lms.dto.inDTO.QuizUpdateInDTO;
import com.nt.course_service_lms.dto.outDTO.QuizOutDTO;
import com.nt.course_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.course_service_lms.service.QuizService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller for managing quiz-related operations in the Course Service of the LMS.
 * Exception handling is managed by GlobalExceptionHandler.
 */
@RestController
@RequestMapping("/api/service-api/quizzes")
@Slf4j

public class QuizController {

    /**
     * Service for handling quiz-related business logic.
     */
    @Autowired
    private QuizService quizService;

    /**
     * Creates a new quiz.
     *
     * @param quizCreateInDTO DTO containing the details of the quiz to be created.
     * @return ResponseEntity containing the StandardResponseOutDTO with created QuizOutDTO.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseOutDTO<QuizOutDTO>> createQuiz(
            @Valid @RequestBody final QuizCreateInDTO quizCreateInDTO
    ) {
        log.info("Received request to create quiz: {}", quizCreateInDTO.getTitle());
        QuizOutDTO createdQuiz = quizService.createQuiz(quizCreateInDTO);
        log.info("Quiz created successfully with ID: {}", createdQuiz.getQuizId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponseOutDTO.success(createdQuiz, "Quiz created successfully"));
    }

    /**
     * Retrieves all available active quizzes.
     *
     * @return ResponseEntity containing StandardResponseOutDTO with a list of all QuizOutDTO.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseOutDTO<List<QuizOutDTO>>> getAllQuizzes() {
        log.info("Received request to fetch all active quizzes");
        List<QuizOutDTO> quizzes = quizService.getAllQuizzes();
        log.info("Retrieved {} active quizzes", quizzes.size());
        return ResponseEntity.ok(StandardResponseOutDTO.success(quizzes, "All quizzes retrieved successfully"));
    }

    /**
     * Retrieves a specific quiz by its ID.
     *
     * @param id The ID of the quiz to retrieve.
     * @return ResponseEntity containing StandardResponseOutDTO with the QuizOutDTO if found.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<StandardResponseOutDTO<QuizOutDTO>> getQuizById(@PathVariable final Long id) {
        log.info("Received request to fetch quiz with ID: {}", id);
        QuizOutDTO quiz = quizService.getQuizById(id);
        log.info("Quiz retrieved successfully with ID: {}", id);
        return ResponseEntity.ok(StandardResponseOutDTO.success(quiz, "Quiz retrieved successfully"));
    }

    /**
     * Retrieves all quizzes for a specific course.
     *
     * @param courseId The ID of the course.
     * @return ResponseEntity containing StandardResponseOutDTO with a list of QuizOutDTO for the course.
     */
    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<StandardResponseOutDTO<List<QuizOutDTO>>> getQuizzesByCourse(@PathVariable final Long courseId) {
        log.info("Received request to fetch quizzes for course ID: {}", courseId);
        List<QuizOutDTO> quizzes = quizService.getQuizzesByCourse(courseId);
        log.info("Retrieved {} quizzes for course ID: {}", quizzes.size(), courseId);
        return ResponseEntity.ok(StandardResponseOutDTO.success(quizzes, "Course quizzes retrieved successfully"));
    }

    /**
     * Retrieves all quizzes for a specific course content.
     *
     * @param courseContentId The ID of the course content.
     * @return ResponseEntity containing StandardResponseOutDTO with a list of QuizOutDTO for the course content.
     */
    @GetMapping("/course-content/{courseContentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    public ResponseEntity<StandardResponseOutDTO<List<QuizOutDTO>>> getQuizzesByCourseContent(
            @PathVariable final Long courseContentId
    ) {
        log.info("Received request to fetch quizzes for course content ID: {}", courseContentId);
        List<QuizOutDTO> quizzes = quizService.getQuizzesByCourseContent(courseContentId);
        log.info("Retrieved {} quizzes for course content ID: {}", quizzes.size(), courseContentId);
        return ResponseEntity.ok(StandardResponseOutDTO.success(quizzes, "Course content quizzes retrieved successfully"));
    }

    /**
     * Updates an existing quiz with the given ID.
     *
     * @param id              The ID of the quiz to update.
     * @param quizUpdateInDTO DTO containing the updated quiz details.
     * @return ResponseEntity containing StandardResponseOutDTO with updated QuizOutDTO.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseOutDTO<QuizOutDTO>> updateQuiz(@PathVariable final Long id,
                                                                         @Valid @RequestBody final QuizUpdateInDTO quizUpdateInDTO
    ) {
        log.info("Received request to update quiz with ID: {}", id);
        QuizOutDTO updatedQuiz = quizService.updateQuiz(id, quizUpdateInDTO);
        log.info("Quiz updated successfully with ID: {}", id);
        return ResponseEntity.ok(StandardResponseOutDTO.success(updatedQuiz, "Quiz updated successfully"));
    }

    /**
     * Soft deletes a quiz by setting its active status to false.
     *
     * @param id The ID of the quiz to delete.
     * @return ResponseEntity containing StandardResponseOutDTO with success message.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseOutDTO<String>> deleteQuiz(@PathVariable final Long id) {
        log.info("Received request to delete quiz with ID: {}", id);
        quizService.deleteQuiz(id);
        log.info("Quiz soft deleted successfully with ID: {}", id);
        return ResponseEntity.ok(StandardResponseOutDTO.success("Quiz deleted successfully", "Quiz deleted successfully"));
    }
}
