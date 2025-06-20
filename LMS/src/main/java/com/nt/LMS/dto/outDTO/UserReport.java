package com.nt.LMS.dto.outDTO;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserReport {

        private Long userId;
        private String name;
        private String username;
        private String email;
        private String role;
        private LocalDateTime createdAt;
        private List<UserCourseReport> enrolledCourses;
}
