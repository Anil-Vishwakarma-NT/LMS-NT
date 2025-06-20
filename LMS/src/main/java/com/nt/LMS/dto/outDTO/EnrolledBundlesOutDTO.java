package com.nt.LMS.dto.outDTO;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EnrolledBundlesOutDTO {
    private Long bundleId;
    private String bundleName;
    private Float progress;
    private LocalDateTime enrollmentDate;
    private LocalDateTime deadline;
    private List<EnrolledCoursesOutDTO> enrolledCoursesList;
}
