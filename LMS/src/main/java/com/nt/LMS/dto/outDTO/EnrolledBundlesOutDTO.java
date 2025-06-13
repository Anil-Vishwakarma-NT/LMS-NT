package com.nt.LMS.dto.outDTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EnrolledBundlesOutDTO {
    private Long bundleId;
    private String bundleName;
    private Float progress;
    private LocalDateTime enrollmentDate;
    private LocalDateTime deadline;
}
