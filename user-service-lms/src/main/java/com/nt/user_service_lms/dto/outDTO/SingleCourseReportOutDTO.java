package com.nt.user_service_lms.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SingleCourseReportOutDTO {
    private Long courseId;
    private String courseTitle;
    private String courseDescription;
    private String courseLevel;
    private List<EnrolledUserDTO> enrolledUsers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnrolledUserDTO {
        private Long userId;
        private String fullName;

        private String enrollmentSource;
        private LocalDateTime assignedAt;
        private LocalDate deadline;

        private Long bundleId;
        private String bundleName;

        private Long groupId;
        private String groupName;

        private Double courseCompletionPercentage;
        private String status;
        private String adherence;
    }
}

