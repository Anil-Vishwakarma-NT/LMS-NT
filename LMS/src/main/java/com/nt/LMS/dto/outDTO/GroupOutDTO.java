package com.nt.LMS.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for sending group details as output.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupOutDTO {

    /**
     * The name of the group.
     */
    private String groupName;

    /**
     * The unique ID of the group.
     */
    private long groupId;

    /**
     * The name of the user who created the group.
     */
    private String creatorName;

}
