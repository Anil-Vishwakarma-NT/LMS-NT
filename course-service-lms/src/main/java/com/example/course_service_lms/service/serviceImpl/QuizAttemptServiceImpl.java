package com.example.course_service_lms.service.serviceImpl;


import com.example.course_service_lms.dto.inDTO.QuizAttemptCreateInDTO;
import com.example.course_service_lms.dto.inDTO.QuizAttemptUpdateInDTO;
import com.example.course_service_lms.entity.QuizAttempt;
import com.example.course_service_lms.exception.ResourceAlreadyExistsException;
import com.example.course_service_lms.exception.ResourceNotFoundException;
import com.example.course_service_lms.exception.ResourceNotValidException;
import com.example.course_service_lms.repository.QuizAttemptRepository;
import com.example.course_service_lms.service.QuizAttemptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for QuizAttempt operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class QuizAttemptServiceImpl implements QuizAttemptService {

    private final QuizAttemptRepository quizAttemptRepository;

    @Override
    public QuizAttempt createQuizAttempt(QuizAttemptCreateInDTO dto) {
        log.info("Creating new quiz attempt for user: {} and quiz: {}", dto.getUserId(), dto.getQuizId());

        // Validate input
        if (dto.getUserId() == null || dto.getQuizId() == null) {
            throw new ResourceNotValidException("User ID and Quiz ID are required");
        }

        // Check if user has any active attempts for this quiz
        if (quizAttemptRepository.hasActiveAttempt(dto.getUserId(), dto.getQuizId())) {
            throw new ResourceAlreadyExistsException("User already has an active attempt for this quiz");
        }

        QuizAttempt quizAttempt = new QuizAttempt();
        quizAttempt.setAttempt(dto.getAttempt());
        quizAttempt.setQuizId(dto.getQuizId());
        quizAttempt.setUserId(dto.getUserId());
        quizAttempt.setStartedAt(LocalDateTime.now());
        quizAttempt.setStatus("STARTED");
        quizAttempt.setCreatedAt(LocalDateTime.now());
        quizAttempt.setUpdatedAt(LocalDateTime.now());

        QuizAttempt savedAttempt = quizAttemptRepository.save(quizAttempt);
        log.info("Created quiz attempt with ID: {}", savedAttempt.getQuizAttemptId());
        return savedAttempt;
    }

    @Override
    public QuizAttempt updateQuizAttempt(Long quizAttemptId, QuizAttemptUpdateInDTO dto) {
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
        return updatedAttempt;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<QuizAttempt> getQuizAttemptById(Long quizAttemptId) {
        log.debug("Fetching quiz attempt with ID: {}", quizAttemptId);

        if (quizAttemptId == null) {
            throw new ResourceNotValidException("Quiz attempt ID cannot be null");
        }

        return quizAttemptRepository.findById(quizAttemptId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<QuizAttempt> getAllQuizAttempts(Pageable pageable) {
        log.debug("Fetching all quiz attempts with pagination");

        if (pageable == null) {
            throw new ResourceNotValidException("Pageable cannot be null");
        }

        return quizAttemptRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizAttempt> getQuizAttemptsByUserId(Long userId) {
        log.debug("Fetching quiz attempts for user: {}", userId);

        if (userId == null) {
            throw new ResourceNotValidException("User ID cannot be null");
        }

        return quizAttemptRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizAttempt> getQuizAttemptsByQuizId(Long quizId) {
        log.debug("Fetching quiz attempts for quiz: {}", quizId);

        if (quizId == null) {
            throw new ResourceNotValidException("Quiz ID cannot be null");
        }

        return quizAttemptRepository.findByQuizIdOrderByCreatedAtDesc(quizId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizAttempt> getQuizAttemptsByUserAndQuiz(Long userId, Long quizId) {
        log.debug("Fetching quiz attempts for user: {} and quiz: {}", userId, quizId);

        if (userId == null || quizId == null) {
            throw new ResourceNotValidException("User ID and Quiz ID cannot be null");
        }

        return quizAttemptRepository.findByUserIdAndQuizIdOrderByAttemptDesc(userId, quizId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizAttempt> getQuizAttemptsByStatus(String status) {
        log.debug("Fetching quiz attempts with status: {}", status);

        if (status == null || status.trim().isEmpty()) {
            throw new ResourceNotValidException("Status cannot be null or empty");
        }

        if (!isValidStatus(status)) {
            throw new ResourceNotValidException("Invalid status: " + status);
        }

        return quizAttemptRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<QuizAttempt> getLatestAttemptByUserAndQuiz(Long userId, Long quizId) {
        log.debug("Fetching latest attempt for user: {} and quiz: {}", userId, quizId);

        if (userId == null || quizId == null) {
            throw new ResourceNotValidException("User ID and Quiz ID cannot be null");
        }

        return quizAttemptRepository.findLatestAttemptByUserAndQuiz(userId, quizId);
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
    public QuizAttempt completeAttempt(Long quizAttemptId, String scoreDetails) {
        log.info("Completing quiz attempt with ID: {}", quizAttemptId);

        if (quizAttemptId == null) {
            throw new ResourceNotValidException("Quiz attempt ID cannot be null");
        }

        QuizAttempt attempt = quizAttemptRepository.findById(quizAttemptId)
                .orElseThrow(() -> new ResourceNotFoundException("QuizAttempt not found with ID: " + quizAttemptId));

        // Validate that attempt can be completed
        if (!"STARTED".equals(attempt.getStatus()) && !"IN_PROGRESS".equals(attempt.getStatus())) {
            throw new ResourceNotValidException("Cannot complete quiz attempt with status: " + attempt.getStatus());
        }

        attempt.setStatus("COMPLETED");
        attempt.setFinishedAt(LocalDateTime.now());
        attempt.setScoreDetails(scoreDetails);
        attempt.setUpdatedAt(LocalDateTime.now());

        return quizAttemptRepository.save(attempt);
    }

    @Override
    public QuizAttempt abandonAttempt(Long quizAttemptId) {
        log.info("Abandoning quiz attempt with ID: {}", quizAttemptId);

        if (quizAttemptId == null) {
            throw new ResourceNotValidException("Quiz attempt ID cannot be null");
        }

        QuizAttempt attempt = quizAttemptRepository.findById(quizAttemptId)
                .orElseThrow(() -> new ResourceNotFoundException("QuizAttempt not found with ID: " + quizAttemptId));

        // Validate that attempt can be abandoned
        if (!"STARTED".equals(attempt.getStatus()) && !"IN_PROGRESS".equals(attempt.getStatus())) {
            throw new ResourceNotValidException("Cannot abandon quiz attempt with status: " + attempt.getStatus());
        }

        attempt.setStatus("ABANDONED");
        attempt.setFinishedAt(LocalDateTime.now());
        attempt.setUpdatedAt(LocalDateTime.now());

        return quizAttemptRepository.save(attempt);
    }

    @Override
    public QuizAttempt timeOutAttempt(Long quizAttemptId) {
        log.info("Timing out quiz attempt with ID: {}", quizAttemptId);

        if (quizAttemptId == null) {
            throw new ResourceNotValidException("Quiz attempt ID cannot be null");
        }

        QuizAttempt attempt = quizAttemptRepository.findById(quizAttemptId)
                .orElseThrow(() -> new ResourceNotFoundException("QuizAttempt not found with ID: " + quizAttemptId));

        // Validate that attempt can be timed out
        if (!"STARTED".equals(attempt.getStatus()) && !"IN_PROGRESS".equals(attempt.getStatus())) {
            throw new ResourceNotValidException("Cannot time out quiz attempt with status: " + attempt.getStatus());
        }

        attempt.setStatus("TIMED_OUT");
        attempt.setFinishedAt(LocalDateTime.now());
        attempt.setUpdatedAt(LocalDateTime.now());

        return quizAttemptRepository.save(attempt);
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
     * Validates if the given status is valid
     */
    private boolean isValidStatus(String status) {
        return status.equals("STARTED") || status.equals("IN_PROGRESS") ||
                status.equals("COMPLETED") || status.equals("ABANDONED") ||
                status.equals("TIMED_OUT");
    }

    /**
     * Validates if the status transition is valid
     */
    private boolean isInvalidStatusTransition(String currentStatus, String newStatus) {
        // Once completed, abandoned, or timed out, status cannot be changed
        if ("COMPLETED".equals(currentStatus) || "ABANDONED".equals(currentStatus) ||
                "TIMED_OUT".equals(currentStatus)) {
            return true;
        }

        // Other transitions are generally valid
        return false;
    }
}