package com.nt.LMS.dto.outDTO;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EnrolledUserOutDTO {
    private Long userId;
    private String assignedByName;
    private String userName;
    private Double progress;
    private LocalDateTime enrollmentDate;
    private LocalDateTime deadline;
}
