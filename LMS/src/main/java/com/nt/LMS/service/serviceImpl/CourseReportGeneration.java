package com.nt.LMS.service.serviceImpl;

import com.nt.LMS.dto.outDTO.CourseContentReport;
import com.nt.LMS.dto.outDTO.CourseEnrolledUserReport;
import com.nt.LMS.dto.outDTO.CourseReport;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CourseReportGeneration {

    private final JdbcTemplate jdbcTemplate;

    public CourseReport generateReportForCourse(Long courseId) {
        String query = "WITH course_details AS ( " +
                " SELECT c.course_id, c.title AS name, c.description, c.level, c.created_at, " +
                " cc.title AS content_name, cc.description AS content_description, cc.created_at AS content_created_at, " +
                " u.firstname || ' ' || u.lastname AS user_enrolled, up.course_completion_percentage AS percentage_completed, " +
                " up.last_updated AS last_viewed, uce.deadline AS deadline " +
                " FROM course c " +
                " LEFT JOIN course_content cc ON c.course_id = cc.course_id " +
                " LEFT JOIN user_course_enrollment uce ON c.course_id = uce.course_id " +
                " LEFT JOIN users u ON u.user_id = uce.user_id " +
                " LEFT JOIN user_progress up ON uce.user_id = up.user_id AND c.course_id = up.course_id AND up.content_id = cc.course_content_id " +
                ") " +
                "SELECT * FROM course_details WHERE course_id = ?";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(query, courseId);

        if (rows.isEmpty()) return null;

        Map<String, Object> first = rows.get(0);
        CourseReport report = CourseReport.builder()
                .courseId(courseId)
                .name((String) first.get("name"))
                .description((String) first.get("description"))
                .level((String) first.get("level"))
                .createdAt(toDateTime(first.get("created_at")))
                .build();

        Set<CourseContentReport> contents = new HashSet<>();
        Map<String, CourseEnrolledUserReport> enrolledUserMap = new LinkedHashMap<>();

        for (Map<String, Object> row : rows) {
            String contentName = (String) row.get("content_name");
            if (contentName != null) {
                contents.add(CourseContentReport.builder()
                        .contentName(contentName)
                        .contentDescription((String) row.get("content_description"))
                        .contentCreatedAt(toDateTime(row.get("content_created_at")))
                        .build());
            }

            String userEnrolled = (String) row.get("user_enrolled");
            if (userEnrolled != null) {
                LocalDateTime lastViewed = toDateTime(row.get("last_viewed"));
                Double percentageCompleted = toDouble(row.get("percentage_completed"));
                LocalDateTime deadline = toDateTime(row.get("deadline"));

                CourseEnrolledUserReport existing = enrolledUserMap.get(userEnrolled);

                if (existing == null) {
                    enrolledUserMap.put(userEnrolled, CourseEnrolledUserReport.builder()
                            .userEnrolled(userEnrolled)
                            .percentageCompleted(percentageCompleted)
                            .lastViewed(lastViewed)
                            .deadline(deadline)
                            .build());
                } else {
                    // Update lastViewed if newer
                    if (lastViewed != null &&
                            (existing.getLastViewed() == null || lastViewed.isAfter(existing.getLastViewed()))) {
                        existing.setLastViewed(lastViewed);
                    }
                }
            }
        }

        report.setContents(contents);
        report.setEnrolledUsers(new HashSet<>(enrolledUserMap.values()));
        return report;
    }

    private LocalDateTime toDateTime(Object obj) {
        return obj == null ? null : ((Timestamp) obj).toLocalDateTime();
    }

    private Double toDouble(Object obj) {
        return obj == null ? null : ((Number) obj).doubleValue();
    }
}
