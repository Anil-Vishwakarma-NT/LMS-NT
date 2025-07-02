package com.example.course_service_lms.service;

import com.example.course_service_lms.dto.inDTO.UserResponseInDTO;
import com.example.course_service_lms.dto.inDTO.UserResponseUpdateInDTO;
import com.example.course_service_lms.dto.outDTO.UserResponseOutDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for UserResponse operations.
 * Defines business logic methods for managing user responses to quiz questions.
 */
public interface UserResponseService {

    /**
     * Create a new user response
     *
     * @param userResponseInDTOList the user response data
     * @return created user response DTO
     */
    List<UserResponseOutDTO> createUserResponse(List<UserResponseInDTO> userResponseInDTOList);

    /**
     * Get user response by ID
     *
     * @param responseId the response ID
     * @return user response DTO
     */
    UserResponseOutDTO getUserResponseById(Long responseId);

    /**
     * Update user response
     *
     * @param responseId the response ID
     * @param userResponseUpdateInDTO the update data
     * @return updated user response DTO
     */
    UserResponseOutDTO updateUserResponse(Long responseId, UserResponseUpdateInDTO userResponseUpdateInDTO);

    /**
     * Delete user response
     *
     * @param responseId the response ID
     */
    void deleteUserResponse(Long responseId);

    /**
     * Get all user responses with pagination
     *
     * @param pageable pagination information
     * @return paginated user responses
     */
    Page<UserResponseOutDTO> getAllUserResponses(Pageable pageable);

    /**
     * Get user responses by user ID
     *
     * @param userId the user ID
     * @return list of user responses
     */
    List<UserResponseOutDTO> getUserResponsesByUserId(Long userId);

    /**
     * Get user responses by quiz ID
     *
     * @param quizId the quiz ID
     * @return list of user responses
     */
    List<UserResponseOutDTO> getUserResponsesByQuizId(Long quizId);

    /**
     * Get user responses by user ID and quiz ID
     *
     * @param userId the user ID
     * @param quizId the quiz ID
     * @return list of user responses
     */
    List<UserResponseOutDTO> getUserResponsesByUserIdAndQuizId(Long userId, Long quizId);

    /**
     * Get user responses by user ID, quiz ID, and attempt
     *
     * @param userId the user ID
     * @param quizId the quiz ID
     * @param attempt the attempt number
     * @return list of user responses
     */
    List<UserResponseOutDTO> getUserResponsesByUserIdAndQuizIdAndAttempt(Long userId, Long quizId, Long attempt);

    /**
     * Get user responses by user ID with pagination
     *
     * @param userId the user ID
     * @param pageable pagination information
     * @return paginated user responses
     */
    Page<UserResponseOutDTO> getUserResponsesByUserId(Long userId, Pageable pageable);

    /**
     * Get user responses by quiz ID with pagination
     *
     * @param quizId the quiz ID
     * @param pageable pagination information
     * @return paginated user responses
     */
    Page<UserResponseOutDTO> getUserResponsesByQuizId(Long quizId, Pageable pageable);

    /**
     * Get total score for a user in a specific quiz attempt
     * This calculates the sum of all pointsEarned for the attempt
     *
     * @param userId the user ID
     * @param quizId the quiz ID
     * @param attempt the attempt number
     * @return total score as BigDecimal
     */
    BigDecimal getTotalScore(Long userId, Long quizId, Long attempt);

    /**
     * Count correct answers for a user in a specific quiz attempt
     *
     * @param userId the user ID
     * @param quizId the quiz ID
     * @param attempt the attempt number
     * @return count of correct answers
     */
    Long countCorrectAnswers(Long userId, Long quizId, Long attempt);

    /**
     * Get maximum attempt number for a user in a specific quiz
     *
     * @param userId the user ID
     * @param quizId the quiz ID
     * @return maximum attempt number
     */
    Long getMaxAttemptNumber(Long userId, Long quizId);
}