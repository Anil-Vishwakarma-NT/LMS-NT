package com.nt.course_service_lms.serviceImplTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nt.course_service_lms.dto.inDTO.QuizAttemptUpdateInDTO;
import com.nt.course_service_lms.dto.inDTO.UserResponseInDTO;
import com.nt.course_service_lms.dto.outDTO.QuizAttemptOutDTO;
import com.nt.course_service_lms.dto.outDTO.QuizSubmissionResultOutDTO;
import com.nt.course_service_lms.dto.outDTO.UserResponseOutDTO;
import com.nt.course_service_lms.entity.QuizAttempt;
import com.nt.course_service_lms.entity.QuizQuestion;
import com.nt.course_service_lms.exception.ResourceNotFoundException;
import com.nt.course_service_lms.exception.ResourceNotValidException;
import com.nt.course_service_lms.repository.QuizAttemptRepository;
import com.nt.course_service_lms.repository.QuizQuestionRepository;
import com.nt.course_service_lms.service.QuizAttemptService;
import com.nt.course_service_lms.service.UserResponseService;
import com.nt.course_service_lms.service.serviceImpl.QuizSubmissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Quiz Submission Service Tests")
class QuizSubmissionServiceTest {

    @Mock
    private UserResponseService userResponseService;

    @Mock
    private QuizAttemptService quizAttemptService;

    @Mock
    private QuizAttemptRepository quizAttemptRepository;

    @Mock
    private QuizQuestionRepository quizQuestionRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private QuizSubmissionService quizSubmissionService;

    private QuizAttempt mockQuizAttempt;
    private List<UserResponseInDTO> mockUserResponses;
    private List<UserResponseOutDTO> mockUserResponsesOut;
    private List<QuizQuestion> mockQuestions;
    private QuizAttemptOutDTO mockQuizAttemptOut;

    @BeforeEach
    void setUp() {
        mockQuizAttempt = QuizAttempt.builder()
                .quizAttemptId(1L)
                .userId(100L)
                .quizId(10L)
                .attempt(1L)
                .status("IN_PROGRESS")
                .startedAt(LocalDateTime.now().minusMinutes(30))
                .createdAt(LocalDateTime.now().minusMinutes(30))
                .updatedAt(LocalDateTime.now().minusMinutes(30))
                .build();

        mockUserResponses = Arrays.asList(
                UserResponseInDTO.builder()
                        .userId(100L)
                        .quizId(10L)
                        .questionId(1L)
                        .attempt(1L)
                        .userAnswer("A")
                        .answeredAt(LocalDateTime.now())
                        .build(),
                UserResponseInDTO.builder()
                        .userId(100L)
                        .quizId(10L)
                        .questionId(2L)
                        .attempt(1L)
                        .userAnswer("B")
                        .answeredAt(LocalDateTime.now())
                        .build()
        );

        mockUserResponsesOut = Arrays.asList(
                UserResponseOutDTO.builder()
                        .responseId(1L)
                        .userId(100L)
                        .quizId(10L)
                        .questionId(1L)
                        .attempt(1L)
                        .userAnswer("A")
                        .isCorrect(true)
                        .pointsEarned(BigDecimal.valueOf(5))
                        .answeredAt(LocalDateTime.now())
                        .build(),
                UserResponseOutDTO.builder()
                        .responseId(2L)
                        .userId(100L)
                        .quizId(10L)
                        .questionId(2L)
                        .attempt(1L)
                        .userAnswer("B")
                        .isCorrect(false)
                        .pointsEarned(BigDecimal.ZERO)
                        .answeredAt(LocalDateTime.now())
                        .build()
        );

        mockQuestions = Arrays.asList(
                QuizQuestion.builder()
                        .questionId(1L)
                        .quizId(10L)
                        .questionText("Question 1")
                        .points(BigDecimal.valueOf(5))
                        .build(),
                QuizQuestion.builder()
                        .questionId(2L)
                        .quizId(10L)
                        .questionText("Question 2")
                        .points(BigDecimal.valueOf(5))
                        .build()
        );

        mockQuizAttemptOut = QuizAttemptOutDTO.builder()
                .quizAttemptId(1L)
                .userId(100L)
                .quizId(10L)
                .attempt(1L)
                .status("COMPLETED")
                .startedAt(mockQuizAttempt.getStartedAt())
                .finishedAt(LocalDateTime.now())
                .scoreDetails("{\"totalScore\":5,\"maxPossibleScore\":10}")
                .build();
    }

    @Nested
    @DisplayName("Submit Quiz Tests")
    class SubmitQuizTests {

        @Test
        @DisplayName("Should successfully submit quiz with user responses")
        void shouldSuccessfullySubmitQuizWithUserResponses() throws JsonProcessingException {
            // Given
            Long quizAttemptId = 1L;
            String submissionType = "MANUAL";

            when(quizAttemptRepository.findById(quizAttemptId)).thenReturn(Optional.of(mockQuizAttempt));
            when(userResponseService.createUserResponse(mockUserResponses)).thenReturn(mockUserResponsesOut);
            when(userResponseService.getTotalScore(100L, 10L, 1L)).thenReturn(BigDecimal.valueOf(5));
            when(userResponseService.countCorrectAnswers(100L, 10L, 1L)).thenReturn(1L);
            when(quizQuestionRepository.findAllById(anySet())).thenReturn(mockQuestions);
            when(objectMapper.writeValueAsString(any())).thenReturn("{\"totalScore\":5}");
            when(quizAttemptService.updateQuizAttempt(eq(quizAttemptId), any(QuizAttemptUpdateInDTO.class)))
                    .thenReturn(mockQuizAttemptOut);

            // When
            QuizSubmissionResultOutDTO result = quizSubmissionService.submitQuiz(
                    quizAttemptId, mockUserResponses, submissionType);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getQuizAttempt()).isEqualTo(mockQuizAttemptOut);
            assertThat(result.getUserResponses()).isEqualTo(mockUserResponsesOut);
            assertThat(result.getTotalScore()).isEqualByComparingTo(BigDecimal.valueOf(5));
            assertThat(result.getMaxPossibleScore()).isEqualByComparingTo(BigDecimal.valueOf(10));
            assertThat(result.getCorrectAnswers()).isEqualTo(1L);
            assertThat(result.getTotalQuestions()).isEqualTo(2L);
            assertThat(result.getPercentageScore()).isEqualByComparingTo(BigDecimal.valueOf(50.00));
            assertThat(result.getSubmissionType()).isEqualTo(submissionType);
            assertThat(result.getSubmittedAt()).isNotNull();

            verify(quizAttemptRepository).findById(quizAttemptId);
            verify(userResponseService).createUserResponse(mockUserResponses);
            verify(userResponseService).getTotalScore(100L, 10L, 1L);
            verify(userResponseService).countCorrectAnswers(100L, 10L, 1L);
            verify(quizQuestionRepository).findAllById(anySet());
            verify(quizAttemptService).updateQuizAttempt(eq(quizAttemptId), any(QuizAttemptUpdateInDTO.class));
        }

        @Test
        @DisplayName("Should successfully submit quiz without user responses")
        void shouldSuccessfullySubmitQuizWithoutUserResponses() throws JsonProcessingException {
            // Given
            Long quizAttemptId = 1L;
            String submissionType = "MANUAL";

            when(quizAttemptRepository.findById(quizAttemptId)).thenReturn(Optional.of(mockQuizAttempt));
            when(userResponseService.getTotalScore(100L, 10L, 1L)).thenReturn(BigDecimal.ZERO);
            when(userResponseService.countCorrectAnswers(100L, 10L, 1L)).thenReturn(0L);
            when(quizQuestionRepository.findByQuizId(10L)).thenReturn(mockQuestions);
            when(objectMapper.writeValueAsString(any())).thenReturn("{\"totalScore\":0}");
            when(quizAttemptService.updateQuizAttempt(eq(quizAttemptId), any(QuizAttemptUpdateInDTO.class)))
                    .thenReturn(mockQuizAttemptOut);

            // When
            QuizSubmissionResultOutDTO result = quizSubmissionService.submitQuiz(
                    quizAttemptId, null, submissionType);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getUserResponses()).isNull();
            assertThat(result.getTotalScore()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(result.getMaxPossibleScore()).isEqualByComparingTo(BigDecimal.valueOf(10));
            assertThat(result.getCorrectAnswers()).isEqualTo(0L);
            assertThat(result.getTotalQuestions()).isEqualTo(2L);

            verify(userResponseService, never()).createUserResponse(any());
            verify(quizQuestionRepository).findByQuizId(10L);
        }

        @Test
        @DisplayName("Should successfully submit quiz with empty user responses")
        void shouldSuccessfullySubmitQuizWithEmptyUserResponses() throws JsonProcessingException {
            // Given
            Long quizAttemptId = 1L;
            String submissionType = "MANUAL";

            when(quizAttemptRepository.findById(quizAttemptId)).thenReturn(Optional.of(mockQuizAttempt));
            when(userResponseService.getTotalScore(100L, 10L, 1L)).thenReturn(BigDecimal.ZERO);
            when(userResponseService.countCorrectAnswers(100L, 10L, 1L)).thenReturn(0L);
            when(quizQuestionRepository.findByQuizId(10L)).thenReturn(mockQuestions);
            when(objectMapper.writeValueAsString(any())).thenReturn("{\"totalScore\":0}");
            when(quizAttemptService.updateQuizAttempt(eq(quizAttemptId), any(QuizAttemptUpdateInDTO.class)))
                    .thenReturn(mockQuizAttemptOut);

            // When
            QuizSubmissionResultOutDTO result = quizSubmissionService.submitQuiz(
                    quizAttemptId, Collections.emptyList(), submissionType);

            // Then
            assertThat(result).isNotNull();
            verify(userResponseService, never()).createUserResponse(any());
            verify(quizQuestionRepository).findByQuizId(10L);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when quiz attempt not found")
        void shouldThrowResourceNotFoundExceptionWhenQuizAttemptNotFound() {
            // Given
            Long quizAttemptId = 999L;
            when(quizAttemptRepository.findById(quizAttemptId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> quizSubmissionService.submitQuiz(quizAttemptId, mockUserResponses, "MANUAL"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Quiz attempt not found with ID: " + quizAttemptId);
        }

        @Test
        @DisplayName("Should throw ResourceNotValidException when quiz attempt ID is null")
        void shouldThrowResourceNotValidExceptionWhenQuizAttemptIdIsNull() {
            // When & Then
            assertThatThrownBy(() -> quizSubmissionService.submitQuiz(null, mockUserResponses, "MANUAL"))
                    .isInstanceOf(ResourceNotValidException.class)
                    .hasMessageContaining("Quiz attempt ID cannot be null");
        }

        @Test
        @DisplayName("Should throw ResourceNotValidException when quiz attempt is not in progress")
        void shouldThrowResourceNotValidExceptionWhenQuizAttemptNotInProgress() {
            // Given
            Long quizAttemptId = 1L;
            mockQuizAttempt.setStatus("COMPLETED");
            when(quizAttemptRepository.findById(quizAttemptId)).thenReturn(Optional.of(mockQuizAttempt));

            // When & Then
            assertThatThrownBy(() -> quizSubmissionService.submitQuiz(quizAttemptId, mockUserResponses, "MANUAL"))
                    .isInstanceOf(ResourceNotValidException.class)
                    .hasMessageContaining("Quiz attempt is not in progress. Current status: COMPLETED");
        }

        @Test
        @DisplayName("Should handle ResourceNotFoundException from userResponseService.createUserResponse")
        void shouldHandleResourceNotFoundExceptionFromCreateUserResponse() {
            // Given
            Long quizAttemptId = 1L;
            when(quizAttemptRepository.findById(quizAttemptId)).thenReturn(Optional.of(mockQuizAttempt));
            when(userResponseService.createUserResponse(mockUserResponses))
                    .thenThrow(new ResourceNotFoundException("User response resource not found"));

            // When & Then
            assertThatThrownBy(() -> quizSubmissionService.submitQuiz(quizAttemptId, mockUserResponses, "MANUAL"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("User response resource not found");
        }

        @Test
        @DisplayName("Should handle ResourceNotValidException from userResponseService.createUserResponse")
        void shouldHandleResourceNotValidExceptionFromCreateUserResponse() {
            // Given
            Long quizAttemptId = 1L;
            when(quizAttemptRepository.findById(quizAttemptId)).thenReturn(Optional.of(mockQuizAttempt));
            when(userResponseService.createUserResponse(mockUserResponses))
                    .thenThrow(new ResourceNotValidException("Invalid user response data"));

            // When & Then
            assertThatThrownBy(() -> quizSubmissionService.submitQuiz(quizAttemptId, mockUserResponses, "MANUAL"))
                    .isInstanceOf(ResourceNotValidException.class)
                    .hasMessageContaining("Invalid user response data");
        }

        @Test
        @DisplayName("Should handle RuntimeException from userResponseService.createUserResponse")
        void shouldHandleRuntimeExceptionFromCreateUserResponse() {
            // Given
            Long quizAttemptId = 1L;
            when(quizAttemptRepository.findById(quizAttemptId)).thenReturn(Optional.of(mockQuizAttempt));
            when(userResponseService.createUserResponse(mockUserResponses))
                    .thenThrow(new RuntimeException("Database connection error"));

            // When & Then
            assertThatThrownBy(() -> quizSubmissionService.submitQuiz(quizAttemptId, mockUserResponses, "MANUAL"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Failed to save user responses");
        }

        @Test
        @DisplayName("Should handle ResourceNotFoundException from getTotalScore")
        void shouldHandleResourceNotFoundExceptionFromGetTotalScore() {
            // Given
            Long quizAttemptId = 1L;
            when(quizAttemptRepository.findById(quizAttemptId)).thenReturn(Optional.of(mockQuizAttempt));
            when(userResponseService.createUserResponse(mockUserResponses)).thenReturn(mockUserResponsesOut);
            when(userResponseService.getTotalScore(100L, 10L, 1L))
                    .thenThrow(new ResourceNotFoundException("Score not found"));

            // When & Then
            assertThatThrownBy(() -> quizSubmissionService.submitQuiz(quizAttemptId, mockUserResponses, "MANUAL"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Score not found");
        }

    }

    @Nested
    @DisplayName("Submit Quiz On Timeout Tests")
    class SubmitQuizOnTimeoutTests {

        @Test
        @DisplayName("Should successfully submit quiz on timeout")
        void shouldSuccessfullySubmitQuizOnTimeout() throws JsonProcessingException {
            // Given
            Long quizAttemptId = 1L;

            when(quizAttemptRepository.findById(quizAttemptId)).thenReturn(Optional.of(mockQuizAttempt));
            when(userResponseService.createUserResponse(mockUserResponses)).thenReturn(mockUserResponsesOut);
            when(userResponseService.getTotalScore(100L, 10L, 1L)).thenReturn(BigDecimal.valueOf(5));
            when(userResponseService.countCorrectAnswers(100L, 10L, 1L)).thenReturn(1L);
            when(quizQuestionRepository.findAllById(anySet())).thenReturn(mockQuestions);
            when(objectMapper.writeValueAsString(any())).thenReturn("{\"totalScore\":5}");

            QuizAttemptOutDTO timedOutAttempt = QuizAttemptOutDTO.builder()
                    .quizAttemptId(1L)
                    .status("TIMED_OUT")
                    .build();
            when(quizAttemptService.updateQuizAttempt(eq(quizAttemptId), any(QuizAttemptUpdateInDTO.class)))
                    .thenReturn(timedOutAttempt);

            // When
            QuizSubmissionResultOutDTO result = quizSubmissionService.submitQuizOnTimeout(
                    quizAttemptId, mockUserResponses);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getSubmissionType()).isEqualTo("AUTO_TIMEOUT");
            assertThat(result.getQuizAttempt().getStatus()).isEqualTo("TIMED_OUT");
        }

        @Test
        @DisplayName("Should handle ResourceNotFoundException on timeout")
        void shouldHandleResourceNotFoundExceptionOnTimeout() {
            // Given
            Long quizAttemptId = 999L;
            when(quizAttemptRepository.findById(quizAttemptId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> quizSubmissionService.submitQuizOnTimeout(quizAttemptId, mockUserResponses))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("Should handle ResourceNotValidException on timeout")
        void shouldHandleResourceNotValidExceptionOnTimeout() {
            // Given
            Long quizAttemptId = 1L;
            mockQuizAttempt.setStatus("COMPLETED");
            when(quizAttemptRepository.findById(quizAttemptId)).thenReturn(Optional.of(mockQuizAttempt));

            // When & Then
            assertThatThrownBy(() -> quizSubmissionService.submitQuizOnTimeout(quizAttemptId, mockUserResponses))
                    .isInstanceOf(ResourceNotValidException.class);
        }

        @Test
        @DisplayName("Should handle RuntimeException on timeout")
        void shouldHandleRuntimeExceptionOnTimeout() {
            // Given
            Long quizAttemptId = 1L;
            when(quizAttemptRepository.findById(quizAttemptId)).thenReturn(Optional.of(mockQuizAttempt));
            when(userResponseService.createUserResponse(mockUserResponses))
                    .thenThrow(new RuntimeException("Database error"));

            // When & Then
            assertThatThrownBy(() -> quizSubmissionService.submitQuizOnTimeout(quizAttemptId, mockUserResponses))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Failed to submit quiz on timeout");
        }

        @Nested
        @DisplayName("Score Calculation Tests")
        class ScoreCalculationTests {

            @Test
            @DisplayName("Should calculate percentage score correctly with non-zero max score")
            void shouldCalculatePercentageScoreCorrectlyWithNonZeroMaxScore() throws JsonProcessingException {
                // Given
                Long quizAttemptId = 1L;
                when(quizAttemptRepository.findById(quizAttemptId)).thenReturn(Optional.of(mockQuizAttempt));
                when(userResponseService.createUserResponse(mockUserResponses)).thenReturn(mockUserResponsesOut);
                when(userResponseService.getTotalScore(100L, 10L, 1L)).thenReturn(BigDecimal.valueOf(7));
                when(userResponseService.countCorrectAnswers(100L, 10L, 1L)).thenReturn(1L);
                when(quizQuestionRepository.findAllById(anySet())).thenReturn(mockQuestions);
                when(objectMapper.writeValueAsString(any())).thenReturn("{\"totalScore\":7}");
                when(quizAttemptService.updateQuizAttempt(eq(quizAttemptId), any(QuizAttemptUpdateInDTO.class)))
                        .thenReturn(mockQuizAttemptOut);

                // When
                QuizSubmissionResultOutDTO result = quizSubmissionService.submitQuiz(
                        quizAttemptId, mockUserResponses, "MANUAL");

                // Then
                assertThat(result.getPercentageScore()).isEqualByComparingTo(BigDecimal.valueOf(70.00));
            }

            @Test
            @DisplayName("Should calculate percentage score as zero when max score is zero")
            void shouldCalculatePercentageScoreAsZeroWhenMaxScoreIsZero() throws JsonProcessingException {
                // Given
                Long quizAttemptId = 1L;
                List<QuizQuestion> zeroPointQuestions = Arrays.asList(
                        QuizQuestion.builder().questionId(1L).points(BigDecimal.ZERO).build(),
                        QuizQuestion.builder().questionId(2L).points(BigDecimal.ZERO).build()
                );

                when(quizAttemptRepository.findById(quizAttemptId)).thenReturn(Optional.of(mockQuizAttempt));
                when(userResponseService.createUserResponse(mockUserResponses)).thenReturn(mockUserResponsesOut);
                when(userResponseService.getTotalScore(100L, 10L, 1L)).thenReturn(BigDecimal.valueOf(5));
                when(userResponseService.countCorrectAnswers(100L, 10L, 1L)).thenReturn(1L);
                when(quizQuestionRepository.findAllById(anySet())).thenReturn(zeroPointQuestions);
                when(objectMapper.writeValueAsString(any())).thenReturn("{\"totalScore\":5}");
                when(quizAttemptService.updateQuizAttempt(eq(quizAttemptId), any(QuizAttemptUpdateInDTO.class)))
                        .thenReturn(mockQuizAttemptOut);

                // When
                QuizSubmissionResultOutDTO result = quizSubmissionService.submitQuiz(
                        quizAttemptId, mockUserResponses, "MANUAL");

                // Then
                assertThat(result.getPercentageScore()).isEqualByComparingTo(BigDecimal.ZERO);
            }

            @Test
            @DisplayName("Should handle null total score")
            void shouldHandleNullTotalScore() throws JsonProcessingException {
                // Given
                Long quizAttemptId = 1L;
                when(quizAttemptRepository.findById(quizAttemptId)).thenReturn(Optional.of(mockQuizAttempt));
                when(userResponseService.createUserResponse(mockUserResponses)).thenReturn(mockUserResponsesOut);
                when(userResponseService.getTotalScore(100L, 10L, 1L)).thenReturn(null);
                when(userResponseService.countCorrectAnswers(100L, 10L, 1L)).thenReturn(1L);
                when(quizQuestionRepository.findAllById(anySet())).thenReturn(mockQuestions);
                when(objectMapper.writeValueAsString(any())).thenReturn("{\"totalScore\":0}");
                when(quizAttemptService.updateQuizAttempt(eq(quizAttemptId), any(QuizAttemptUpdateInDTO.class)))
                        .thenReturn(mockQuizAttemptOut);

                // When
                QuizSubmissionResultOutDTO result = quizSubmissionService.submitQuiz(
                        quizAttemptId, mockUserResponses, "MANUAL");

                // Then
                assertThat(result.getTotalScore()).isEqualByComparingTo(BigDecimal.ZERO);
            }

            @Test
            @DisplayName("Should handle null correct answers count")
            void shouldHandleNullCorrectAnswersCount() throws JsonProcessingException {
                // Given
                Long quizAttemptId = 1L;
                when(quizAttemptRepository.findById(quizAttemptId)).thenReturn(Optional.of(mockQuizAttempt));
                when(userResponseService.createUserResponse(mockUserResponses)).thenReturn(mockUserResponsesOut);
                when(userResponseService.getTotalScore(100L, 10L, 1L)).thenReturn(BigDecimal.valueOf(5));
                when(userResponseService.countCorrectAnswers(100L, 10L, 1L)).thenReturn(null);
                when(quizQuestionRepository.findAllById(anySet())).thenReturn(mockQuestions);
                when(objectMapper.writeValueAsString(any())).thenReturn("{\"totalScore\":5}");
                when(quizAttemptService.updateQuizAttempt(eq(quizAttemptId), any(QuizAttemptUpdateInDTO.class)))
                        .thenReturn(mockQuizAttemptOut);

                // When
                QuizSubmissionResultOutDTO result = quizSubmissionService.submitQuiz(
                        quizAttemptId, mockUserResponses, "MANUAL");

                // Then
                assertThat(result.getCorrectAnswers()).isEqualTo(0L);
            }
        }
    }
}