package com.nt.LMS.dto;

import com.nt.LMS.dto.inDTO.GroupInDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GroupInDtoTests {

    private GroupInDTO groupInDTO;

    @BeforeEach
    void setUp() {
        groupInDTO = new GroupInDTO();
        groupInDTO.setGroupName("JavaGroup");
        groupInDTO.setGroupId(101);
        groupInDTO.setUserId(1001);
    }

    @Test
    void testGetters() {
        assertEquals("JavaGroup", groupInDTO.getGroupName());
        assertEquals(101L, groupInDTO.getGroupId());
        assertEquals(1001L, groupInDTO.getUserId());
    }

    @Test
    void testSetters() {
        groupInDTO.setGroupName("SpringGroup");
        groupInDTO.setGroupId(102);
        groupInDTO.setUserId(1002);

        assertEquals("SpringGroup", groupInDTO.getGroupName());
        assertEquals(102L, groupInDTO.getGroupId());
        assertEquals(1002L, groupInDTO.getUserId());
    }

    @Test
    void testToString() {
        String expected = "GroupInDTO(groupName=JavaGroup, groupId=101, userId=1001)";
        assertEquals(expected, groupInDTO.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        GroupInDTO dto1 = new GroupInDTO("JavaGroup", 101L, 1001L);
        GroupInDTO dto2 = new GroupInDTO("JavaGroup", 101L, 1001L);
        GroupInDTO dto3 = new GroupInDTO("DifferentGroup", 999L, 999L);

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }

    @Test
    void testNoArgsConstructor() {
        GroupInDTO dto = new GroupInDTO();
        assertNotNull(dto);
    }

    @Test
    void testAllArgsConstructor() {
        GroupInDTO dto = new GroupInDTO("TestGroup", 201L, 3001L);

        assertEquals("TestGroup", dto.getGroupName());
        assertEquals(201L, dto.getGroupId());
        assertEquals(3001L, dto.getUserId());
    }
}
