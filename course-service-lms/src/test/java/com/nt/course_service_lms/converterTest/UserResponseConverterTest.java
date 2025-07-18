package com.nt.course_service_lms.converterTest;

import com.nt.course_service_lms.converters.UserResponseConverter;
import com.nt.course_service_lms.dto.inDTO.UserResponseInDTO;
import com.nt.course_service_lms.dto.inDTO.UserResponseUpdateInDTO;
import com.nt.course_service_lms.dto.outDTO.UserResponseOutDTO;
import com.nt.course_service_lms.entity.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserResponseConverterTest {

    private UserResponseConverter converter;

    @BeforeEach
    void setUp() {
        converter = new UserResponseConverter();
    }

    @Test
    void testConvertToEntity_NullInput() {
        assertNull(converter.convertToEntity(null));
    }

    @Test
    void testConvertToEntity_ValidInput() {
        UserResponseInDTO dto = new UserResponseInDTO();
        dto.setUserId(1L);
        dto.setQuizId(2L);
        dto.setQuestionId(3L);
        dto.setAttempt(1L);
        dto.setUserAnswer("Answer");
        dto.setAnsweredAt(LocalDateTime.of(2023, 1, 1, 10, 0));

        UserResponse entity = converter.convertToEntity(dto);

        assertEquals(1, entity.getUserId());
        assertEquals(2L, entity.getQuizId());
        assertEquals(3L, entity.getQuestionId());
        assertEquals(1, entity.getAttempt());
        assertEquals("Answer", entity.getUserAnswer());
        assertEquals(LocalDateTime.of(2023, 1, 1, 10, 0), entity.getAnsweredAt());
    }

    @Test
    void testConvertToEntity_NullAnsweredAt_DefaultsToNow() {
        UserResponseInDTO dto = new UserResponseInDTO();
        dto.setUserId(1L);
        dto.setQuizId(2L);
        dto.setQuestionId(3L);
        dto.setAttempt(1L);
        dto.setUserAnswer("Answer");
        dto.setAnsweredAt(null);

        UserResponse entity = converter.convertToEntity(dto);

        assertNotNull(entity.getAnsweredAt());
    }

    @Test
    void testConvertToOutDTO_NullInput() {
        assertNull(converter.convertToOutDTO(null));
    }

    @Test
    void testConvertToOutDTO_ValidInput() {
        UserResponse entity = new UserResponse();
        entity.setResponseId(10L);
        entity.setUserId(1L);
        entity.setQuizId(2L);
        entity.setQuestionId(3L);
        entity.setAttempt(1L);
        entity.setUserAnswer("Correct Answer");
        entity.setIsCorrect(true);
        entity.setPointsEarned(BigDecimal.valueOf(5));
        entity.setAnsweredAt(LocalDateTime.of(2023, 2, 2, 12, 0));

        UserResponseOutDTO dto = converter.convertToOutDTO(entity);

        assertEquals(10L, dto.getResponseId());
        assertEquals(1, dto.getUserId());
        assertEquals(2L, dto.getQuizId());
        assertEquals(3L, dto.getQuestionId());
        assertEquals(1, dto.getAttempt());
        assertEquals("Correct Answer", dto.getUserAnswer());
        assertTrue(dto.getIsCorrect());
        assertEquals(BigDecimal.valueOf(5), dto.getPointsEarned());
        assertEquals(LocalDateTime.of(2023, 2, 2, 12, 0), dto.getAnsweredAt());
    }

    @Test
    void testUpdateEntityFromDTO_NullEntity_ReturnsNull() {
        UserResponseUpdateInDTO dto = new UserResponseUpdateInDTO();
        dto.setUserAnswer("A");
        assertNull(converter.updateEntityFromDTO(null, dto));
    }

    @Test
    void testUpdateEntityFromDTO_NullDTO_ReturnsSameEntity() {
        UserResponse entity = new UserResponse();
        entity.setUserAnswer("Old");

        UserResponse updated = converter.updateEntityFromDTO(entity, null);

        assertEquals("Old", updated.getUserAnswer());
    }

    @Test
    void testUpdateEntityFromDTO_ValidUpdate() {
        UserResponse entity = new UserResponse();
        entity.setUserAnswer("Old");
        entity.setIsCorrect(false);
        entity.setPointsEarned(BigDecimal.ZERO);
        entity.setAnsweredAt(LocalDateTime.of(2022, 1, 1, 0, 0));

        UserResponseUpdateInDTO dto = new UserResponseUpdateInDTO();
        dto.setUserAnswer("New");
        dto.setIsCorrect(true);
        dto.setPointsEarned(BigDecimal.valueOf(10));
        dto.setAnsweredAt(LocalDateTime.of(2024, 1, 1, 0, 0));

        UserResponse updated = converter.updateEntityFromDTO(entity, dto);

        assertEquals("New", updated.getUserAnswer());
        assertTrue(updated.getIsCorrect());
        assertEquals(BigDecimal.valueOf(10), updated.getPointsEarned());
        assertEquals(LocalDateTime.of(2024, 1, 1, 0, 0), updated.getAnsweredAt());
    }

    @Test
    void testUpdateEntityFromDTO_NullAnsweredAt_DoesNotChangeOriginal() {
        UserResponse entity = new UserResponse();
        entity.setAnsweredAt(LocalDateTime.of(2021, 1, 1, 0, 0));

        UserResponseUpdateInDTO dto = new UserResponseUpdateInDTO();
        dto.setAnsweredAt(null);

        UserResponse updated = converter.updateEntityFromDTO(entity, dto);
        assertEquals(LocalDateTime.of(2021, 1, 1, 0, 0), updated.getAnsweredAt());
    }

    @Test
    void testConvertToOutDTOList_NullList() {
        assertNull(converter.convertToOutDTOList(null));
    }

    @Test
    void testConvertToOutDTOList_EmptyList() {
        List<UserResponseOutDTO> result = converter.convertToOutDTOList(Collections.emptyList());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testConvertToOutDTOList_ValidList() {
        UserResponse ur1 = new UserResponse();
        ur1.setResponseId(1L);
        ur1.setUserId(1L);
        ur1.setQuizId(1L);
        ur1.setQuestionId(1L);
        ur1.setAttempt(1L);
        ur1.setUserAnswer("Ans");
        ur1.setIsCorrect(true);
        ur1.setPointsEarned(BigDecimal.ONE);
        ur1.setAnsweredAt(LocalDateTime.now());

        UserResponse ur2 = new UserResponse();
        ur2.setResponseId(2L);
        ur2.setUserId(2L);
        ur2.setQuizId(2L);
        ur2.setQuestionId(2L);
        ur2.setAttempt(1L);
        ur2.setUserAnswer("Ans 2");
        ur2.setIsCorrect(false);
        ur2.setPointsEarned(BigDecimal.ZERO);
        ur2.setAnsweredAt(LocalDateTime.now());

        List<UserResponseOutDTO> result = converter.convertToOutDTOList(Arrays.asList(ur1, ur2));
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getResponseId());
        assertEquals(2L, result.get(1).getResponseId());
    }
}

