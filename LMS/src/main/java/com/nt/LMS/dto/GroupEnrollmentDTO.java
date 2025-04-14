package com.nt.LMS.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GroupEnrollmentDTO {
    private long groupId;
    private long courseId;
    private long bundleId;
    private LocalDateTime assignedAt;
    private long assignedBy;
}
