package com.example.course_service_lms.service.serviceImpl;

import com.example.course_service_lms.dto.inDTO.QuizAttemptCreateInDTO;
import com.example.course_service_lms.dto.inDTO.QuizAttemptUpdateInDTO;
import com.example.course_service_lms.dto.outDTO.QuizAttemptOutDTO;
import com.example.course_service_lms.entity.Quiz;
import com.example.course_service_lms.entity.QuizAttempt;
import com.example.course_service_lms.exception.ResourceAlreadyExistsException;
import com.example.course_service_lms.exception.ResourceNotFoundException;
import com.example.course_service_lms.exception.ResourceNotValidException;
import com.example.course_service_lms.repository.QuizAttemptRepository;
import com.example.course_service_lms.repository.QuizRepository;
import com.example.course_service_lms.service.QuizAttemptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service implementation for QuizAttempt operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class QuizAttemptServiceImpl implements QuizAttemptService {

    public static final String IN_PROGRESS = "IN_PROGRESS";
    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizRepository quizRepository;

    @Override
    public QuizAttemptOutDTO createQuizAttempt(QuizAttemptCreateInDTO dto) {
        log.info("Creating new quiz attempt for user: {} and quiz: {}", dto.getUserId(), dto.getQuizId());

        // Validate input
        if (dto.getUserId() == null || dto.getQuizId() == null) {
            throw new ResourceNotValidException("User ID and Quiz ID are required");
        }

        Quiz quiz = quizRepository.findById(dto.getQuizId()).orElseThrow(
                () -> new ResourceNotFoundException("Quiz Not Found")
        );

        // Get the latest attempt for this user and quiz
        QuizAttempt existingAttempt = quizAttemptRepository.findTopByUserIdAndQuizIdOrderByAttemptDesc(dto.getUserId(), dto.getQuizId());

        // Calculate the next attempt number
        Long nextAttemptNumber = (existingAttempt != null) ? existingAttempt.getAttempt() + 1 : 1;

        // Check if user has exceeded maximum attempts
        if (nextAttemptNumber > quiz.getAttemptsAllowed()) {
            throw new ResourceNotValidException("User has exceeded maximum allowed attempts for this quiz");
        }

        // Check if user has any active attempts for this quiz
        if (quizAttemptRepository.hasActiveAttempt(dto.getUserId(), dto.getQuizId())) {
            throw new ResourceAlreadyExistsException("User already has an active attempt for this quiz");
        }

        QuizAttempt quizAttempt = new QuizAttempt();
        quizAttempt.setAttempt(nextAttemptNumber); // Set the calculated attempt number
        quizAttempt.setQuizId(dto.getQuizId());
        quizAttempt.setUserId(dto.getUserId());
        quizAttempt.setStartedAt(LocalDateTime.now());
        quizAttempt.setStatus(IN_PROGRESS);
        quizAttempt.setCreatedAt(LocalDateTime.now());
        quizAttempt.setUpdatedAt(LocalDateTime.now());

        QuizAttempt savedAttempt = quizAttemptRepository.save(quizAttempt);
        log.info("Created quiz attempt with ID: {} (attempt number: {})", savedAttempt.getQuizAttemptId(), nextAttemptNumber);
        return convertToOutDTO(savedAttempt);
    }

    @Override
    public QuizAttemptOutDTO updateQuizAttempt(Long quizAttemptId, QuizAttemptUpdateInDTO dto) {
        log.info("Updating quiz attempt with ID: {}", quizAttemptId);

        if (quizAttemptId == null) {
            throw new ResourceNotValidException("Quiz attempt ID cannot be null");
        }

        QuizAttempt existingAttempt = quizAttemptRepository.findById(quizAttemptId)
                .orElseThrow(() -> new ResourceNotFoundException("QuizAttempt not found with ID: " + quizAttemptId));

        // Validate status transition
        if (dto.getStatus() != null && existingAttempt.getStatus() != null) {
            if (isInvalidStatusTransition(existingAttempt.getStatus(), dto.getStatus())) {
                throw new ResourceNotValidException("Invalid status transition from " +
                        existingAttempt.getStatus() + " to " + dto.getStatus());
            }
        }

        // Update fields if provided
        if (dto.getFinishedAt() != null) {
            existingAttempt.setFinishedAt(dto.getFinishedAt());
        }

        if (dto.getScoreDetails() != null) {
            existingAttempt.setScoreDetails(dto.getScoreDetails());
        }

        if (dto.getStatus() != null) {
            existingAttempt.setStatus(dto.getStatus());
            // Auto-set finishedAt if status is set to COMPLETED, ABANDONED, or TIMED_OUT
            if (("COMPLETED".equals(dto.getStatus()) || "ABANDONED".equals(dto.getStatus()) ||
                    "TIMED_OUT".equals(dto.getStatus())) && existingAttempt.getFinishedAt() == null) {
                existingAttempt.setFinishedAt(LocalDateTime.now());
            }
        }

        existingAttempt.setUpdatedAt(LocalDateTime.now());

        QuizAttempt updatedAttempt = quizAttemptRepository.save(existingAttempt);
        log.info("Updated quiz attempt with ID: {}", quizAttemptId);
        return convertToOutDTO(updatedAttempt);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<QuizAttemptOutDTO> getQuizAttemptById(Long quizAttemptId) {
        log.debug("Fetching quiz attempt with ID: {}", quizAttemptId);

        if (quizAttemptId == null) {
            throw new ResourceNotValidException("Quiz attempt ID cannot be null");
        }

        return quizAttemptRepository.findById(quizAttemptId)
                .map(this::convertToOutDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuizAttemptOutDTO> getAllQuizAttempts(Pageable pageable) {
        log.debug("Fetching all quiz attempts with pagination");

        if (pageable == null) {
            throw new ResourceNotValidException("Pageable cannot be null");
        }

        Page<QuizAttempt> attemptPage = quizAttemptRepository.findAll(pageable);
        List<QuizAttemptOutDTO> attemptDTOs = attemptPage.getContent().stream()
                .map(this::convertToOutDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(attemptDTOs, pageable, attemptPage.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizAttemptOutDTO> getQuizAttemptsByUserId(Long userId) {
        log.debug("Fetching quiz attempts for user: {}", userId);

        if (userId == null) {
            throw new ResourceNotValidException("User ID cannot be null");
        }

        return quizAttemptRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToOutDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizAttemptOutDTO> getQuizAttemptsByQuizId(Long quizId) {
        log.debug("Fetching quiz attempts for quiz: {}", quizId);

        if (quizId == null) {
            throw new ResourceNotValidException("Quiz ID cannot be null");
        }

        return quizAttemptRepository.findByQuizIdOrderByCreatedAtDesc(quizId)
                .stream()
                .map(this::convertToOutDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizAttemptOutDTO> getQuizAttemptsByUserAndQuiz(Long userId, Long quizId) {
        log.debug("Fetching quiz attempts for user: {} and quiz: {}", userId, quizId);

        if (userId == null || quizId == null) {
            throw new ResourceNotValidException("User ID and Quiz ID cannot be null");
        }

        return quizAttemptRepository.findByUserIdAndQuizIdOrderByAttemptDesc(userId, quizId)
                .stream()
                .map(this::convertToOutDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizAttemptOutDTO> getQuizAttemptsByStatus(String status) {
        log.debug("Fetching quiz attempts with status: {}", status);

        if (status == null || status.trim().isEmpty()) {
            throw new ResourceNotValidException("Status cannot be null or empty");
        }

        if (!isValidStatus(status)) {
            throw new ResourceNotValidException("Invalid status: " + status);
        }

        return quizAttemptRepository.findByStatusOrderByCreatedAtDesc(status)
                .stream()
                .map(this::convertToOutDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<QuizAttemptOutDTO> getLatestAttemptByUserAndQuiz(Long userId, Long quizId) {
        log.debug("Fetching latest attempt for user: {} and quiz: {}", userId, quizId);

        if (userId == null || quizId == null) {
            throw new ResourceNotValidException("User ID and Quiz ID cannot be null");
        }

        return quizAttemptRepository.findLatestAttemptByUserAndQuiz(userId, quizId)
                .map(this::convertToOutDTO);
    }

    @Override
    public void deleteQuizAttempt(Long quizAttemptId) {
        log.info("Deleting quiz attempt with ID: {}", quizAttemptId);

        if (quizAttemptId == null) {
            throw new ResourceNotValidException("Quiz attempt ID cannot be null");
        }

        if (!quizAttemptRepository.existsById(quizAttemptId)) {
            throw new ResourceNotFoundException("QuizAttempt not found with ID: " + quizAttemptId);
        }

        quizAttemptRepository.deleteById(quizAttemptId);
        log.info("Deleted quiz attempt with ID: {}", quizAttemptId);
    }

    @Override
    public QuizAttemptOutDTO completeAttempt(Long quizAttemptId, String scoreDetails) {
        log.info("Completing quiz attempt with ID: {}", quizAttemptId);

        if (quizAttemptId == null) {
            throw new ResourceNotValidException("Quiz attempt ID cannot be null");
        }

        QuizAttempt attempt = quizAttemptRepository.findById(quizAttemptId)
                .orElseThrow(() -> new ResourceNotFoundException("QuizAttempt not found with ID: " + quizAttemptId));

        // Validate that attempt can be completed
        if (!"IN_PROGRESS".equals(attempt.getStatus())) {
            throw new ResourceNotValidException("Cannot complete quiz attempt with status: " + attempt.getStatus());
        }

        attempt.setStatus("COMPLETED");
        attempt.setFinishedAt(LocalDateTime.now());
        attempt.setScoreDetails(scoreDetails);
        attempt.setUpdatedAt(LocalDateTime.now());

        QuizAttempt savedAttempt = quizAttemptRepository.save(attempt);
        return convertToOutDTO(savedAttempt);
    }

    @Override
    public QuizAttemptOutDTO abandonAttempt(Long quizAttemptId) {
        log.info("Abandoning quiz attempt with ID: {}", quizAttemptId);

        if (quizAttemptId == null) {
            throw new ResourceNotValidException("Quiz attempt ID cannot be null");
        }

        QuizAttempt attempt = quizAttemptRepository.findById(quizAttemptId)
                .orElseThrow(() -> new ResourceNotFoundException("QuizAttempt not found with ID: " + quizAttemptId));

        // Validate that attempt can be abandoned
        if (!"IN_PROGRESS".equals(attempt.getStatus())) {
            throw new ResourceNotValidException("Cannot abandon quiz attempt with status: " + attempt.getStatus());
        }

        attempt.setStatus("ABANDONED");
        attempt.setFinishedAt(LocalDateTime.now());
        attempt.setUpdatedAt(LocalDateTime.now());

        QuizAttempt savedAttempt = quizAttemptRepository.save(attempt);
        return convertToOutDTO(savedAttempt);
    }

    @Override
    public QuizAttemptOutDTO timeOutAttempt(Long quizAttemptId) {
        log.info("Timing out quiz attempt with ID: {}", quizAttemptId);

        if (quizAttemptId == null) {
            throw new ResourceNotValidException("Quiz attempt ID cannot be null");
        }

        QuizAttempt attempt = quizAttemptRepository.findById(quizAttemptId)
                .orElseThrow(() -> new ResourceNotFoundException("QuizAttempt not found with ID: " + quizAttemptId));

        // Validate that attempt can be timed out
        if (!"IN_PROGRESS".equals(attempt.getStatus())) {
            throw new ResourceNotValidException("Cannot time out quiz attempt with status: " + attempt.getStatus());
        }

        attempt.setStatus("TIMED_OUT");
        attempt.setFinishedAt(LocalDateTime.now());
        attempt.setUpdatedAt(LocalDateTime.now());

        QuizAttempt savedAttempt = quizAttemptRepository.save(attempt);
        return convertToOutDTO(savedAttempt);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long quizAttemptId) {
        if (quizAttemptId == null) {
            throw new ResourceNotValidException("Quiz attempt ID cannot be null");
        }

        return quizAttemptRepository.existsById(quizAttemptId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countAttemptsByUserAndQuiz(Long userId, Long quizId) {
        log.debug("Counting attempts for user: {} and quiz: {}", userId, quizId);

        if (userId == null || quizId == null) {
            throw new ResourceNotValidException("User ID and Quiz ID cannot be null");
        }

        return quizAttemptRepository.countByUserIdAndQuizId(userId, quizId);
    }

    /**
     * Converts QuizAttempt entity to QuizAttemptOutDTO
     */
    private QuizAttemptOutDTO convertToOutDTO(QuizAttempt quizAttempt) {
        QuizAttemptOutDTO dto = new QuizAttemptOutDTO();
        dto.setQuizAttemptId(quizAttempt.getQuizAttemptId());
        dto.setAttempt(quizAttempt.getAttempt());
        dto.setQuizId(quizAttempt.getQuizId());
        dto.setUserId(quizAttempt.getUserId());
        dto.setStartedAt(quizAttempt.getStartedAt());
        dto.setFinishedAt(quizAttempt.getFinishedAt());
        dto.setScoreDetails(quizAttempt.getScoreDetails());
        dto.setStatus(quizAttempt.getStatus());
        dto.setCreatedAt(quizAttempt.getCreatedAt());
        dto.setUpdatedAt(quizAttempt.getUpdatedAt());
        return dto;
    }

    /**
     * Validates if the given status is valid
     */
    private boolean isValidStatus(String status) {
        return status.equals("IN_PROGRESS") ||
                status.equals("COMPLETED") || status.equals("ABANDONED") ||
                status.equals("TIMED_OUT");
    }

    /**
     * Validates if the status transition is valid
     */
    private boolean isInvalidStatusTransition(String currentStatus, String newStatus) {
        // Once completed, abandoned, or timed out, status cannot be changed
        return "COMPLETED".equals(currentStatus) || "ABANDONED".equals(currentStatus) ||
                "TIMED_OUT".equals(currentStatus);
    }
}