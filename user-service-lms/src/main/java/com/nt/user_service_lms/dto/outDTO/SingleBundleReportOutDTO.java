package com.nt.user_service_lms.dto.outDTO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class SingleBundleReportOutDTO {
    private Long bundleId;
    private String bundleName;
    private List<CourseInfo> courses;

    @Data
    @Builder
    public static class CourseInfo {
        private Long courseId;
        private String courseTitle;
        private String courseLevel;
        private List<GroupEnrollment> groupEnrollments;
        private List<UserInfo> individualEnrollments;
    }

    @Data
    @Builder
    public static class GroupEnrollment {
        private Long groupId;
        private String groupName;
        private List<UserInfo> enrolledUsers;
    }

    @Data
    @Builder
    public static class UserInfo {
        private Long userId;
        private String fullName;

        private String enrollmentSource; // GROUP_BUNDLE / BUNDLE
        private String status;     // Completed / In Progress / Not Started
        private String adherence;  // On Time / Behind Schedule etc.

        private Double courseCompletionPercentage;
        private LocalDateTime assignedAt;
        private LocalDate deadline;
    }
}
