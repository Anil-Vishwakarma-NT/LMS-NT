package com.nt.course_service_lms.serviceImplTest;

import com.nt.course_service_lms.dto.inDTO.QuizAttemptCreateInDTO;
import com.nt.course_service_lms.dto.inDTO.QuizAttemptUpdateInDTO;
import com.nt.course_service_lms.dto.outDTO.QuizAttemptOutDTO;
import com.nt.course_service_lms.entity.Quiz;
import com.nt.course_service_lms.entity.QuizAttempt;
import com.nt.course_service_lms.exception.ResourceNotValidException;
import com.nt.course_service_lms.repository.QuizAttemptRepository;
import com.nt.course_service_lms.repository.QuizRepository;
import com.nt.course_service_lms.service.serviceImpl.QuizAttemptServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuizAttemptServiceImplTest {

    @InjectMocks
    private QuizAttemptServiceImpl service;

    @Mock
    private QuizAttemptRepository attemptRepo;

    @Mock
    private QuizRepository quizRepo;

    private QuizAttemptCreateInDTO createDTO;
    private QuizAttemptUpdateInDTO updateDTO;
    private Quiz quiz;
    private QuizAttempt activeAttempt;
    private QuizAttempt savedAttempt;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        createDTO = new QuizAttemptCreateInDTO();
        createDTO.setUserId(1L);
        createDTO.setQuizId(2L);

        updateDTO = new QuizAttemptUpdateInDTO();
        updateDTO.setStatus("COMPLETED");
        updateDTO.setScoreDetails("Passed");

        quiz = new Quiz();
        quiz.setAttemptsAllowed(3);

        activeAttempt = new QuizAttempt();
        activeAttempt.setQuizId(2L);
        activeAttempt.setUserId(1L);
        activeAttempt.setAttempt(1L);
        activeAttempt.setCreatedAt(now);
        activeAttempt.setUpdatedAt(now);
        activeAttempt.setStatus("IN_PROGRESS");

        savedAttempt = new QuizAttempt();
        savedAttempt.setQuizId(2L);
        savedAttempt.setUserId(1L);
        savedAttempt.setAttempt(2L);
        savedAttempt.setCreatedAt(now);
        savedAttempt.setUpdatedAt(now);
    }

    @Test
    void createQuizAttempt_shouldReturnExistingActiveAttempt() {
        when(quizRepo.findById(2L)).thenReturn(Optional.of(quiz));
        when(attemptRepo.findActiveAttemptByUserAndQuiz(1L, 2L)).thenReturn(Optional.of(activeAttempt));

        QuizAttemptOutDTO result = service.createQuizAttempt(createDTO);

        assertEquals(2L, result.getQuizId());
        assertEquals(1L, result.getUserId());
        assertEquals(2, result.getAttemptsLeft());
    }

    @Test
    void createQuizAttempt_shouldCreateNewWhenNoneActiveAndAllowed() {
        QuizAttempt latest = new QuizAttempt();
        latest.setAttempt(1L);

        when(quizRepo.findById(2L)).thenReturn(Optional.of(quiz));
        when(attemptRepo.findActiveAttemptByUserAndQuiz(1L, 2L)).thenReturn(Optional.empty());
        when(attemptRepo.findTopByUserIdAndQuizIdOrderByAttemptDesc(1L, 2L)).thenReturn(latest);
        when(attemptRepo.save(any())).thenReturn(savedAttempt);

        QuizAttemptOutDTO result = service.createQuizAttempt(createDTO);

        assertEquals(2L, result.getAttempt());
    }

    @Test
    void createQuizAttempt_shouldThrowIfOverLimit() {
        quiz.setAttemptsAllowed(1);
        QuizAttempt existing = new QuizAttempt();
        existing.setAttempt(1L);

        when(quizRepo.findById(2L)).thenReturn(Optional.of(quiz));
        when(attemptRepo.findActiveAttemptByUserAndQuiz(1L, 2L)).thenReturn(Optional.empty());
        when(attemptRepo.findTopByUserIdAndQuizIdOrderByAttemptDesc(1L, 2L)).thenReturn(existing);

        assertThrows(ResourceNotValidException.class, () -> service.createQuizAttempt(createDTO));
    }

    @Test
    void updateQuizAttempt_shouldUpdateSuccessfully() {
        QuizAttempt existing = new QuizAttempt();
        existing.setQuizAttemptId(1L);
        existing.setStatus("IN_PROGRESS");

        when(attemptRepo.findById(1L)).thenReturn(Optional.of(existing));
        when(attemptRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        QuizAttemptOutDTO result = service.updateQuizAttempt(1L, updateDTO);

        assertEquals("COMPLETED", result.getStatus());
        assertNotNull(result.getFinishedAt());
    }

    @Test
    void updateQuizAttempt_shouldThrowIfInvalidTransition() {
        QuizAttempt existing = new QuizAttempt();
        existing.setStatus("COMPLETED");

        when(attemptRepo.findById(1L)).thenReturn(Optional.of(existing));

        assertThrows(ResourceNotValidException.class, () -> service.updateQuizAttempt(1L, updateDTO));
    }

    @Test
    void getQuizAttemptById_shouldReturnIfExists() {
        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuizAttemptId(1L);

        when(attemptRepo.findById(1L)).thenReturn(Optional.of(attempt));

        Optional<QuizAttemptOutDTO> result = service.getQuizAttemptById(1L);
        assertTrue(result.isPresent());
    }

    @Test
    void getAllQuizAttempts_shouldReturnPagedResult() {
        Pageable pageable = PageRequest.of(0, 5);
        QuizAttempt a = new QuizAttempt();
        a.setQuizAttemptId(1L);
        Page<QuizAttempt> page = new PageImpl<>(Arrays.asList(a), pageable, 1);

        when(attemptRepo.findAll(pageable)).thenReturn(page);

        Page<QuizAttemptOutDTO> result = service.getAllQuizAttempts(pageable);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getQuizAttemptsByUserAndQuiz_shouldReturnList() {
        when(attemptRepo.findByUserIdAndQuizIdOrderByAttemptDesc(1L, 2L))
                .thenReturn(Arrays.asList(new QuizAttempt()));

        List<QuizAttemptOutDTO> result = service.getQuizAttemptsByUserAndQuiz(1L, 2L);
        assertEquals(1, result.size());
    }

    @Test
    void completeAttempt_shouldCompleteSuccessfully() {
        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuizAttemptId(1L);
        attempt.setStatus("IN_PROGRESS");

        when(attemptRepo.findById(1L)).thenReturn(Optional.of(attempt));
        when(attemptRepo.save(any())).thenReturn(attempt);

        QuizAttemptOutDTO result = service.completeAttempt(1L, "A+");
        assertEquals("COMPLETED", result.getStatus());
    }

    @Test
    void abandonAttempt_shouldSetAbandoned() {
        QuizAttempt attempt = new QuizAttempt();
        attempt.setQuizAttemptId(1L);
        attempt.setStatus("IN_PROGRESS");

        when(attemptRepo.findById(1L)).thenReturn(Optional.of(attempt));
        when(attemptRepo.save(any())).thenReturn(attempt);

        QuizAttemptOutDTO result = service.abandonAttempt(1L);
        assertEquals("ABANDONED", result.getStatus());
    }

    @Test
    void existsById_shouldReturnTrueIfExists() {
        when(attemptRepo.existsById(1L)).thenReturn(true);
        assertTrue(service.existsById(1L));
    }

    @Test
    void countAttemptsByUserAndQuiz_shouldReturnCount() {
        when(attemptRepo.countByUserIdAndQuizId(1L, 2L)).thenReturn(5L);
        long count = service.countAttemptsByUserAndQuiz(1L, 2L);
        assertEquals(5L, count);
    }

    @Test
    void getQuizAttemptsByStatus_shouldReturnList() {
        when(attemptRepo.findByStatusOrderByCreatedAtDesc("COMPLETED"))
                .thenReturn(Arrays.asList(new QuizAttempt()));

        List<QuizAttemptOutDTO> result = service.getQuizAttemptsByStatus("COMPLETED");
        assertEquals(1, result.size());
    }

    @Test
    void deleteQuizAttempt_shouldRemoveIfExists() {
        when(attemptRepo.existsById(1L)).thenReturn(true);
        service.deleteQuizAttempt(1L);
        verify(attemptRepo).deleteById(1L);
    }

    @Test
    void getLatestAttemptByUserAndQuiz_shouldReturnIfPresent() {
        when(attemptRepo.findLatestAttemptByUserAndQuiz(1L, 2L))
                .thenReturn(Optional.of(new QuizAttempt()));

        Optional<QuizAttemptOutDTO> result = service.getLatestAttemptByUserAndQuiz(1L, 2L);
        assertTrue(result.isPresent());
    }
}
