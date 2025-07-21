package com.nt.course_service_lms.serviceImplTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nt.course_service_lms.converters.UserResponseConverter;
import com.nt.course_service_lms.dto.inDTO.UserResponseInDTO;
import com.nt.course_service_lms.dto.inDTO.UserResponseUpdateInDTO;
import com.nt.course_service_lms.dto.outDTO.UserResponseOutDTO;
import com.nt.course_service_lms.entity.QuizQuestion;
import com.nt.course_service_lms.entity.UserResponse;
import com.nt.course_service_lms.exception.ResourceAlreadyExistsException;
import com.nt.course_service_lms.exception.ResourceNotFoundException;
import com.nt.course_service_lms.repository.QuizQuestionRepository;
import com.nt.course_service_lms.repository.UserResponseRepository;
import com.nt.course_service_lms.service.serviceImpl.UserResponseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserResponseServiceImpl Tests")
class UserResponseServiceImplTest {

    @Mock
    private UserResponseRepository userResponseRepository;

    @Mock
    private QuizQuestionRepository quizQuestionRepository;

    @Mock
    private UserResponseConverter userResponseConverter;

    @InjectMocks
    private UserResponseServiceImpl userResponseService;

    private UserResponseInDTO userResponseInDTO;
    private UserResponse userResponse;
    private UserResponseOutDTO userResponseOutDTO;
    private QuizQuestion quizQuestion;

    @BeforeEach
    void setUp() {
        userResponseInDTO = UserResponseInDTO.builder()
                .userId(1L).quizId(1L).questionId(101L).attempt(1L).userAnswer("[\"a\"]").build();

        quizQuestion = new QuizQuestion();
        quizQuestion.setQuestionId(101L);
        quizQuestion.setQuizId(1L);
        quizQuestion.setQuestionType("mcq_single");
        quizQuestion.setCorrectAnswer("[\"a\"]");
        quizQuestion.setPoints(BigDecimal.TEN);

        userResponse = new UserResponse();
        userResponse.setResponseId(1L);
        userResponse.setUserId(1L);
        userResponse.setQuizId(1L);
        userResponse.setQuestionId(101L);
        userResponse.setAttempt(1L);
        userResponse.setUserAnswer("[\"a\"]");
        userResponse.setIsCorrect(true);
        userResponse.setPointsEarned(BigDecimal.TEN);
        userResponse.setAnsweredAt(LocalDateTime.now());

        userResponseOutDTO = new UserResponseOutDTO();
        userResponseOutDTO.setResponseId(1L);
        userResponseOutDTO.setUserId(1L);
    }

    @Nested
    @DisplayName("Create User Response Tests")
    class CreateUserResponseTests {

        @Test
        @DisplayName("Should create user responses successfully for valid input")
        void createUserResponse_Success() {
            // Arrange
            when(userResponseRepository.existsByUserIdAndQuestionIdAndAttempt(anyLong(), anyLong(), anyLong())).thenReturn(false);
            when(quizQuestionRepository.findAllById(any(Set.class))).thenReturn(List.of(quizQuestion));
            when(userResponseConverter.convertToEntity(any(UserResponseInDTO.class))).thenReturn(userResponse);
            when(userResponseRepository.saveAll(anyList())).thenReturn(List.of(userResponse));
            when(userResponseConverter.convertToOutDTOList(anyList())).thenReturn(List.of(userResponseOutDTO));

            // Act
            List<UserResponseOutDTO> result = userResponseService.createUserResponse(List.of(userResponseInDTO));

            // Assert
            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertEquals(1L, result.get(0).getUserId());
            verify(userResponseRepository, times(1)).saveAll(anyList());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException for empty list")
        void createUserResponse_ThrowsIllegalArgumentException_ForEmptyList() {
            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                userResponseService.createUserResponse(Collections.emptyList());
            });
        }

        @Test
        @DisplayName("Should throw ResourceAlreadyExistsException if response exists")
        void createUserResponse_ThrowsResourceAlreadyExistsException() {
            // Arrange
            when(userResponseRepository.existsByUserIdAndQuestionIdAndAttempt(1L, 101L, 1L)).thenReturn(true);

            // Act & Assert
            assertThrows(ResourceAlreadyExistsException.class, () -> {
                userResponseService.createUserResponse(List.of(userResponseInDTO));
            });
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException if question not found")
        void createUserResponse_ThrowsResourceNotFoundException() {
            // Arrange
            when(userResponseRepository.existsByUserIdAndQuestionIdAndAttempt(anyLong(), anyLong(), anyLong())).thenReturn(false);
            when(quizQuestionRepository.findAllById(any(Set.class))).thenReturn(Collections.emptyList());
            when(userResponseConverter.convertToEntity(any(UserResponseInDTO.class))).thenReturn(userResponse);

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> {
                userResponseService.createUserResponse(List.of(userResponseInDTO));
            });
        }

//        @Test
//        @DisplayName("Should handle various question types and answer formats correctly")
//        void createUserResponse_AnswerValidation() {
//            // Arrange
//            // Correct MCQ Multiple
//            UserResponseInDTO mcqMultiCorrect = UserResponseInDTO.builder().userId(1L).quizId(1L).questionId(102L).attempt(1L).userAnswer("[\"a\",\"c\"]").build();
//            QuizQuestion questionMcqMulti = new QuizQuestion();
//            questionMcqMulti.setQuestionId(102L);
//            questionMcqMulti.setQuestionType("mcq_multiple");
//            questionMcqMulti.setCorrectAnswer("[\"c\",\"a\"]"); // Note order difference
//            questionMcqMulti.setPoints(BigDecimal.TEN);
//
//            // Incorrect Text
//            UserResponseInDTO textIncorrect = UserResponseInDTO.builder().userId(1L).quizId(1L).questionId(103L).attempt(1L).userAnswer("wrong answer").build();
//            QuizQuestion questionText = new QuizQuestion();
//            questionText.setQuestionId(103L);
//            questionText.setQuestionType("text");
//            questionText.setCorrectAnswer("correct answer");
//            questionText.setPoints(BigDecimal.TEN);
//
//            List<UserResponseInDTO> dtoList = List.of(mcqMultiCorrect, textIncorrect);
//
//            when(userResponseRepository.existsByUserIdAndQuestionIdAndAttempt(anyLong(), anyLong(), anyLong())).thenReturn(false);
//            when(quizQuestionRepository.findAllById(anySet())).thenReturn(List.of(questionMcqMulti, questionText));
//
//            // Mock converter to return what's needed
//            UserResponse responseMulti = new UserResponse(); // Simulating conversion
//            responseMulti.setQuestionId(102L);
//            UserResponse responseText = new UserResponse();
//            responseText.setQuestionId(103L);
//            when(userResponseConverter.convertToEntity(mcqMultiCorrect)).thenReturn(responseMulti);
//            when(userResponseConverter.convertToEntity(textIncorrect)).thenReturn(responseText);
//
//            when(userResponseRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0)); // Return the input list
//            when(userResponseConverter.convertToOutDTOList(anyList())).thenAnswer(invocation -> List.of(new UserResponseOutDTO(), new UserResponseOutDTO()));
//
//            // Act
//            userResponseService.createUserResponse(dtoList);
//
//            // Assert
//            verify(userResponseRepository).saveAll(argThat(list -> {
//                UserResponse savedMulti = list.stream().filter(r -> r.getQuestionId() == 102L).findFirst().orElse(null);
//                UserResponse savedText = list.stream().filter(r -> r.getQuestionId() == 103L).findFirst().orElse(null);
//
//                assertNotNull(savedMulti);
//                assertTrue(savedMulti.getIsCorrect());
//                assertEquals(0, BigDecimal.TEN.compareTo(savedMulti.getPointsEarned()));
//
//                assertNotNull(savedText);
//                assertFalse(savedText.getIsCorrect());
//                assertEquals(0, BigDecimal.ZERO.compareTo(savedText.getPointsEarned()));
//
//                return true;
//            }));
//        }
//    }

        @Nested
        @DisplayName("Get User Response Tests")
        class GetUserResponseTests {

            @Test
            @DisplayName("Should return UserResponse when ID exists")
            void getUserResponseById_Success() {
                // Arrange
                when(userResponseRepository.findById(1L)).thenReturn(Optional.of(userResponse));
                when(userResponseConverter.convertToOutDTO(userResponse)).thenReturn(userResponseOutDTO);

                // Act
                UserResponseOutDTO result = userResponseService.getUserResponseById(1L);

                // Assert
                assertNotNull(result);
                assertEquals(1L, result.getResponseId());
            }

            @Test
            @DisplayName("Should throw ResourceNotFoundException when ID does not exist")
            void getUserResponseById_ThrowsResourceNotFoundException() {
                // Arrange
                when(userResponseRepository.findById(anyLong())).thenReturn(Optional.empty());

                // Act & Assert
                assertThrows(ResourceNotFoundException.class, () -> userResponseService.getUserResponseById(99L));
            }
        }

        @Nested
        @DisplayName("Update User Response Tests")
        class UpdateUserResponseTests {

            @Test
            @DisplayName("Should update user response successfully")
            void updateUserResponse_Success() {
                // Arrange
                UserResponseUpdateInDTO updateDTO = new UserResponseUpdateInDTO();
                updateDTO.setUserAnswer("new answer");
                updateDTO.setIsCorrect(false);
                updateDTO.setPointsEarned(BigDecimal.ZERO);

                when(userResponseRepository.findById(1L)).thenReturn(Optional.of(userResponse));
                when(userResponseConverter.updateEntityFromDTO(any(UserResponse.class), any(UserResponseUpdateInDTO.class))).thenReturn(userResponse);
                when(userResponseRepository.save(any(UserResponse.class))).thenReturn(userResponse);
                when(userResponseConverter.convertToOutDTO(any(UserResponse.class))).thenReturn(userResponseOutDTO);

                // Act
                UserResponseOutDTO result = userResponseService.updateUserResponse(1L, updateDTO);

                // Assert
                assertNotNull(result);
                verify(userResponseRepository, times(1)).save(userResponse);
            }

            @Test
            @DisplayName("Should throw ResourceNotFoundException for non-existent ID")
            void updateUserResponse_ThrowsResourceNotFoundException() {
                // Arrange
                UserResponseUpdateInDTO updateDTO = new UserResponseUpdateInDTO();
                when(userResponseRepository.findById(99L)).thenReturn(Optional.empty());

                // Act & Assert
                assertThrows(ResourceNotFoundException.class, () -> userResponseService.updateUserResponse(99L, updateDTO));
            }
        }

        @Nested
        @DisplayName("Delete User Response Tests")
        class DeleteUserResponseTests {

            @Test
            @DisplayName("Should delete user response successfully")
            void deleteUserResponse_Success() {
                // Arrange
                when(userResponseRepository.existsById(1L)).thenReturn(true);
                doNothing().when(userResponseRepository).deleteById(1L);

                // Act
                userResponseService.deleteUserResponse(1L);

                // Assert
                verify(userResponseRepository, times(1)).deleteById(1L);
            }

            @Test
            @DisplayName("Should throw ResourceNotFoundException for non-existent ID")
            void deleteUserResponse_ThrowsResourceNotFoundException() {
                // Arrange
                when(userResponseRepository.existsById(99L)).thenReturn(false);

                // Act & Assert
                assertThrows(ResourceNotFoundException.class, () -> userResponseService.deleteUserResponse(99L));
            }
        }

        @Nested
        @DisplayName("Get All/By Criteria Tests")
        class GetByCriteriaTests {

            @Test
            @DisplayName("Should get all user responses with pagination")
            void getAllUserResponses_Success() {
                // Arrange
                Pageable pageable = PageRequest.of(0, 10);
                Page<UserResponse> page = new PageImpl<>(List.of(userResponse));
                when(userResponseRepository.findAll(pageable)).thenReturn(page);
                when(userResponseConverter.convertToOutDTO(userResponse)).thenReturn(userResponseOutDTO);

                // Act
                Page<UserResponseOutDTO> result = userResponseService.getAllUserResponses(pageable);

                // Assert
                assertNotNull(result);
                assertEquals(1, result.getTotalElements());
                assertEquals(userResponseOutDTO, result.getContent().get(0));
            }

            @Test
            @DisplayName("Should get user responses by user ID")
            void getUserResponsesByUserId_Success() {
                // Arrange
                when(userResponseRepository.findByUserId(1L)).thenReturn(List.of(userResponse));
                when(userResponseConverter.convertToOutDTOList(anyList())).thenReturn(List.of(userResponseOutDTO));

                // Act
                List<UserResponseOutDTO> result = userResponseService.getUserResponsesByUserId(1L);

                // Assert
                assertNotNull(result);
                assertFalse(result.isEmpty());
            }

            @Test
            @DisplayName("Should get user responses by quiz ID")
            void getUserResponsesByQuizId_Success() {
                // Arrange
                when(userResponseRepository.findByQuizId(1L)).thenReturn(List.of(userResponse));
                when(userResponseConverter.convertToOutDTOList(anyList())).thenReturn(List.of(userResponseOutDTO));

                // Act
                List<UserResponseOutDTO> result = userResponseService.getUserResponsesByQuizId(1L);

                // Assert
                assertNotNull(result);
                assertFalse(result.isEmpty());
            }

            @Test
            @DisplayName("Should get user responses by user ID and quiz ID")
            void getUserResponsesByUserIdAndQuizId_Success() {
                // Arrange
                when(userResponseRepository.findByUserIdAndQuizId(1L, 1L)).thenReturn(List.of(userResponse));
                when(userResponseConverter.convertToOutDTOList(anyList())).thenReturn(List.of(userResponseOutDTO));

                // Act
                List<UserResponseOutDTO> result = userResponseService.getUserResponsesByUserIdAndQuizId(1L, 1L);

                // Assert
                assertNotNull(result);
                assertFalse(result.isEmpty());
            }

            @Test
            @DisplayName("Should get user responses by user, quiz, and attempt")
            void getUserResponsesByUserIdAndQuizIdAndAttempt_Success() {
                // Arrange
                when(userResponseRepository.findByUserIdAndQuizIdAndAttempt(1L, 1L, 1L)).thenReturn(List.of(userResponse));
                when(userResponseConverter.convertToOutDTOList(anyList())).thenReturn(List.of(userResponseOutDTO));

                // Act
                List<UserResponseOutDTO> result = userResponseService.getUserResponsesByUserIdAndQuizIdAndAttempt(1L, 1L, 1L);

                // Assert
                assertNotNull(result);
                assertFalse(result.isEmpty());
            }
        }

        @Nested
        @DisplayName("Calculation and Aggregation Tests")
        class CalculationTests {

            @Test
            @DisplayName("Should get total score for a quiz attempt")
            void getTotalScore_Success() {
                // Arrange
                BigDecimal expectedScore = new BigDecimal("25.50");
                when(userResponseRepository.getTotalScoreByUserIdAndQuizIdAndAttempt(1L, 1L, 1L)).thenReturn(expectedScore);

                // Act
                BigDecimal result = userResponseService.getTotalScore(1L, 1L, 1L);

                // Assert
                assertNotNull(result);
                assertEquals(0, expectedScore.compareTo(result));
            }

            @Test
            @DisplayName("Should return zero score when no responses found")
            void getTotalScore_ReturnsZeroForNull() {
                // Arrange
                when(userResponseRepository.getTotalScoreByUserIdAndQuizIdAndAttempt(1L, 1L, 1L)).thenReturn(null);

                // Act
                BigDecimal result = userResponseService.getTotalScore(1L, 1L, 1L);

                // Assert
                assertNotNull(result);
                assertEquals(0, BigDecimal.ZERO.compareTo(result));
            }

            @Test
            @DisplayName("Should count correct answers for a quiz attempt")
            void countCorrectAnswers_Success() {
                // Arrange
                when(userResponseRepository.countCorrectAnswersByUserIdAndQuizIdAndAttempt(1L, 1L, 1L)).thenReturn(5L);

                // Act
                Long result = userResponseService.countCorrectAnswers(1L, 1L, 1L);

                // Assert
                assertNotNull(result);
                assertEquals(5L, result);
            }

            @Test
            @DisplayName("Should get max attempt number for a user and quiz")
            void getMaxAttemptNumber_Success() {
                // Arrange
                when(userResponseRepository.getMaxAttemptByUserIdAndQuizId(1L, 1L)).thenReturn(3L);

                // Act
                Long result = userResponseService.getMaxAttemptNumber(1L, 1L);

                // Assert
                assertNotNull(result);
                assertEquals(3L, result);
            }

            @Test
            @DisplayName("Should return zero for max attempt if none found")
            void getMaxAttemptNumber_ReturnsZeroForNull() {
                // Arrange
                when(userResponseRepository.getMaxAttemptByUserIdAndQuizId(1L, 1L)).thenReturn(null);

                // Act
                Long result = userResponseService.getMaxAttemptNumber(1L, 1L);

                // Assert
                assertNotNull(result);
                assertEquals(0L, result);
            }
        }
    }
}
