package com.example.course_service_lms.service.serviceImpl;

import com.example.course_service_lms.converters.UserResponseConverter;
import com.example.course_service_lms.dto.inDTO.UserResponseInDTO;
import com.example.course_service_lms.dto.inDTO.UserResponseUpdateInDTO;
import com.example.course_service_lms.dto.outDTO.UserResponseOutDTO;
import com.example.course_service_lms.entity.UserResponse;
import com.example.course_service_lms.exception.ResourceAlreadyExistsException;
import com.example.course_service_lms.exception.ResourceNotFoundException;
import com.example.course_service_lms.repository.UserResponseRepository;
import com.example.course_service_lms.service.UserResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of UserResponseService interface.
 * Provides business logic for managing user responses to quiz questions.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserResponseServiceImpl implements UserResponseService {

    private final UserResponseRepository userResponseRepository;
    private final UserResponseConverter userResponseConverter;

    @Override
    public UserResponseOutDTO createUserResponse(UserResponseInDTO userResponseInDTO) {
        log.info("Creating user response for user ID: {}, quiz ID: {}, question ID: {}, attempt: {}",
                userResponseInDTO.getUserId(), userResponseInDTO.getQuizId(),
                userResponseInDTO.getQuestionId(), userResponseInDTO.getAttempt());

        try {
            // Check if response already exists for this user, question, and attempt
            boolean responseExists = userResponseRepository.existsByUserIdAndQuestionIdAndAttempt(
                    userResponseInDTO.getUserId(),
                    userResponseInDTO.getQuestionId(),
                    userResponseInDTO.getAttempt()
            );

            if (responseExists) {
                log.warn("User response already exists for user ID: {}, question ID: {}, attempt: {}",
                        userResponseInDTO.getUserId(), userResponseInDTO.getQuestionId(), userResponseInDTO.getAttempt());
                throw new ResourceAlreadyExistsException(
                        String.format("User response already exists for user ID: %d, question ID: %d, attempt: %d",
                                userResponseInDTO.getUserId(), userResponseInDTO.getQuestionId(), userResponseInDTO.getAttempt())
                );
            }

            // Convert DTO to entity
            UserResponse userResponse = userResponseConverter.convertToEntity(userResponseInDTO);

            // Set answered time if not provided
            if (userResponse.getAnsweredAt() == null) {
                userResponse.setAnsweredAt(LocalDateTime.now());
            }

            // Save the entity
            UserResponse savedUserResponse = userResponseRepository.save(userResponse);
            log.info("User response created successfully with ID: {}", savedUserResponse.getResponseId());

            // Convert and return DTO
            return userResponseConverter.convertToOutDTO(savedUserResponse);

        } catch (ResourceAlreadyExistsException e) {
            log.error("Failed to create user response - already exists: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while creating user response for user ID: {}, quiz ID: {}, question ID: {}",
                    userResponseInDTO.getUserId(), userResponseInDTO.getQuizId(), userResponseInDTO.getQuestionId(), e);
            throw new RuntimeException("Failed to create user response", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponseOutDTO getUserResponseById(Long responseId) {
        log.info("Fetching user response with ID: {}", responseId);

        try {
            UserResponse userResponse = userResponseRepository.findById(responseId)
                    .orElseThrow(() -> {
                        log.warn("User response not found with ID: {}", responseId);
                        return new ResourceNotFoundException("User response not found with ID: " + responseId);
                    });

            log.info("User response found with ID: {}", responseId);
            return userResponseConverter.convertToOutDTO(userResponse);

        } catch (ResourceNotFoundException e) {
            log.error("User response not found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching user response with ID: {}", responseId, e);
            throw new RuntimeException("Failed to fetch user response", e);
        }
    }

    @Override
    public UserResponseOutDTO updateUserResponse(Long responseId, UserResponseUpdateInDTO userResponseUpdateInDTO) {
        log.info("Updating user response with ID: {}", responseId);

        try {
            UserResponse existingUserResponse = userResponseRepository.findById(responseId)
                    .orElseThrow(() -> {
                        log.warn("User response not found with ID: {} for update", responseId);
                        return new ResourceNotFoundException("User response not found with ID: " + responseId);
                    });

            // Update entity with new data
            UserResponse updatedUserResponse = userResponseConverter.updateEntityFromDTO(existingUserResponse, userResponseUpdateInDTO);

            // Save updated entity
            UserResponse savedUserResponse = userResponseRepository.save(updatedUserResponse);
            log.info("User response updated successfully with ID: {}", savedUserResponse.getResponseId());

            // Convert and return DTO
            return userResponseConverter.convertToOutDTO(savedUserResponse);

        } catch (ResourceNotFoundException e) {
            log.error("Failed to update user response - not found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while updating user response with ID: {}", responseId, e);
            throw new RuntimeException("Failed to update user response", e);
        }
    }

    @Override
    public void deleteUserResponse(Long responseId) {
        log.info("Deleting user response with ID: {}", responseId);

        try {
            if (!userResponseRepository.existsById(responseId)) {
                log.warn("User response not found with ID: {} for deletion", responseId);
                throw new ResourceNotFoundException("User response not found with ID: " + responseId);
            }

            userResponseRepository.deleteById(responseId);
            log.info("User response deleted successfully with ID: {}", responseId);

        } catch (ResourceNotFoundException e) {
            log.error("Failed to delete user response - not found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error occurred while deleting user response with ID: {}", responseId, e);
            throw new RuntimeException("Failed to delete user response", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseOutDTO> getAllUserResponses(Pageable pageable) {
        log.info("Fetching all user responses with pagination - page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        try {
            Page<UserResponse> userResponsePage = userResponseRepository.findAll(pageable);
            log.info("Found {} user responses", userResponsePage.getTotalElements());

            return userResponsePage.map(userResponseConverter::convertToOutDTO);

        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching all user responses", e);
            throw new RuntimeException("Failed to fetch user responses", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseOutDTO> getUserResponsesByUserId(Long userId) {
        log.info("Fetching user responses for user ID: {}", userId);

        try {
            List<UserResponse> userResponses = userResponseRepository.findByUserId(userId);
            log.info("Found {} user responses for user ID: {}", userResponses.size(), userId);

            return userResponseConverter.convertToOutDTOList(userResponses);

        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching user responses for user ID: {}", userId, e);
            throw new RuntimeException("Failed to fetch user responses", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseOutDTO> getUserResponsesByQuizId(Long quizId) {
        log.info("Fetching user responses for quiz ID: {}", quizId);

        try {
            List<UserResponse> userResponses = userResponseRepository.findByQuizId(quizId);
            log.info("Found {} user responses for quiz ID: {}", userResponses.size(), quizId);

            return userResponseConverter.convertToOutDTOList(userResponses);

        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching user responses for quiz ID: {}", quizId, e);
            throw new RuntimeException("Failed to fetch user responses", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseOutDTO> getUserResponsesByUserIdAndQuizId(Long userId, Long quizId) {
        log.info("Fetching user responses for user ID: {} and quiz ID: {}", userId, quizId);

        try {
            List<UserResponse> userResponses = userResponseRepository.findByUserIdAndQuizId(userId, quizId);
            log.info("Found {} user responses for user ID: {} and quiz ID: {}", userResponses.size(), userId, quizId);

            return userResponseConverter.convertToOutDTOList(userResponses);

        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching user responses for user ID: {} and quiz ID: {}", userId, quizId, e);
            throw new RuntimeException("Failed to fetch user responses", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseOutDTO> getUserResponsesByUserIdAndQuizIdAndAttempt(Long userId, Long quizId, Long attempt) {
        log.info("Fetching user responses for user ID: {}, quiz ID: {}, attempt: {}", userId, quizId, attempt);

        try {
            List<UserResponse> userResponses = userResponseRepository.findByUserIdAndQuizIdAndAttempt(userId, quizId, attempt);
            log.info("Found {} user responses for user ID: {}, quiz ID: {}, attempt: {}",
                    userResponses.size(), userId, quizId, attempt);

            return userResponseConverter.convertToOutDTOList(userResponses);

        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching user responses for user ID: {}, quiz ID: {}, attempt: {}",
                    userId, quizId, attempt, e);
            throw new RuntimeException("Failed to fetch user responses", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseOutDTO> getUserResponsesByUserId(Long userId, Pageable pageable) {
        log.info("Fetching user responses for user ID: {} with pagination - page: {}, size: {}",
                userId, pageable.getPageNumber(), pageable.getPageSize());

        try {
            Page<UserResponse> userResponsePage = userResponseRepository.findByUserId(userId, pageable);
            log.info("Found {} user responses for user ID: {}", userResponsePage.getTotalElements(), userId);

            return userResponsePage.map(userResponseConverter::convertToOutDTO);

        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching user responses for user ID: {} with pagination", userId, e);
            throw new RuntimeException("Failed to fetch user responses", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseOutDTO> getUserResponsesByQuizId(Long quizId, Pageable pageable) {
        log.info("Fetching user responses for quiz ID: {} with pagination - page: {}, size: {}",
                quizId, pageable.getPageNumber(), pageable.getPageSize());

        try {
            Page<UserResponse> userResponsePage = userResponseRepository.findByQuizId(quizId, pageable);
            log.info("Found {} user responses for quiz ID: {}", userResponsePage.getTotalElements(), quizId);

            return userResponsePage.map(userResponseConverter::convertToOutDTO);

        } catch (Exception e) {
            log.error("Unexpected error occurred while fetching user responses for quiz ID: {} with pagination", quizId, e);
            throw new RuntimeException("Failed to fetch user responses", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalScore(Long userId, Long quizId, Long attempt) {
        log.info("Calculating total score for user ID: {}, quiz ID: {}, attempt: {}", userId, quizId, attempt);

        try {
            BigDecimal totalScore = userResponseRepository.getTotalScoreByUserIdAndQuizIdAndAttempt(userId, quizId, attempt);
            if (totalScore == null) {
                totalScore = BigDecimal.ZERO;
            }
            log.info("Total score calculated: {} for user ID: {}, quiz ID: {}, attempt: {}", totalScore, userId, quizId, attempt);
            return totalScore;

        } catch (Exception e) {
            log.error("Unexpected error occurred while calculating total score for user ID: {}, quiz ID: {}, attempt: {}",
                    userId, quizId, attempt, e);
            throw new RuntimeException("Failed to calculate total score", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Long countCorrectAnswers(Long userId, Long quizId, Long attempt) {
        log.info("Counting correct answers for user ID: {}, quiz ID: {}, attempt: {}", userId, quizId, attempt);

        try {
            Long correctCount = userResponseRepository.countCorrectAnswersByUserIdAndQuizIdAndAttempt(userId, quizId, attempt);
            log.info("Correct answers count: {} for user ID: {}, quiz ID: {}, attempt: {}", correctCount, userId, quizId, attempt);
            return correctCount;

        } catch (Exception e) {
            log.error("Unexpected error occurred while counting correct answers for user ID: {}, quiz ID: {}, attempt: {}",
                    userId, quizId, attempt, e);
            throw new RuntimeException("Failed to count correct answers", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Long getMaxAttemptNumber(Long userId, Long quizId) {
        log.info("Getting maximum attempt number for user ID: {} and quiz ID: {}", userId, quizId);

        try {
            Long maxAttempt = userResponseRepository.getMaxAttemptByUserIdAndQuizId(userId, quizId);
            if (maxAttempt == null) {
                maxAttempt = 0L;
            }
            log.info("Maximum attempt number: {} for user ID: {} and quiz ID: {}", maxAttempt, userId, quizId);
            return maxAttempt;

        } catch (Exception e) {
            log.error("Unexpected error occurred while getting maximum attempt number for user ID: {} and quiz ID: {}",
                    userId, quizId, e);
            throw new RuntimeException("Failed to get maximum attempt number", e);
        }
    }
}