package com.nt.LMS.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing group summary information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupSummaryDTO {
    private Long groupId;
    private String groupName;
    private String creatorName;
    private Long memberCount;
}