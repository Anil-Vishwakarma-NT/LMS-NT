package com.nt.user_service_lms.dto.outDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserEnrollDetailsOutDTO {
    private Long courseId;
    private Long assignedById;
    private LocalDateTime enrollmentDate;
    private LocalDateTime deadline;
}





