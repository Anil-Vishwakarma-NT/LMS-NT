package com.nt.course_service_lms.service;

import com.nt.course_service_lms.dto.inDTO.UserResponseInDTO;
import com.nt.course_service_lms.dto.inDTO.UserResponseUpdateInDTO;
import com.nt.course_service_lms.dto.outDTO.UserResponseOutDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for UserResponse operations in the Learning Management System.
 *
 * <p>This interface defines the contract for managing user responses to quiz questions,
 * including CRUD operations, score calculations, and various query methods. It handles
 * user interactions with quizzes, tracking their responses, attempts, and performance
 * metrics.</p>
 *
 * <p>The service supports multiple quiz attempts per user and provides comprehensive
 * analytics capabilities including score calculation, correct answer counting, and
 * attempt tracking.</p>
 *
 * <p>Key features include:</p>
 * <ul>
 *   <li>Bulk creation and management of user responses</li>
 *   <li>Score calculation and performance tracking</li>
 *   <li>Multi-attempt quiz support</li>
 *   <li>Comprehensive filtering and pagination</li>
 *   <li>Analytics and reporting capabilities</li>
 * </ul>
 *
 * @author Course Service LMS Team
 * @version 1.0
 * @see UserResponseInDTO
 * @see UserResponseUpdateInDTO
 * @see UserResponseOutDTO
 * @since 1.0
 */
public interface UserResponseService {

    /**
     * Creates multiple new user responses in a single operation.
     *
     * <p>This method allows bulk creation of user responses, typically used when
     * a user submits answers for multiple questions in a quiz simultaneously.
     * Each response in the list will be validated and persisted.</p>
     *
     * @param userResponseInDTOList the list of user response data transfer objects
     *                              containing the response information to be created.
     *                              Must not be null or empty.
     * @return a list of created user response DTOs with generated IDs and timestamps
     * @throws IllegalArgumentException if userResponseInDTOList is null or empty
     */
    List<UserResponseOutDTO> createUserResponse(List<UserResponseInDTO> userResponseInDTOList);

    /**
     * Retrieves a specific user response by its unique identifier.
     *
     * @param responseId the unique identifier of the response to retrieve.
     *                   Must be a positive number.
     * @return the user response DTO containing the response details
     * @throws IllegalArgumentException if responseId is null or non-positive
     */
    UserResponseOutDTO getUserResponseById(Long responseId);

    /**
     * Updates an existing user response with new information.
     *
     * <p>This method allows modification of user response data, typically used
     * for corrections or updates to previously submitted answers.</p>
     *
     * @param responseId              the unique identifier of the response to update.
     *                                Must be a positive number.
     * @param userResponseUpdateInDTO the update data containing the new response information.
     *                                Must not be null.
     * @return the updated user response DTO with the new information
     * @throws IllegalArgumentException if responseId is null/non-positive or updateDTO is null
     */
    UserResponseOutDTO updateUserResponse(Long responseId, UserResponseUpdateInDTO userResponseUpdateInDTO);

    /**
     * Permanently deletes a user response from the system.
     *
     * <p>This operation cannot be undone. Use with caution as it will permanently
     * remove the user's response data and may affect score calculations.</p>
     *
     * @param responseId the unique identifier of the response to delete.
     *                   Must be a positive number.
     * @throws IllegalArgumentException if responseId is null or non-positive
     * @since 1.0
     */
    void deleteUserResponse(Long responseId);

    /**
     * Retrieves all user responses in the system with pagination support.
     *
     * <p>This method provides a paginated view of all user responses, useful
     * for administrative purposes and reporting.</p>
     *
     * @param pageable the pagination information including page number, size, and sorting.
     *                 Must not be null.
     * @return a paginated collection of user response DTOs
     */
    Page<UserResponseOutDTO> getAllUserResponses(Pageable pageable);

    /**
     * Retrieves all responses submitted by a specific user.
     *
     * <p>This method returns all quiz responses for a particular user across
     * all quizzes and attempts, useful for user progress tracking.</p>
     *
     * @param userId the unique identifier of the user whose responses to retrieve.
     *               Must be a positive number.
     * @return a list of user response DTOs for the specified user
     */
    List<UserResponseOutDTO> getUserResponsesByUserId(Long userId);

    /**
     * Retrieves all user responses for a specific quiz.
     *
     * <p>This method returns all responses submitted for a particular quiz
     * across all users and attempts, useful for quiz analytics.</p>
     *
     * @param quizId the unique identifier of the quiz whose responses to retrieve.
     *               Must be a positive number.
     * @return a list of user response DTOs for the specified quiz
     */
    List<UserResponseOutDTO> getUserResponsesByQuizId(Long quizId);

    /**
     * Retrieves all responses submitted by a specific user for a specific quiz.
     *
     * <p>This method returns responses across all attempts for the given
     * user-quiz combination, useful for tracking user progress on a specific quiz.</p>
     *
     * @param userId the unique identifier of the user. Must be a positive number.
     * @param quizId the unique identifier of the quiz. Must be a positive number.
     * @return a list of user response DTOs for the specified user and quiz
     */
    List<UserResponseOutDTO> getUserResponsesByUserIdAndQuizId(Long userId, Long quizId);

    /**
     * Retrieves all responses for a specific user, quiz, and attempt combination.
     *
     * <p>This method returns responses for a single quiz attempt, useful for
     * reviewing specific attempt details and calculating attempt-specific scores.</p>
     *
     * @param userId  the unique identifier of the user. Must be a positive number.
     * @param quizId  the unique identifier of the quiz. Must be a positive number.
     * @param attempt the attempt number. Must be a positive number.
     * @return a list of user response DTOs for the specified user, quiz, and attempt
     */
    List<UserResponseOutDTO> getUserResponsesByUserIdAndQuizIdAndAttempt(Long userId, Long quizId, Long attempt);

    /**
     * Retrieves all responses submitted by a specific user with pagination support.
     *
     * <p>This is the paginated version of getUserResponsesByUserId, useful when
     * a user has submitted a large number of responses.</p>
     *
     * @param userId   the unique identifier of the user whose responses to retrieve.
     *                 Must be a positive number.
     * @param pageable the pagination information including page number, size, and sorting.
     *                 Must not be null.
     * @return a paginated collection of user response DTOs for the specified user
     */
    Page<UserResponseOutDTO> getUserResponsesByUserId(Long userId, Pageable pageable);

    /**
     * Retrieves all user responses for a specific quiz with pagination support.
     *
     * <p>This is the paginated version of getUserResponsesByQuizId, useful when
     * a quiz has received a large number of responses.</p>
     *
     * @param quizId   the unique identifier of the quiz whose responses to retrieve.
     *                 Must be a positive number.
     * @param pageable the pagination information including page number, size, and sorting.
     *                 Must not be null.
     * @return a paginated collection of user response DTOs for the specified quiz
     */
    Page<UserResponseOutDTO> getUserResponsesByQuizId(Long quizId, Pageable pageable);

    /**
     * Calculates the total score for a user's specific quiz attempt.
     *
     * <p>This method sums all points earned by the user for a particular quiz attempt.
     * The score is calculated by aggregating the pointsEarned field from all responses
     * in the specified attempt.</p>
     *
     * @param userId  the unique identifier of the user. Must be a positive number.
     * @param quizId  the unique identifier of the quiz. Must be a positive number.
     * @param attempt the attempt number for which to calculate the score.
     *                Must be a positive number.
     * @return the total score as a BigDecimal, or BigDecimal.ZERO if no responses found
     */
    BigDecimal getTotalScore(Long userId, Long quizId, Long attempt);

    /**
     * Counts the number of correct answers for a user's specific quiz attempt.
     *
     * <p>This method provides a count of questions answered correctly in a specific
     * quiz attempt, useful for performance analysis and pass/fail determination.</p>
     *
     * @param userId  the unique identifier of the user. Must be a positive number.
     * @param quizId  the unique identifier of the quiz. Must be a positive number.
     * @param attempt the attempt number for which to count correct answers.
     *                Must be a positive number.
     * @return the count of correct answers, or 0 if no correct answers found
     */
    Long countCorrectAnswers(Long userId, Long quizId, Long attempt);

    /**
     * Retrieves the maximum attempt number for a user's quiz submissions.
     *
     * <p>This method finds the highest attempt number recorded for a specific
     * user-quiz combination, useful for determining how many times a user has
     * attempted a quiz and for generating the next attempt number.</p>
     *
     * @param userId the unique identifier of the user. Must be a positive number.
     * @param quizId the unique identifier of the quiz. Must be a positive number.
     * @return the maximum attempt number, or 0 if no attempts found
     */
    Long getMaxAttemptNumber(Long userId, Long quizId);
}
