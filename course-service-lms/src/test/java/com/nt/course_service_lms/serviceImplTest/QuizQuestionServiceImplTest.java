package com.nt.course_service_lms.serviceImplTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.course_service_lms.dto.inDTO.QuizQuestionInDTO;
import com.nt.course_service_lms.dto.inDTO.UpdateQuizQuestionInDTO;
import com.nt.course_service_lms.dto.outDTO.QuizQuestionOutDTO;
import com.nt.course_service_lms.entity.QuizQuestion;
import com.nt.course_service_lms.exception.ResourceNotFoundException;
import com.nt.course_service_lms.exception.ResourceNotValidException;
import com.nt.course_service_lms.repository.QuizQuestionRepository;
import com.nt.course_service_lms.repository.QuizRepository;
import com.nt.course_service_lms.service.serviceImpl.QuizQuestionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuizQuestionServiceImplTest {

    @Mock
    private QuizQuestionRepository quizQuestionRepository;

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private QuizQuestionServiceImpl quizQuestionService;

    private QuizQuestionInDTO questionInDTO;
    private UpdateQuizQuestionInDTO updateInDTO;
    private QuizQuestion question;

    @BeforeEach
    void setUp() {
        questionInDTO = new QuizQuestionInDTO();
        questionInDTO.setQuizId(1L);
        questionInDTO.setQuestionText("What is Java?");
        questionInDTO.setQuestionType("MCQ_SINGLE");
        questionInDTO.setOptions("[\"OOP\",\"Functional\"]");
        questionInDTO.setCorrectAnswer("\"OOP\"");
        questionInDTO.setPoints(new BigDecimal("5"));
        questionInDTO.setExplanation("Because it's OOP");
        questionInDTO.setRequired(true);

        updateInDTO = new UpdateQuizQuestionInDTO();
        updateInDTO.setQuestionText("Updated Text");
        updateInDTO.setQuestionType("MCQ_SINGLE");
        updateInDTO.setOptions("[\"Yes\",\"No\"]");
        updateInDTO.setCorrectAnswer("\"Yes\"");
        updateInDTO.setPoints(new BigDecimal("10"));
        updateInDTO.setExplanation("Explanation");
        updateInDTO.setRequired(false);
        updateInDTO.setPosition(1);

        question = new QuizQuestion();
        question.setQuestionId(1L);
        question.setQuizId(1L);
        question.setQuestionText("Old Text");
        question.setPosition(1);
        question.setOptions("[\"OOP\"]");
        question.setCorrectAnswer("\"OOP\"");
        question.setQuestionType("MCQ_SINGLE");
        question.setPoints(new BigDecimal("5"));
        question.setRequired(true);
        question.setExplanation("Explanation");
    }

    @Test
    void createQuestion_success() throws JsonProcessingException {
        when(quizRepository.existsById(1L)).thenReturn(true);
        when(quizQuestionRepository.findByQuizIdOrderByPosition(1L)).thenReturn(Arrays.asList());
        when(quizQuestionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        QuizQuestionOutDTO out = quizQuestionService.createQuestion(questionInDTO);

        assertNotNull(out);
        verify(quizQuestionRepository).save(any());
    }

    @Test
    void createQuestion_invalidJson_throwsException() throws JsonProcessingException {
        when(quizRepository.existsById(1L)).thenReturn(true);
        when(quizQuestionRepository.findByQuizIdOrderByPosition(1L)).thenReturn(Arrays.asList());
        doThrow(JsonProcessingException.class).when(objectMapper).readTree(anyString());

        assertThrows(ResourceNotValidException.class, () -> quizQuestionService.createQuestion(questionInDTO));
    }

    @Test
    void getAllQuestions_returnsList() {
        when(quizQuestionRepository.findAll()).thenReturn(Arrays.asList(question));
        List<QuizQuestionOutDTO> result = quizQuestionService.getAllQuestions();
        assertEquals(1, result.size());
    }

    @Test
    void getQuestionsByQuizId_success() {
        when(quizRepository.existsById(1L)).thenReturn(true);
        when(quizQuestionRepository.findByQuizIdOrderByPosition(1L)).thenReturn(Arrays.asList(question));
        List<QuizQuestionOutDTO> result = quizQuestionService.getQuestionsByQuizId(1L);
        assertEquals(1, result.size());
    }

    @Test
    void getQuestionsByQuizId_notFound() {
        when(quizRepository.existsById(1L)).thenReturn(true);
        when(quizQuestionRepository.findByQuizIdOrderByPosition(1L)).thenReturn(Arrays.asList());
        assertThrows(ResourceNotFoundException.class, () -> quizQuestionService.getQuestionsByQuizId(1L));
    }

    @Test
    void getQuestionById_success() {
        when(quizQuestionRepository.findById(1L)).thenReturn(Optional.of(question));
        QuizQuestionOutDTO result = quizQuestionService.getQuestionById(1L);
        assertEquals("Old Text", result.getQuestionText());
    }

    @Test
    void getQuestionById_notFound() {
        when(quizQuestionRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> quizQuestionService.getQuestionById(1L));
    }

    @Test
    void updateQuestion_success() throws JsonProcessingException {
        when(quizQuestionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(quizQuestionRepository.findByQuizIdOrderByPosition(1L)).thenReturn(Arrays.asList(question));
        when(quizQuestionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        QuizQuestionOutDTO result = quizQuestionService.updateQuestion(1L, updateInDTO);
        assertEquals("Updated Text", result.getQuestionText());
    }

    @Test
    void updateQuestion_invalidJson_throwsException() throws JsonProcessingException {
        when(quizQuestionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(quizQuestionRepository.findByQuizIdOrderByPosition(1L)).thenReturn(Arrays.asList(question));
        doThrow(JsonProcessingException.class).when(objectMapper).readTree(anyString());
        assertThrows(ResourceNotValidException.class, () -> quizQuestionService.updateQuestion(1L, updateInDTO));
    }

    @Test
    void deleteQuestion_success() {
        when(quizQuestionRepository.findById(1L)).thenReturn(Optional.of(question));
        when(quizQuestionRepository.findByQuizIdOrderByPosition(1L)).thenReturn(new ArrayList<>());

        assertDoesNotThrow(() -> quizQuestionService.deleteQuestion(1L));
        verify(quizQuestionRepository).delete(question);
    }

    @Test
    void deleteQuestion_notFound() {
        when(quizQuestionRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> quizQuestionService.deleteQuestion(1L));
    }
}

