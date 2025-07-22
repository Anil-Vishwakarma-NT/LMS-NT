package com.nt.user_service_lms.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a summary of a group including its ID, name, creator, and member count.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupSummaryOutDTO {
    /**
     * The unique identifier of the group.
     */
    private Long groupId;

    /**
     * The name of the group.
     */
    private String groupName;

    /**
     * The name of the creator of the group.
     */
    private String creatorName;

    /**
     * The number of members in the group.
     */
    private Long memberCount;
}
