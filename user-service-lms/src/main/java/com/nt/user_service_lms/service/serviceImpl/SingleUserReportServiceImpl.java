package com.nt.user_service_lms.service.serviceImpl;

import com.nt.user_service_lms.dto.outDTO.SingleUserReportOutDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SingleUserReportServiceImpl {

    private final JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public SingleUserReportOutDTO getUserReport(Long userId) {
        entityManager.createNativeQuery("REFRESH MATERIALIZED VIEW user_course_report_view").executeUpdate();

        String sql = "SELECT * FROM user_course_report_view WHERE user_id = ?";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, userId);

        if (rows.isEmpty()) return null;

        Map<Long, SingleUserReportOutDTO.EnrollmentDTO> enrollmentMap = new LinkedHashMap<>();
        Map<Long, Set<Long>> enrollmentContentTracker = new HashMap<>();
        SingleUserReportOutDTO userDTO = null;

        for (Map<String, Object> row : rows) {
            if (userDTO == null) {
                userDTO = SingleUserReportOutDTO.builder()
                        .userId(((Number) row.get("user_id")).longValue())
                        .username(String.valueOf(row.get("username")))
                        .fullName(String.valueOf(row.get("full_name")))
                        .email(String.valueOf(row.get("email")))
                        .role(String.valueOf(row.get("role")))
                        .enrollments(new ArrayList<>())
                        .build();
            }

            Long enrollmentId = ((Number) row.get("enrollment_id")).longValue();

            if (!enrollmentMap.containsKey(enrollmentId)) {
                SingleUserReportOutDTO.EnrollmentDTO enrollment = SingleUserReportOutDTO.EnrollmentDTO.builder()
                        .enrollmentId(enrollmentId)
                        .enrollmentSource(String.valueOf(row.get("enrollment_source")))
                        .sourceType(String.valueOf(row.get("source_type")))
                        .groupId(row.get("group_id") != null ? ((Number) row.get("group_id")).longValue() : null)
                        .groupName(String.valueOf(row.get("group_name")))
                        .bundleId(row.get("bundle_id") != null ? ((Number) row.get("bundle_id")).longValue() : null)
                        .bundleName(String.valueOf(row.get("bundle_name")))
                        .courseId(((Number) row.get("course_id")).longValue())
                        .courseTitle(String.valueOf(row.get("course_title")))
                        .assignedAt(row.get("assigned_at") != null ? ((java.sql.Timestamp) row.get("assigned_at")).toLocalDateTime() : null)
                        .deadline(row.get("deadline") != null ? ((java.sql.Timestamp) row.get("deadline")).toLocalDateTime() : null)
                        .courseCompletionPercentage(row.get("course_completion_percentage") != null ? ((Number) row.get("course_completion_percentage")).doubleValue() : null)
                        .status(String.valueOf(row.get("status")))
                        .adherence(String.valueOf(row.get("adherence")))
                        .contentProgress(new ArrayList<>())
                        .build();

                enrollmentMap.put(enrollmentId, enrollment);
                enrollmentContentTracker.put(enrollmentId, new HashSet<>()); // init tracker for this enrollment
            }

            if (row.get("content_id") != null) {
                Long contentId = ((Number) row.get("content_id")).longValue();

                // deduplication logic
                if (!enrollmentContentTracker.get(enrollmentId).contains(contentId)) {
                    SingleUserReportOutDTO.ContentDTO contentProgress = SingleUserReportOutDTO.ContentDTO.builder()
                            .contentId(contentId)
                            .contentTitle(String.valueOf(row.get("content_title")))
                            .contentType(String.valueOf(row.get("content_type")))
                            .contentCompletionPercentage(row.get("content_completion_percentage") != null
                                    ? ((Number) row.get("content_completion_percentage")).doubleValue()
                                    : null)
                            .build();

                    enrollmentMap.get(enrollmentId).getContentProgress().add(contentProgress);
                    enrollmentContentTracker.get(enrollmentId).add(contentId); // mark as seen
                }
            }
        }

        userDTO.getEnrollments().addAll(enrollmentMap.values());
        return userDTO;
    }
}
