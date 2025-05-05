package com.nt.LMS.dto;

import lombok.Data;

@Data
public class EnrollmentOutDTO {
    private Long userId;
    private Long courseId;
    private Long bundleId;
    private Long groupId;
    private Long assignedById;
    private String assignedByName;
    private String learnerName;
    private String courseName;
    private String groupName;
    private String bundleName;
}
