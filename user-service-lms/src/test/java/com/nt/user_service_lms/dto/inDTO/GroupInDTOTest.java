package com.nt.user_service_lms.dto.inDTO;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GroupInDTOTest {

    private GroupInDTO dto;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        dto = new GroupInDTO();
        dto.setGroupName("Team Alpha");
        dto.setGroupId(101L);
        dto.setUserId(1L);
        dto.setEmployees(Arrays.asList(11L, 12L));
        dto.setCourses(Arrays.asList(1001L, 1002L));
        dto.setDeadline(now.plusDays(5));
        dto.setAssignedAt(now);
    }

    @AfterEach
    void tearDown() {
        dto = null;
    }

    // Setters
    @Test
    void setGroupName() {
        dto.setGroupName("Beta");
        assertEquals("Beta", dto.getGroupName());
    }

    @Test
    void setGroupId() {
        dto.setGroupId(202L);
        assertEquals(202L, dto.getGroupId());
    }

    @Test
    void setUserId() {
        dto.setUserId(5L);
        assertEquals(5L, dto.getUserId());
    }

    @Test
    void setEmployees() {
        List<Long> emps = Arrays.asList(99L, 88L);
        dto.setEmployees(emps);
        assertEquals(emps, dto.getEmployees());
    }

    @Test
    void setCourses() {
        List<Long> courses = Arrays.asList(501L, 502L);
        dto.setCourses(courses);
        assertEquals(courses, dto.getCourses());
    }

    @Test
    void setDeadline() {
        LocalDateTime newDeadline = now.plusDays(10);
        dto.setDeadline(newDeadline);
        assertEquals(newDeadline, dto.getDeadline());
    }

    @Test
    void setAssignedAt() {
        LocalDateTime assigned = now.minusDays(1);
        dto.setAssignedAt(assigned);
        assertEquals(assigned, dto.getAssignedAt());
    }

    // Getters
    @Test
    void getGroupName() {
        assertEquals("Team Alpha", dto.getGroupName());
    }

    @Test
    void getGroupId() {
        assertEquals(101L, dto.getGroupId());
    }

    @Test
    void getUserId() {
        assertEquals(1L, dto.getUserId());
    }

    @Test
    void getEmployees() {
        assertEquals(Arrays.asList(11L, 12L), dto.getEmployees());
    }

    @Test
    void getCourses() {
        assertEquals(Arrays.asList(1001L, 1002L), dto.getCourses());
    }

    @Test
    void getDeadline() {
        assertEquals(now.plusDays(5), dto.getDeadline());
    }

    @Test
    void getAssignedAt() {
        assertEquals(now, dto.getAssignedAt());
    }

    // Equals
    @Test
    void testEquals_sameObject() {
        assertEquals(dto, dto);
    }

    @Test
    void testEquals_equalFields() {
        GroupInDTO other = new GroupInDTO(
                "Team Alpha",
                101L,
                1L,
                Arrays.asList(11L, 12L),
                Arrays.asList(1001L, 1002L),
                now.plusDays(5),
                now
        );
        assertEquals(dto, other);
    }

    @Test
    void testEquals_differentFields() {
        GroupInDTO other = new GroupInDTO();
        assertNotEquals(dto, other);
    }

    @Test
    void testEquals_null() {
        assertNotEquals(dto, null);
    }

    @Test
    void testEquals_differentClass() {
        assertNotEquals(dto, "Not a DTO");
    }

    // HashCode
    @Test
    void testHashCode_equalObjects() {
        GroupInDTO other = GroupInDTO.builder()
                .groupName("Team Alpha")
                .groupId(101L)
                .userId(1L)
                .employees(Arrays.asList(11L, 12L))
                .courses(Arrays.asList(1001L, 1002L))
                .deadline(now.plusDays(5))
                .assignedAt(now)
                .build();

        assertEquals(dto.hashCode(), other.hashCode());
    }

    @Test
    void testHashCode_notEqual() {
        GroupInDTO other = GroupInDTO.builder().groupName("Other").build();
        assertNotEquals(dto.hashCode(), other.hashCode());
    }

    // ToString
    @Test
    void testToString_notNull() {
        assertNotNull(dto.toString());
    }

    @Test
    void testToString_containsFields() {
        String str = dto.toString();
        assertTrue(str.contains("Team Alpha"));
        assertTrue(str.contains("101"));
        assertTrue(str.contains("1001"));
    }

    // Builder
    @Test
    void builder() {
        GroupInDTO built = GroupInDTO.builder()
                .groupName("Builder Group")
                .groupId(55L)
                .userId(3L)
                .employees(Collections.singletonList(9L))
                .courses(Collections.singletonList(300L))
                .deadline(now)
                .assignedAt(now)
                .build();

        assertEquals("Builder Group", built.getGroupName());
        assertEquals(55L, built.getGroupId());
        assertEquals(3L, built.getUserId());
        assertEquals(Arrays.asList(9L), built.getEmployees());
        assertEquals(Arrays.asList(300L), built.getCourses());
        assertEquals(now, built.getDeadline());
        assertEquals(now, built.getAssignedAt());
    }

    // Additional edge cases
    @Test
    void testSetNullFields() {
        dto.setGroupName(null);
        dto.setEmployees(null);
        dto.setCourses(null);
        dto.setDeadline(null);
        dto.setAssignedAt(null);

        assertNull(dto.getGroupName());
        assertNull(dto.getEmployees());
        assertNull(dto.getCourses());
        assertNull(dto.getDeadline());
        assertNull(dto.getAssignedAt());
    }

    @Test
    void testEmptyEmployeesAndCourses() {
        dto.setEmployees(Collections.emptyList());
        dto.setCourses(Collections.emptyList());

        assertTrue(dto.getEmployees().isEmpty());
        assertTrue(dto.getCourses().isEmpty());
    }
}
