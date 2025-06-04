package com.nt.LMS.dto;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserEnrollDetails {
    private Long courseId;
    private Long assignedById;
    private LocalDateTime enrollmentDate;
    private LocalDateTime deadline;
}





