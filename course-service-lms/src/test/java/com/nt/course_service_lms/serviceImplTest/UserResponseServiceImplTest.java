package com.nt.course_service_lms.serviceImplTest;

import com.nt.course_service_lms.converters.UserResponseConverter;
import com.nt.course_service_lms.dto.inDTO.UserResponseInDTO;
import com.nt.course_service_lms.dto.inDTO.UserResponseUpdateInDTO;
import com.nt.course_service_lms.dto.outDTO.UserResponseOutDTO;
import com.nt.course_service_lms.entity.UserResponse;
import com.nt.course_service_lms.repository.UserResponseRepository;
import com.nt.course_service_lms.service.serviceImpl.QuizSubmissionService;
import com.nt.course_service_lms.service.serviceImpl.UserResponseServiceImpl;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserResponseServiceImplTest {

    @Mock
    private UserResponseRepository repository;

    @Mock
    private UserResponseConverter converter;

    @InjectMocks
    private UserResponseServiceImpl service;

    private UserResponse entity;
    private UserResponseInDTO inDTO;
    private UserResponseUpdateInDTO updateDTO;
    private UserResponseOutDTO outDTO;

    @BeforeEach
    void setUp() {
        entity = UserResponse.builder()
                .responseId(1L)
                .userId(10L)
                .quizId(20L)
                .questionId(30L)
                .attempt(1L)
                .userAnswer("A")
                .isCorrect(true)
                .pointsEarned(BigDecimal.valueOf(5))
                .answeredAt(LocalDateTime.now())
                .build();

        inDTO = UserResponseInDTO.builder()
                .userId(10L)
                .quizId(20L)
                .questionId(30L)
                .attempt(1L)
                .userAnswer("A")
                .answeredAt(LocalDateTime.now())
                .build();

        updateDTO = UserResponseUpdateInDTO.builder()
                .userAnswer("B")
                .isCorrect(false)
                .pointsEarned(BigDecimal.valueOf(3))
                .answeredAt(LocalDateTime.now())
                .build();

        outDTO = new UserResponseOutDTO();
    }

    @Test
    void testCreateUserResponse() {
        when(converter.convertToEntity(inDTO)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(converter.convertToOutDTO(entity)).thenReturn(outDTO);

        UserResponseOutDTO result = service.createUserResponse(inDTO);
        assertThat(result).isEqualTo(outDTO);
    }

    @Test
    void testGetUserResponseById_Found() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(converter.convertToOutDTO(entity)).thenReturn(outDTO);

        UserResponseOutDTO result = service.getUserResponseById(1L);
        assertThat(result).isEqualTo(outDTO);
    }

    @Test
    void testGetUserResponseById_NotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getUserResponseById(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("UserResponse not found with id: 1");
    }

    @Test
    void testUpdateUserResponse_Found() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(converter.updateEntityFromDTO(entity, updateDTO)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(converter.convertToOutDTO(entity)).thenReturn(outDTO);

        UserResponseOutDTO result = service.updateUserResponse(1L, updateDTO);
        assertThat(result).isEqualTo(outDTO);
    }

    @Test
    void testUpdateUserResponse_NotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.updateUserResponse(1L, updateDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("UserResponse not found with id: 1");
    }

    @Test
    void testDeleteUserResponse_Found() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        service.deleteUserResponse(1L);
        verify(repository).delete(entity);
    }

    @Test
    void testDeleteUserResponse_NotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.deleteUserResponse(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("UserResponse not found with id: 1");
    }

    @Test
    void testGetAllUserResponses() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserResponse> page = new PageImpl<>(List.of(entity));

        when(repository.findAll(pageable)).thenReturn(page);
        when(converter.convertToOutDTO(entity)).thenReturn(outDTO);

        List<UserResponseOutDTO> result = service.getAllUserResponses(pageable);
        assertThat(result).containsExactly(outDTO);
    }

    @Test
    void testGetUserResponsesByUserId() {
        when(repository.findByUserId(10L)).thenReturn(List.of(entity));
        when(converter.convertToOutDTOList(List.of(entity))).thenReturn(List.of(outDTO));

        List<UserResponseOutDTO> result = service.getUserResponsesByUserId(10L);
        assertThat(result).containsExactly(outDTO);
    }

    @Test
    void testGetUserResponsesByQuizId() {
        when(repository.findByQuizId(20L)).thenReturn(List.of(entity));
        when(converter.convertToOutDTOList(List.of(entity))).thenReturn(List.of(outDTO));

        List<UserResponseOutDTO> result = service.getUserResponsesByQuizId(20L);
        assertThat(result).containsExactly(outDTO);
    }

    @Test
    void testGetUserResponsesByUserIdAndQuizId() {
        when(repository.findByUserIdAndQuizId(10L, 20L)).thenReturn(List.of(entity));
        when(converter.convertToOutDTOList(List.of(entity))).thenReturn(List.of(outDTO));

        List<UserResponseOutDTO> result = service.getUserResponsesByUserIdAndQuizId(10L, 20L);
        assertThat(result).containsExactly(outDTO);
    }

    @Test
    void testGetUserResponsesByUserIdAndQuizIdAndAttempt() {
        when(repository.findByUserIdAndQuizIdAndAttempt(10L, 20L, 1L)).thenReturn(List.of(entity));
        when(converter.convertToOutDTOList(List.of(entity))).thenReturn(List.of(outDTO));

        List<UserResponseOutDTO> result = service.getUserResponsesByUserIdAndQuizIdAndAttempt(10L, 20L, 1L);
        assertThat(result).containsExactly(outDTO);
    }

    @Test
    void testCalculateTotalScore() {
        when(repository.findByUserIdAndQuizIdAndAttempt(10L, 20L, 1L)).thenReturn(List.of(entity));

        BigDecimal result = service.calculateTotalScore(10L, 20L, 1L);
        assertThat(result).isEqualByComparingTo("5.00");
    }

    @Test
    void testCalculateTotalScore_Empty() {
        when(repository.findByUserIdAndQuizIdAndAttempt(10L, 20L, 1L)).thenReturn(Collections.emptyList());

        BigDecimal result = service.calculateTotalScore(10L, 20L, 1L);
        assertThat(result).isEqualByComparingTo("0.00");
    }

    @Test
    void testHasUserAttemptedQuiz_True() {
        when(repository.existsByUserIdAndQuizId(10L, 20L)).thenReturn(true);
        assertThat(service.hasUserAttemptedQuiz(10L, 20L)).isTrue();
    }

    @Test
    void testHasUserAttemptedQuiz_False() {
        when(repository.existsByUserIdAndQuizId(10L, 20L)).thenReturn(false);
        assertThat(service.hasUserAttemptedQuiz(10L, 20L)).isFalse();
    }
}

