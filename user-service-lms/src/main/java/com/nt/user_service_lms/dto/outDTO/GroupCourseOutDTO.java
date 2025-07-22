package com.nt.user_service_lms.dtoTest.outDTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for representing a group course with progress and enrolment details.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupCourseOutDTO {
    /**
     * The name of the course.
     */
    private String courseName;

    /**
     * The unique identifier of the course.
     */
    private long courseId;

    /**
     * The progress percentage of the course.
     */
    private double progress;

    /**
     * The number of enrolments in the course.
     */
    private long enrols;
}
