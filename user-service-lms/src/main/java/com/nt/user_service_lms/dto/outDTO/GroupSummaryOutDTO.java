package com.nt.user_service_lms.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing group summary information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupSummaryOutDTO {
    private Long groupId;
    private String groupName;
    private String creatorName;
    private Long memberCount;
}