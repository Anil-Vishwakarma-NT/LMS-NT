
package com.nt.user_service_lms.service.serviceImpl;

import com.nt.user_service_lms.dto.outDTO.SingleBundleReportOutDTO;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SingleBundleReportServiceImpl {

    private final EntityManager entityManager;

    public SingleBundleReportOutDTO getBundleReport(Long bundleId) {
        List<Object[]> rows = entityManager.createNativeQuery("SELECT * FROM bundle_details WHERE bundle_id = :bundleId")
                .setParameter("bundleId", bundleId)
                .getResultList();

        if (rows.isEmpty()) {
            throw new RuntimeException("No data found for bundleId: " + bundleId);
        }

        Map<Long, SingleBundleReportOutDTO.CourseInfo> courseMap = new LinkedHashMap<>();
        Map<String, SingleBundleReportOutDTO.GroupEnrollment> groupMap = new LinkedHashMap<>();

        String bundleName = null;

        for (Object[] r : rows) {
            String enrollmentSource = (String) r[0];
            Long groupId = (r[1] != null) ? ((Number) r[1]).longValue() : null;
            Long userId = ((Number) r[2]).longValue();
            String fullName = (String) r[4];
            String status = (String) r[28];
            String adherence = (String) r[29];
            Long courseId = ((Number) r[16]).longValue();

            bundleName = (String) r[22];

            courseMap.putIfAbsent(courseId, SingleBundleReportOutDTO.CourseInfo.builder()
                    .courseId(courseId)
                    .courseTitle((String) r[18])
                    .courseLevel((String) r[20])
                    .groupEnrollments(new ArrayList<>())
                    .individualEnrollments(new ArrayList<>())
                    .build());

            SingleBundleReportOutDTO.UserInfo userInfo = SingleBundleReportOutDTO.UserInfo.builder()
                    .userId(userId)
                    .fullName(fullName)
                    .enrollmentSource(enrollmentSource)
                    .status(status)
                    .adherence(adherence)
                    .courseCompletionPercentage(r[25] != null ? ((Number) r[25]).doubleValue() : null)
                    .assignedAt(r[11] != null ? ((java.sql.Timestamp) r[11]).toLocalDateTime() : null)
                    .deadline(r[12] != null ? ((java.sql.Timestamp) r[12]).toLocalDateTime().toLocalDate() : null)
                    .build();


            if ("GROUP_BUNDLE".equalsIgnoreCase(enrollmentSource)) {
                String groupKey = courseId + ":" + groupId;
                groupMap.putIfAbsent(groupKey, SingleBundleReportOutDTO.GroupEnrollment.builder()
                        .groupId(groupId)
                        .groupName((String) r[24])
                        .enrolledUsers(new ArrayList<>())
                        .build());
                groupMap.get(groupKey).getEnrolledUsers().add(userInfo);
            } else {
                courseMap.get(courseId).getIndividualEnrollments().add(userInfo);
            }
        }

        // Attach groups to corresponding courses
        for (Map.Entry<String, SingleBundleReportOutDTO.GroupEnrollment> entry : groupMap.entrySet()) {
            String[] keyParts = entry.getKey().split(":");
            Long courseId = Long.parseLong(keyParts[0]);

            if (courseMap.containsKey(courseId)) {
                courseMap.get(courseId).getGroupEnrollments().add(entry.getValue());
            }
        }

        return SingleBundleReportOutDTO.builder()
                .bundleId(bundleId)
                .bundleName(bundleName)
                .courses(new ArrayList<>(courseMap.values()))
                .build();
    }
}
