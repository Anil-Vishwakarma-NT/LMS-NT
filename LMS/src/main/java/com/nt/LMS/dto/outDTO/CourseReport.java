package com.nt.LMS.dto.outDTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class CourseReport {

    private Long courseId;
    private String name;
    private String description;
    private String level;
    private LocalDateTime createdAt;

    private Set<CourseContentReport> contents = new HashSet<>();
    private Set<CourseEnrolledUserReport> enrolledUsers = new HashSet<>();
}


