package com.nt.LMS.dto.outDTO;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CourseDeadlinesDTO {

    private String title;

    private Long ownerId;

    private Long courseId;

   private  LocalDateTime deadline;
}
