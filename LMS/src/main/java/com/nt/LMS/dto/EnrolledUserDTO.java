package com.nt.LMS.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EnrolledUserDTO {
    private Long userId;
    private String assignedByName;
    private String userName;
    private Float progress;
    private LocalDateTime enrollmentDate;
    private LocalDateTime deadline;
}
