package com.nt.user_service_lms.dto.inDTO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EnrollmentRequestInDTOTest {

    private EnrollmentRequestInDTO dto;
    private LocalDateTime deadline;

    @BeforeEach
    void setUp() {
        deadline = LocalDateTime.now().plusDays(10);
        dto = new EnrollmentRequestInDTO();
        dto.setAssignedBy(1L);
        dto.setUserIds(Arrays.asList(101L, 102L));
        dto.setGroupIds(null);
        dto.setCourseIds(Arrays.asList(201L));
        dto.setBundleIds(null);
        dto.setDeadline(deadline);
        dto.setStatus("ACTIVE");
    }

    @Test
    void testGettersAndSetters() {
        assertEquals(1L, dto.getAssignedBy());
        assertEquals(Arrays.asList(101L, 102L), dto.getUserIds());
        assertNull(dto.getGroupIds());
        assertEquals(Arrays.asList(201L), dto.getCourseIds());
        assertNull(dto.getBundleIds());
        assertEquals(deadline, dto.getDeadline());
        assertEquals("ACTIVE", dto.getStatus());
    }

    @Test
    void testHasUsers_true() {
        assertTrue(dto.hasUsers());
    }

    @Test
    void testHasUsers_false() {
        dto.setUserIds(null);
        assertFalse(dto.hasUsers());

        dto.setUserIds(Collections.emptyList());
        assertFalse(dto.hasUsers());
    }

    @Test
    void testHasGroups_true() {
        dto.setGroupIds(Arrays.asList(10L));
        assertTrue(dto.hasGroups());
    }

    @Test
    void testHasGroups_false() {
        dto.setGroupIds(null);
        assertFalse(dto.hasGroups());

        dto.setGroupIds(Collections.emptyList());
        assertFalse(dto.hasGroups());
    }

    @Test
    void testHasCourses_true() {
        assertTrue(dto.hasCourses());
    }

    @Test
    void testHasCourses_false() {
        dto.setCourseIds(null);
        assertFalse(dto.hasCourses());

        dto.setCourseIds(Collections.emptyList());
        assertFalse(dto.hasCourses());
    }

    @Test
    void testHasBundles_true() {
        dto.setBundleIds(Arrays.asList(30L));
        assertTrue(dto.hasBundles());
    }

    @Test
    void testHasBundles_false() {
        dto.setBundleIds(null);
        assertFalse(dto.hasBundles());

        dto.setBundleIds(Collections.emptyList());
        assertFalse(dto.hasBundles());
    }

    // ---------- isValid Scenarios ----------
    @Test
    void testIsValid_validUserAndCourse() {
        assertTrue(dto.isValid());
    }

    @Test
    void testIsValid_validGroupAndBundle() {
        dto.setUserIds(null);
        dto.setGroupIds(Arrays.asList(10L));
        dto.setCourseIds(null);
        dto.setBundleIds(Arrays.asList(40L));

        assertTrue(dto.isValid());
    }

    @Test
    void testIsValid_invalid_noTargets() {
        dto.setUserIds(null);
        dto.setGroupIds(null);
        assertFalse(dto.isValid());
    }

    @Test
    void testIsValid_invalid_noContent() {
        dto.setCourseIds(null);
        dto.setBundleIds(null);
        assertFalse(dto.isValid());
    }

    @Test
    void testIsValid_invalid_bothUsersAndGroups() {
        dto.setGroupIds(Arrays.asList(11L));
        assertFalse(dto.isValid());
    }

    @Test
    void testIsValid_invalid_bothCoursesAndBundles() {
        dto.setBundleIds(Arrays.asList(20L));
        assertFalse(dto.isValid());
    }

    // ---------- equals, hashCode, toString ----------
    @Test
    void testEqualsAndHashCode_sameData() {
        EnrollmentRequestInDTO dto2 = new EnrollmentRequestInDTO(
                1L,
                Arrays.asList(101L, 102L),
                null,
                Arrays.asList(201L),
                null,
                deadline,
                "ACTIVE"
        );
        assertEquals(dto, dto2);
        assertEquals(dto.hashCode(), dto2.hashCode());
    }

    @Test
    void testEquals_false() {
        EnrollmentRequestInDTO dto2 = new EnrollmentRequestInDTO();
        assertNotEquals(dto, dto2);
    }

    @Test
    void testToString_notNull() {
        assertNotNull(dto.toString());
    }

    // ---------- Builder ----------
    @Test
    void testBuilderUsage() {
        EnrollmentRequestInDTO builtDto = EnrollmentRequestInDTO.builder()
                .assignedBy(99L)
                .userIds(Arrays.asList(1L, 2L))
                .groupIds(Arrays.asList(3L))
                .courseIds(Arrays.asList(4L))
                .bundleIds(Arrays.asList(5L))
                .deadline(deadline)
                .status("PENDING")
                .build();

        assertEquals(99L, builtDto.getAssignedBy());
        assertEquals("PENDING", builtDto.getStatus());
        assertEquals(Arrays.asList(1L, 2L), builtDto.getUserIds());
        assertEquals(Arrays.asList(3L), builtDto.getGroupIds());
        assertEquals(Arrays.asList(4L), builtDto.getCourseIds());
        assertEquals(Arrays.asList(5L), builtDto.getBundleIds());
        assertEquals(deadline, builtDto.getDeadline());
    }
}
