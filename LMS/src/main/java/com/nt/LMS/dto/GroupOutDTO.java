package com.nt.LMS.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupOutDTO {
    private String groupName;
    private long groupId;
    private String creatorName;

}
