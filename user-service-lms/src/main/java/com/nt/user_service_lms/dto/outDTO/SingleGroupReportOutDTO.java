package com.nt.user_service_lms.dto.outDTO;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SingleGroupReportOutDTO {

    private Long groupId;
    private String groupName;

    private List<GroupUserCourseDTO> userCourses;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class GroupUserCourseDTO {
        private Long userId;
        private String fullName;

        private Long courseId;
        private String courseTitle;
        private String courseDescription;
        private String courseLevel;

        private String enrollmentSource;
        private LocalDateTime assignedAt;
        private LocalDateTime deadline;

        private double courseCompletionPercentage;
        private String status;
        private String adherence;

        private Long bundleId;
        private String bundleName;
    }
}

