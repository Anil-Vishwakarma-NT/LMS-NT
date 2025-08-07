package com.nt.user_service_lms.dto.outDTO;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SingleUserReportOutDTO {
    private Long userId;
    private String fullName;
    private String username;
    private String email;
    private String role;

    private List<EnrollmentDTO> enrollments;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class EnrollmentDTO {
        private Long enrollmentId;
        private String enrollmentSource;
        private String sourceType;

        private Long groupId;
        private String groupName;

        private Long bundleId;
        private String bundleName;

        private Long courseId;
        private String courseTitle;

        private LocalDateTime assignedAt;
        private LocalDateTime deadline;


        private Double courseCompletionPercentage;
        private String status;
        private String adherence;

        private List<ContentDTO> contentProgress;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ContentDTO {
        private Long contentId;
        private String contentTitle;
        private String contentType;
        private Double contentCompletionPercentage;
    }
}
