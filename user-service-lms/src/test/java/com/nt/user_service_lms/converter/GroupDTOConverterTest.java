package com.nt.user_service_lms.converter;

import com.nt.user_service_lms.dto.outDTO.GroupOutDTO;
import com.nt.user_service_lms.entities.Group;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GroupDTOConverterTest {

    private GroupDTOConverter converter;

    @BeforeEach
    void setUp() {
        converter = new GroupDTOConverter();
    }

    @AfterEach
    void tearDown() {
        converter = null;
    }

    /**
     * Happy path: full group with non-null creator.
     */
    @Test
    void testGroupToOutDto_withCreator() {
        Group group = Group.builder()
                .groupId(1L)
                .groupName("Test Group")
                .build();

        String creator = "Alice";
        GroupOutDTO result = converter.groupToOutDto(group, creator);

        assertNotNull(result);
        assertEquals(1L, result.getGroupId());
        assertEquals("Test Group", result.getGroupName());
        assertEquals("Alice", result.getCreatorName());
    }

    /**
     * Creator name is null — field should remain unset in output DTO.
     */
    @Test
    void testGroupToOutDto_withNullCreator() {
        Group group = Group.builder()
                .groupId(2L)
                .groupName("Another Group")
                .build();

        GroupOutDTO result = converter.groupToOutDto(group, null);

        assertNotNull(result);
        assertEquals(2L, result.getGroupId());
        assertEquals("Another Group", result.getGroupName());
        assertNull(result.getCreatorName());
    }

    /**
     * Group entity has empty groupName.
     */
    @Test
    void testGroupToOutDto_withEmptyGroupName() {
        Group group = Group.builder()
                .groupId(3L)
                .groupName("")
                .build();

        GroupOutDTO result = converter.groupToOutDto(group, "Bob");

        assertNotNull(result);
        assertEquals(3L, result.getGroupId());
        assertEquals("", result.getGroupName());
        assertEquals("Bob", result.getCreatorName());
    }

    /**
     * Group entity has all null fields.
     */
    @Test
    void testGroupToOutDto_withNullFields() {
        Group group = new Group(); // all fields null

        GroupOutDTO result = converter.groupToOutDto(group, "Charlie");

        assertNotNull(result);
        assertNull(result.getGroupId());
        assertNull(result.getGroupName());
        assertEquals("Charlie", result.getCreatorName());
    }

    /**
     * Group is null — should throw NullPointerException.
     */
    @Test
    void testGroupToOutDto_withNullGroup_shouldThrowNPE() {
        assertThrows(NullPointerException.class, () -> {
            converter.groupToOutDto(null, "David");
        });
    }
}

