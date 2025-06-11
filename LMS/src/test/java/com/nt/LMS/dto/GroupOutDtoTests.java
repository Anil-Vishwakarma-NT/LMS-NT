package com.nt.LMS.dto;

import com.nt.LMS.dto.outDTO.GroupOutDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GroupOutDtoTests {

    private GroupOutDTO groupOutDTO;

    @BeforeEach
    public void setUp() {
        groupOutDTO = new GroupOutDTO();
        groupOutDTO.setGroupName("Test Group");
        groupOutDTO.setGroupId(1L);
        groupOutDTO.setCreatorName("Test");
    }

    @Test
    public void testNoArgsConstructor() {
        GroupOutDTO dto = new GroupOutDTO();
        assertNotNull(dto);
    }

    @Test
    public void testAllArgsConstructor() {
        GroupOutDTO dto = new GroupOutDTO("Test Group2", 100L, "Test2");
        assertEquals("Test Group2", dto.getGroupName());
        assertEquals(100L, dto.getGroupId());
        assertEquals("Test2", dto.getCreatorName());
    }

    @Test
    public void testGettersAndSetters() {
        assertEquals("Test Group", groupOutDTO.getGroupName());
        assertEquals(1L, groupOutDTO.getGroupId());
        assertEquals("Test", groupOutDTO.getCreatorName());

        groupOutDTO.setGroupName("Updated Group");
        groupOutDTO.setGroupId(2L);
        groupOutDTO.setCreatorName("Test Updated");

        assertEquals("Updated Group", groupOutDTO.getGroupName());
        assertEquals(2L, groupOutDTO.getGroupId());
        assertEquals("Test Updated", groupOutDTO.getCreatorName());
    }

    @Test
    public void testToString() {
        String expected = "GroupOutDTO(groupName=Test Group, groupId=1, creatorName=Test)";
        assertEquals(expected, groupOutDTO.toString());
    }

    @Test
    public void testEqualsAndHashCode() {
        GroupOutDTO dto1 = new GroupOutDTO("Test Group", 1L, "Test");
        GroupOutDTO dto2 = new GroupOutDTO("Test Group", 1L, "Test");
        GroupOutDTO dto3 = new GroupOutDTO("Other Group", 2L, "Test2");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());

        assertNotEquals(dto1, dto3);
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }
}
