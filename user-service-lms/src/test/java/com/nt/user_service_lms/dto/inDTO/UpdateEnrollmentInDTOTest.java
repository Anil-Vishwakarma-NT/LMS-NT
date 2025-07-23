package com.nt.user_service_lms.dto.inDTO;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UpdateEnrollmentInDTOTest {

    private UpdateEnrollmentInDTO dto;

    @BeforeEach
    void setUp() {
        dto = new UpdateEnrollmentInDTO();
        dto.setUserId(1L);
        dto.setGroupId(2L);
        dto.setCourseId(3L);
        dto.setBundleId(4L);
        dto.setManagerId(5L);
        dto.setStatus("ACTIVE");
        dto.setDeadline(LocalDateTime.of(2025, 7, 22, 12, 0));
    }

    @AfterEach
    void tearDown() {
        dto = null;
    }

    @Test
    void getUserId() {
        assertEquals(1L, dto.getUserId());
    }

    @Test
    void getGroupId() {
        assertEquals(2L, dto.getGroupId());
    }

    @Test
    void getCourseId() {
        assertEquals(3L, dto.getCourseId());
    }

    @Test
    void getBundleId() {
        assertEquals(4L, dto.getBundleId());
    }

    @Test
    void getManagerId() {
        assertEquals(5L, dto.getManagerId());
    }

    @Test
    void getStatus() {
        assertEquals("ACTIVE", dto.getStatus());
    }

    @Test
    void getDeadline() {
        assertEquals(LocalDateTime.of(2025, 7, 22, 12, 0), dto.getDeadline());
    }

    @Test
    void setUserId() {
        dto.setUserId(10L);
        assertEquals(10L, dto.getUserId());
    }

    @Test
    void setGroupId() {
        dto.setGroupId(20L);
        assertEquals(20L, dto.getGroupId());
    }

    @Test
    void setCourseId() {
        dto.setCourseId(30L);
        assertEquals(30L, dto.getCourseId());
    }

    @Test
    void setBundleId() {
        dto.setBundleId(40L);
        assertEquals(40L, dto.getBundleId());
    }

    @Test
    void setManagerId() {
        dto.setManagerId(50L);
        assertEquals(50L, dto.getManagerId());
    }

    @Test
    void setStatus() {
        dto.setStatus("INACTIVE");
        assertEquals("INACTIVE", dto.getStatus());
    }

    @Test
    void setDeadline() {
        LocalDateTime newDeadline = LocalDateTime.of(2025, 12, 31, 23, 59);
        dto.setDeadline(newDeadline);
        assertEquals(newDeadline, dto.getDeadline());
    }

    @Test
    void testToString() {
        assertNotNull(dto.toString());
        assertTrue(dto.toString().contains("ACTIVE"));
    }

    @Test
    void builder() {
        LocalDateTime deadline = LocalDateTime.now();
        UpdateEnrollmentInDTO built = UpdateEnrollmentInDTO.builder()
                .userId(1L)
                .groupId(2L)
                .courseId(3L)
                .bundleId(4L)
                .managerId(5L)
                .status("COMPLETED")
                .deadline(deadline)
                .build();

        assertEquals(1L, built.getUserId());
        assertEquals(2L, built.getGroupId());
        assertEquals(3L, built.getCourseId());
        assertEquals(4L, built.getBundleId());
        assertEquals(5L, built.getManagerId());
        assertEquals("COMPLETED", built.getStatus());
        assertEquals(deadline, built.getDeadline());
    }

    @Test
    void testEquals_sameObject() {
        assertEquals(dto, dto);
    }

    @Test
    void testEquals_equalFields() {
        UpdateEnrollmentInDTO other = UpdateEnrollmentInDTO.builder()
                .userId(1L)
                .groupId(2L)
                .courseId(3L)
                .bundleId(4L)
                .managerId(5L)
                .status("ACTIVE")
                .deadline(LocalDateTime.of(2025, 7, 22, 12, 0))
                .build();
        assertEquals(dto, other);
    }

    @Test
    void testEquals_differentUserId() {
        UpdateEnrollmentInDTO other = UpdateEnrollmentInDTO.builder()
                .userId(99L)
                .groupId(2L)
                .courseId(3L)
                .bundleId(4L)
                .managerId(5L)
                .status("ACTIVE")
                .deadline(LocalDateTime.of(2025, 7, 22, 12, 0))
                .build();
        assertNotEquals(dto, other);
    }

    @Test
    void testEquals_nullObject() {
        assertNotEquals(dto, null);
    }

    @Test
    void testEquals_differentClass() {
        assertNotEquals(dto, "String");
    }

    @Test
    void testEquals_nullFields() {
        UpdateEnrollmentInDTO dto1 = new UpdateEnrollmentInDTO(null, null, null, null, 5L, null, null);
        UpdateEnrollmentInDTO dto2 = new UpdateEnrollmentInDTO(null, null, null, null, 5L, null, null);
        assertEquals(dto1, dto2);
    }

    @Test
    void testEquals_oneNullDeadline() {
        UpdateEnrollmentInDTO dto1 = new UpdateEnrollmentInDTO(1L, 2L, 3L, 4L, 5L, "ACTIVE", null);
        UpdateEnrollmentInDTO dto2 = new UpdateEnrollmentInDTO(1L, 2L, 3L, 4L, 5L, "ACTIVE", LocalDateTime.now());
        assertNotEquals(dto1, dto2);
    }

    @Test
    void testHashCode_consistency() {
        int hash1 = dto.hashCode();
        int hash2 = dto.hashCode();
        assertEquals(hash1, hash2);
    }

    @Test
    void testHashCode_equalObjects() {
        UpdateEnrollmentInDTO other = UpdateEnrollmentInDTO.builder()
                .userId(1L)
                .groupId(2L)
                .courseId(3L)
                .bundleId(4L)
                .managerId(5L)
                .status("ACTIVE")
                .deadline(LocalDateTime.of(2025, 7, 22, 12, 0))
                .build();
        assertEquals(dto.hashCode(), other.hashCode());
    }

    @Test
    void testHashCode_differentObjects() {
        UpdateEnrollmentInDTO other = UpdateEnrollmentInDTO.builder()
                .userId(9L)
                .groupId(2L)
                .courseId(3L)
                .bundleId(4L)
                .managerId(5L)
                .status("ACTIVE")
                .deadline(LocalDateTime.of(2025, 7, 22, 12, 0))
                .build();
        assertNotEquals(dto.hashCode(), other.hashCode());
    }

    @Test
    void testHashCode_nullFields() {
        UpdateEnrollmentInDTO dtoWithNulls = new UpdateEnrollmentInDTO(null, null, null, null, null, null, null);
        assertDoesNotThrow(() -> dtoWithNulls.hashCode());
    }
}
