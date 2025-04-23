package com.example.course_service_lms.dto;

import com.example.course_service_lms.entity.CourseLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing summary information of a course.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseSummaryDTO {
    private String title;
    private String description;
    private CourseLevel level;
}
