package com.nt.LMS.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserBundleEnrollmentOutDTO {
    private Long userId;
    private Long BundleId;
    private Long assignedById;
    private String userName;
    private String BundleName;
    private String assignedByName;
    private LocalDateTime deadline;
}
