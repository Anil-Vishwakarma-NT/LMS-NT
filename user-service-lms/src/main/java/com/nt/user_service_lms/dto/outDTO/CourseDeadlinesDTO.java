package com.nt.user_service_lms.dto.outDTO;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseDeadlinesDTO {

    private String title;

    private Long ownerId;

    private Long courseId;

   private  LocalDateTime deadline;
}
