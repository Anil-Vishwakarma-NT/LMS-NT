package com.nt.course_service_lms.service;

import com.nt.course_service_lms.dto.inDTO.QuizAttemptCreateInDTO;
import com.nt.course_service_lms.dto.inDTO.QuizAttemptUpdateInDTO;
import com.nt.course_service_lms.dto.outDTO.QuizAttemptOutDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing quiz attempt operations in the Learning Management System (LMS).
 *
 * <p>This service provides comprehensive functionality for handling quiz attempts, including:
 * <ul>
 *   <li>Creating and updating quiz attempts</li>
 *   <li>Retrieving quiz attempts by various criteria (user, quiz, status)</li>
 *   <li>Managing attempt lifecycle (completion, abandonment, timeout)</li>
 *   <li>Providing statistics and validation operations</li>
 * </ul>
 *
 * <p>All operations are designed to maintain data integrity and provide proper error handling
 * for quiz attempt management scenarios.
 *
 * @author Course Service LMS Team
 * @version 1.0
 * @since 1.0
 */
public interface QuizAttemptService {

    /**
     * Creates a new quiz attempt record in the system.
     *
     * <p>This method initializes a new quiz attempt for a specific user and quiz,
     * setting up all necessary tracking information including start time, attempt number,
     * and initial status.
     *
     * @param dto the data transfer object containing quiz attempt creation details.
     *            Must include valid user ID, quiz ID, and other required fields.
     *            Cannot be null.
     * @return QuizAttemptOutDTO containing the created quiz attempt information
     *         including generated ID, timestamps, and status
     * @throws IllegalArgumentException if the input DTO is null or contains invalid data
     */
    QuizAttemptOutDTO createQuizAttempt(QuizAttemptCreateInDTO dto);

    /**
     * Updates an existing quiz attempt with new information.
     *
     * <p>This method allows modification of mutable fields in a quiz attempt record,
     * such as answers, progress status, and other trackable metrics. Immutable fields
     * like creation timestamp and attempt number cannot be modified.
     *
     * @param quizAttemptId the unique identifier of the quiz attempt to update.
     *                      Must be a positive long value representing an existing attempt.
     * @param dto the data transfer object containing updated quiz attempt information.
     *            Only non-null fields will be updated. Cannot be null.
     * @return QuizAttemptOutDTO containing the updated quiz attempt information
     * @throws IllegalArgumentException if quizAttemptId is null or non-positive,
     *                                 or if dto is null
     */
    QuizAttemptOutDTO updateQuizAttempt(Long quizAttemptId, QuizAttemptUpdateInDTO dto);

    /**
     * Retrieves a quiz attempt by its unique identifier.
     *
     * <p>This method provides a safe way to fetch quiz attempt details, returning
     * an Optional to handle cases where the attempt might not exist.
     *
     * @param quizAttemptId the unique identifier of the quiz attempt to retrieve.
     *                      Must be a positive long value.
     * @return Optional containing QuizAttemptOutDTO if found, empty Optional otherwise
     * @throws IllegalArgumentException if quizAttemptId is null or non-positive
     */
    Optional<QuizAttemptOutDTO> getQuizAttemptById(Long quizAttemptId);

    /**
     * Retrieves all quiz attempts in the system with pagination support.
     *
     * <p>This method provides a paginated view of all quiz attempts, useful for
     * administrative interfaces and reporting. Results are ordered by creation time
     * in descending order by default.
     *
     * @param pageable the pagination information including page number, size, and sort criteria.
     *                 Cannot be null. Use Pageable.unpaged() for retrieving all results.
     * @return Page containing QuizAttemptOutDTO objects for the requested page,
     *         along with pagination metadata
     * @throws IllegalArgumentException if pageable is null
     */
    Page<QuizAttemptOutDTO> getAllQuizAttempts(Pageable pageable);

    /**
     * Retrieves all quiz attempts made by a specific user.
     *
     * <p>This method returns all attempts across all quizzes for a given user,
     * ordered by creation time in descending order. Useful for user progress tracking
     * and personal attempt history.
     *
     * @param userId the unique identifier of the user whose attempts to retrieve.
     *               Must be a positive long value representing an existing user.
     * @return List of QuizAttemptOutDTO objects representing all attempts by the user.
     *         Returns empty list if user has no attempts.
     */
    List<QuizAttemptOutDTO> getQuizAttemptsByUserId(Long userId);

    /**
     * Retrieves all attempts made for a specific quiz.
     *
     * <p>This method returns all attempts across all users for a given quiz,
     * ordered by creation time in descending order. Useful for quiz analytics
     * and performance monitoring.
     *
     * @param quizId the unique identifier of the quiz whose attempts to retrieve.
     *               Must be a positive long value representing an existing quiz.
     * @return List of QuizAttemptOutDTO objects representing all attempts for the quiz.
     *         Returns empty list if quiz has no attempts.
     */
    List<QuizAttemptOutDTO> getQuizAttemptsByQuizId(Long quizId);

    /**
     * Retrieves all attempts made by a specific user for a specific quiz.
     *
     * <p>This method provides a focused view of attempts, showing all tries
     * by a particular user on a particular quiz. Results are ordered by
     * attempt number in ascending order.
     *
     * @param userId the unique identifier of the user. Must be a positive long value.
     * @param quizId the unique identifier of the quiz. Must be a positive long value.
     * @return List of QuizAttemptOutDTO objects representing all attempts by the user
     *         for the specified quiz. Returns empty list if no attempts exist.
     */
    List<QuizAttemptOutDTO> getQuizAttemptsByUserAndQuiz(Long userId, Long quizId);

    /**
     * Retrieves all quiz attempts filtered by their current status.
     *
     * <p>This method allows filtering attempts based on their lifecycle status,
     * useful for monitoring and administrative purposes. Common statuses include:
     * "IN_PROGRESS", "COMPLETED", "ABANDONED", "TIMED_OUT".
     *
     * @param status the status string to filter by. Must be a valid status value.
     *               Cannot be null or empty. Case-sensitive matching is performed.
     * @return List of QuizAttemptOutDTO objects matching the specified status.
     *         Returns empty list if no attempts have the given status.
     * @throws IllegalArgumentException if status is null, empty, or not a valid status value
     */
    List<QuizAttemptOutDTO> getQuizAttemptsByStatus(String status);

    /**
     * Retrieves the most recent attempt made by a user for a specific quiz.
     *
     * <p>This method is particularly useful for continuing interrupted attempts
     * or checking the current status of a user's progress on a quiz.
     *
     * @param userId the unique identifier of the user. Must be a positive long value.
     * @param quizId the unique identifier of the quiz. Must be a positive long value.
     * @return Optional containing the latest QuizAttemptOutDTO if any attempts exist,
     *         empty Optional otherwise
     */
    Optional<QuizAttemptOutDTO> getLatestAttemptByUserAndQuiz(Long userId, Long quizId);

    /**
     * Permanently deletes a quiz attempt from the system.
     *
     * <p>This method performs a hard delete of the quiz attempt record.
     * Use with caution as this operation cannot be undone. Consider soft deletion
     * or archiving for audit trail purposes.
     *
     * @param quizAttemptId the unique identifier of the quiz attempt to delete.
     */
    void deleteQuizAttempt(Long quizAttemptId);

    /**
     * Marks a quiz attempt as completed and records the final score details.
     *
     * <p>This method transitions an attempt from "IN_PROGRESS" to "COMPLETED" status,
     * recording the completion timestamp and score information. Once completed,
     * the attempt becomes read-only.
     *
     * @param quizAttemptId the unique identifier of the quiz attempt to complete.
     *                      Must be a positive long value representing an existing attempt.
     * @param scoreDetails the detailed scoring information in JSON or structured format.
     *                     Cannot be null but can be empty string if no score details available.
     * @return QuizAttemptOutDTO containing the updated attempt information with completion data
     */
    QuizAttemptOutDTO completeAttempt(Long quizAttemptId, String scoreDetails);

    /**
     * Marks a quiz attempt as abandoned by the user.
     *
     * <p>This method transitions an attempt from "IN_PROGRESS" to "ABANDONED" status,
     * typically called when a user explicitly exits a quiz without completing it.
     * The abandonment timestamp is recorded for analytics.
     *
     * @param quizAttemptId the unique identifier of the quiz attempt to abandon.
     * @return QuizAttemptOutDTO containing the updated attempt information with abandonment data
     * @throws IllegalArgumentException if quizAttemptId is null or non-positive
     */
    QuizAttemptOutDTO abandonAttempt(Long quizAttemptId);

    /**
     * Marks a quiz attempt as timed out due to exceeding the allowed duration.
     *
     * <p>This method transitions an attempt from "IN_PROGRESS" to "TIMED_OUT" status,
     * typically called by background processes that monitor quiz time limits.
     * The timeout timestamp is recorded and any partial progress is preserved.
     *
     * @param quizAttemptId the unique identifier of the quiz attempt to time out.
     * @return QuizAttemptOutDTO containing the updated attempt information with timeout data
     * @throws IllegalArgumentException if quizAttemptId is null or non-positive
     */
    QuizAttemptOutDTO timeOutAttempt(Long quizAttemptId);

    /**
     * Checks whether a quiz attempt exists with the given identifier.
     *
     * <p>This method provides a lightweight way to verify attempt existence
     * without retrieving the full attempt data. Useful for validation
     * and conditional logic.
     *
     * @param quizAttemptId the unique identifier of the quiz attempt to check.
     * @return true if an attempt exists with the given ID, false otherwise
     * @throws IllegalArgumentException if quizAttemptId is null or non-positive
     */
    boolean existsById(Long quizAttemptId);

    /**
     * Counts the total number of attempts made by a specific user for a specific quiz.
     *
     * <p>This method provides statistics about user engagement with a particular quiz,
     * useful for enforcing attempt limits and generating analytics reports.
     *
     * @param userId the unique identifier of the user. Must be a positive long value.
     * @param quizId the unique identifier of the quiz. Must be a positive long value.
     * @return the total count of attempts (including all statuses) made by the user
     *         for the specified quiz. Returns 0 if no attempts exist.
     * @throws IllegalArgumentException if userId or quizId is null or non-positive
     */
    long countAttemptsByUserAndQuiz(Long userId, Long quizId);
}
