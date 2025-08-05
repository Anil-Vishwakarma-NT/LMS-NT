package com.nt.course_service_lms.serviceImplTest;

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
import com.nt.course_service_lms.service.serviceImpl.QuizServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuizServiceImplTest {

    @Mock
    private QuizRepository quizRepository;
    @Mock
    private CourseRepository courseRepository;
    @Mock
    private CourseContentRepository courseContentRepository;
    @Mock
    private QuizConverter quizConverter;

    @InjectMocks
    private QuizServiceImpl quizService;

    private Quiz quiz;
    private QuizCreateInDTO createDTO;
    private QuizUpdateInDTO updateDTO;
    private QuizOutDTO outDTO;

    @BeforeEach
    void setup() {
        quiz = Quiz.builder()
                .quizId(1L)
                .parentType("course")
                .parentId(100L)
                .title("Java Quiz")
                .description("Test your Java knowledge")
                .timeLimit(30)
                .attemptsAllowed(3)
                .passingScore(new BigDecimal("70.00"))
                .randomizeQuestions(true)
                .showResults(true)
                .isActive(true)
                .createdBy(10)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createDTO = QuizCreateInDTO.builder()
                .parentType("course")
                .parentId(100L)
                .title("Java Quiz")
                .description("Test your Java knowledge")
                .timeLimit(30)
                .attemptsAllowed(3)
                .passingScore(new BigDecimal("70.00"))
                .randomizeQuestions(true)
                .showResults(true)
                .isActive(true)
                .createdBy(10)
                .build();

        updateDTO = QuizUpdateInDTO.builder()
                .title("Advanced Java Quiz")
                .description("Updated desc")
                .timeLimit(45)
                .attemptsAllowed(2)
                .passingScore(new BigDecimal("80.00"))
                .randomizeQuestions(false)
                .showResults(false)
                .isActive(true)
                .build();

        outDTO = new QuizOutDTO();
        outDTO.setQuizId(1L);
        outDTO.setTitle("Java Quiz");
    }

    // ----------- createQuiz -----------

    @Test
    void testCreateQuiz_success_courseParent() {
        when(courseRepository.existsById(100L)).thenReturn(true);
        when(quizRepository.existsByTitleAndParentTypeAndParentId(any(), any(), any())).thenReturn(false);
        when(quizConverter.toEntity(createDTO)).thenReturn(quiz);
        when(quizRepository.save(quiz)).thenReturn(quiz);
        when(quizConverter.toOutDTO(quiz)).thenReturn(outDTO);

        QuizOutDTO result = quizService.createQuiz(createDTO);

        assertNotNull(result);
        verify(quizRepository).save(quiz);
    }

    @Test
    void testCreateQuiz_parentNotFound_course() {
        when(courseRepository.existsById(100L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> quizService.createQuiz(createDTO));
    }

    @Test
    void testCreateQuiz_duplicateQuiz() {
        when(courseRepository.existsById(100L)).thenReturn(true);
        when(quizRepository.existsByTitleAndParentTypeAndParentId(any(), any(), any())).thenReturn(true);
        assertThrows(ResourceAlreadyExistsException.class, () -> quizService.createQuiz(createDTO));
    }

    @Test
    void testCreateQuiz_courseContentParentNotFound() {
        createDTO.setParentType("course-content");
        when(courseContentRepository.existsById(100L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> quizService.createQuiz(createDTO));
    }

    @Test
    void testCreateQuiz_runtimeException() {
        when(courseRepository.existsById(100L)).thenReturn(true);
        when(quizRepository.existsByTitleAndParentTypeAndParentId(any(), any(), any())).thenThrow(new RuntimeException("DB error"));
        assertThrows(RuntimeException.class, () -> quizService.createQuiz(createDTO));
    }

    // ----------- getAllQuizzes -----------

    @Test
    void testGetAllQuizzes_success() {
        when(quizRepository.findByIsActiveTrue()).thenReturn(Arrays.asList(quiz));
        when(quizConverter.toOutDTO(any())).thenReturn(outDTO);
        List<QuizOutDTO> result = quizService.getAllQuizzes();
        assertEquals(1, result.size());
    }

    @Test
    void testGetAllQuizzes_noData() {
        when(quizRepository.findByIsActiveTrue()).thenReturn(Collections.emptyList());
        assertThrows(ResourceNotFoundException.class, () -> quizService.getAllQuizzes());
    }

    @Test
    void testGetAllQuizzes_runtimeException() {
        when(quizRepository.findByIsActiveTrue()).thenThrow(RuntimeException.class);
        assertThrows(RuntimeException.class, () -> quizService.getAllQuizzes());
    }

    // ----------- getQuizById -----------

    @Test
    void testGetQuizById_success() {
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        when(quizConverter.toOutDTO(quiz)).thenReturn(outDTO);
        QuizOutDTO result = quizService.getQuizById(1L);
        assertEquals("Java Quiz", result.getTitle());
    }

    @Test
    void testGetQuizById_notFound() {
        when(quizRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> quizService.getQuizById(1L));
    }

    @Test
    void testGetQuizById_runtimeException() {
        when(quizRepository.findById(1L)).thenThrow(RuntimeException.class);
        assertThrows(RuntimeException.class, () -> quizService.getQuizById(1L));
    }

    // ----------- getQuizzesByCourse -----------

    @Test
    void testGetQuizzesByCourse_success() {
        when(quizRepository.findByParentTypeAndParentIdAndIsActiveTrue("course", 100L)).thenReturn(Arrays.asList(quiz));
        when(quizConverter.toOutDTO(any())).thenReturn(outDTO);
        List<QuizOutDTO> result = quizService.getQuizzesByCourse(100L);
        assertEquals(1, result.size());
    }

    @Test
    void testGetQuizzesByCourse_notFound() {
        when(quizRepository.findByParentTypeAndParentIdAndIsActiveTrue("course", 100L)).thenReturn(Collections.emptyList());
        assertThrows(ResourceNotFoundException.class, () -> quizService.getQuizzesByCourse(100L));
    }

    @Test
    void testGetQuizzesByCourse_runtimeException() {
        when(quizRepository.findByParentTypeAndParentIdAndIsActiveTrue(any(), any())).thenThrow(RuntimeException.class);
        assertThrows(RuntimeException.class, () -> quizService.getQuizzesByCourse(100L));
    }

    // ----------- getQuizzesByCourseContent -----------

    @Test
    void testGetQuizzesByCourseContent_success() {
        when(quizRepository.findByParentTypeAndParentIdAndIsActiveTrue("course-content", 100L)).thenReturn(Arrays.asList(quiz));
        when(quizConverter.toOutDTO(any())).thenReturn(outDTO);
        List<QuizOutDTO> result = quizService.getQuizzesByCourseContent(100L);
        assertEquals(1, result.size());
    }

    @Test
    void testGetQuizzesByCourseContent_notFound() {
        when(quizRepository.findByParentTypeAndParentIdAndIsActiveTrue("course-content", 100L)).thenReturn(Collections.emptyList());
        assertThrows(ResourceNotFoundException.class, () -> quizService.getQuizzesByCourseContent(100L));
    }

    @Test
    void testGetQuizzesByCourseContent_runtimeException() {
        when(quizRepository.findByParentTypeAndParentIdAndIsActiveTrue(any(), any())).thenThrow(RuntimeException.class);
        assertThrows(RuntimeException.class, () -> quizService.getQuizzesByCourseContent(100L));
    }

    // ----------- updateQuiz -----------

    @Test
    void testUpdateQuiz_success() {
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        when(quizRepository.existsByTitleAndParentTypeAndParentId(any(), any(), any())).thenReturn(false);
        when(quizConverter.updateEntity(any(), any())).thenReturn(quiz);
        when(quizRepository.save(any())).thenReturn(quiz);
        when(quizConverter.toOutDTO(any())).thenReturn(outDTO);
        QuizOutDTO result = quizService.updateQuiz(1L, updateDTO);
        assertEquals("Java Quiz", result.getTitle());
    }

    @Test
    void testUpdateQuiz_notFound() {
        when(quizRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> quizService.updateQuiz(1L, updateDTO));
    }

    @Test
    void testUpdateQuiz_duplicateTitle() {
        Quiz conflicting = Quiz.builder().quizId(2L).title("Advanced Java Quiz").build();
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        when(quizRepository.existsByTitleAndParentTypeAndParentId(any(), any(), any())).thenReturn(true);
        assertThrows(ResourceAlreadyExistsException.class, () -> quizService.updateQuiz(1L, updateDTO));
    }

    @Test
    void testUpdateQuiz_runtimeException() {
        when(quizRepository.findById(1L)).thenThrow(RuntimeException.class);
        assertThrows(RuntimeException.class, () -> quizService.updateQuiz(1L, updateDTO));
    }

    // ----------- deleteQuiz -----------

    @Test
    void testDeleteQuiz_success() {
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        quizService.deleteQuiz(1L);
        verify(quizRepository).save(quiz);
        assertFalse(quiz.getIsActive());
    }

    @Test
    void testDeleteQuiz_notFound() {
        when(quizRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> quizService.deleteQuiz(1L));
    }

    @Test
    void testDeleteQuiz_runtimeException() {
        when(quizRepository.findById(1L)).thenThrow(RuntimeException.class);
        assertThrows(RuntimeException.class, () -> quizService.deleteQuiz(1L));
    }
}

