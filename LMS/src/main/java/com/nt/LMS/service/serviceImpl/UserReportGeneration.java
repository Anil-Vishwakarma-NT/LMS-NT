package com.nt.LMS.service.serviceImpl;

import com.nt.LMS.dto.outDTO.UserCourseReport;
import com.nt.LMS.dto.outDTO.UserReport;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserReportGeneration {

    private final JdbcTemplate jdbcTemplate;

    public UserReport generateUserReport(Long userId) {
        String sql = "WITH user_details AS ( " +
                "  SELECT u.user_id, u.firstname || ' ' || u.lastname AS name, u.username, u.email, r.name AS role, u.created_at " +
                "  FROM users u LEFT JOIN role r ON u.role_id = r.role_id " +
                "), " +
                "user_enrollment AS ( " +
                "  SELECT ud.user_id, ud.name, ud.username, ud.email, ud.role, ud.created_at, " +
                "         c.title AS course_name, c.description AS course_description, c.level AS course_level, " +
                "         up.course_completion_percentage, up.last_updated AS last_viewed, " +
                "         ab.firstname || ' ' || ab.lastname AS assigned_by, " +
                "         uce.assigned_at, uce.deadline, uce.status " +
                "  FROM user_details ud " +
                "  LEFT JOIN user_course_enrollment uce ON ud.user_id = uce.user_id " +
                "  LEFT JOIN users ab ON uce.assigned_by = ab.user_id " +
                "  LEFT JOIN course c ON uce.course_id = c.course_id " +
                "  LEFT JOIN user_progress up ON ud.user_id = up.user_id AND uce.course_id = up.course_id " +
                ") " +
                "SELECT * FROM user_enrollment WHERE user_id = ?";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, userId);
        if (rows.isEmpty()) return null;

        Map<String, Object> first = rows.get(0);
        UserReport report = new UserReport();
        report.setUserId(((Number) first.get("user_id")).longValue());
        report.setName((String) first.get("name"));
        report.setUsername((String) first.get("username"));
        report.setEmail((String) first.get("email"));
        report.setRole((String) first.get("role"));
        report.setCreatedAt(toDateTime(first.get("created_at")));

        Map<String, UserCourseReport> courseMap = new LinkedHashMap<>();

        for (Map<String, Object> row : rows) {
            String courseName = (String) row.get("course_name");
            if (courseName == null) continue; // skip if no course

            UserCourseReport course = courseMap.get(courseName);
            LocalDateTime lastViewed = toDateTime(row.get("last_viewed"));

            if (course == null) {
                course = new UserCourseReport();
                course.setCourseName(courseName);
                course.setCourseDescription((String) row.get("course_description"));
                course.setCourseLevel((String) row.get("course_level"));
                course.setCourseCompletionPercentage(toDouble(row.get("course_completion_percentage")));
                course.setAssignedBy((String) row.get("assigned_by"));
                course.setAssignedAt(toDateTime(row.get("assigned_at")));
                course.setDeadline(toDateTime(row.get("deadline")));
                course.setStatus((String) row.get("status"));
                course.setLastViewed(lastViewed);
                courseMap.put(courseName, course);
            } else {
                // Replace lastViewed if current one is newer
                if (lastViewed != null && (course.getLastViewed() == null || lastViewed.isAfter(course.getLastViewed()))) {
                    course.setLastViewed(lastViewed);
                }
            }
        }

        report.setEnrolledCourses(new ArrayList<>(courseMap.values()));
        return report;
    }

    private LocalDateTime toDateTime(Object obj) {
        return obj == null ? null : ((Timestamp) obj).toLocalDateTime();
    }

    private Double toDouble(Object obj) {
        return obj == null ? null : ((Number) obj).doubleValue();
    }
}
