package com.nt.LMS.dto.outDTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GroupBundleEnrollmentOutDTO {
    private Long groupId;
    private Long bundleId;
    private Long assignedById;
    private String groupName;
    private String bundleName;
    private String assignedByName;
    private LocalDateTime deadline;
}
