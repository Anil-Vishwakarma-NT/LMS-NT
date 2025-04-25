package com.nt.LMS.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EnrolledBundlesDTO {
    private Long bundleId;
    private String bundleName;
    private Float progress;
    private LocalDateTime enrollmentDate;
    private LocalDateTime deadline;
}
