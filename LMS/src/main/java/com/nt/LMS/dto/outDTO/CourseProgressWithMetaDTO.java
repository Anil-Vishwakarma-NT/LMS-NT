package com.nt.LMS.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseProgressWithMetaDTO {
    private double courseCompletionPercentage;
    private LocalDateTime firstCompletedAt;
}
