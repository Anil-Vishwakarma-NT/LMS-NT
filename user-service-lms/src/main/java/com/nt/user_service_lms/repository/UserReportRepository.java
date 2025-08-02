package com.nt.user_service_lms.repository;

import com.nt.user_service_lms.dto.outDTO.UserReportOutDTO;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserReportRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public UserReportRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<UserReportOutDTO> fetchUserKpiReport(int limit, int offset) {
        jdbcTemplate.getJdbcTemplate().execute("REFRESH MATERIALIZED VIEW user_details");

        String sql = """
                SELECT
                  user_id AS userId,
                  full_name AS fullName,
                  email,
                  role,
                  manager,
                
                  COUNT(DISTINCT course_id) FILTER (WHERE enrollment_source = 'INDIVIDUAL') AS individualEnrollments,
                  ROUND(AVG(course_completion_percentage) FILTER (WHERE enrollment_source = 'INDIVIDUAL')::numeric, 2) AS individualAvgCompletionPercentage,
                  MAX(course_completion_percentage) FILTER (WHERE enrollment_source = 'INDIVIDUAL') AS individualHighestCompletionPercentage,
                  MIN(course_completion_percentage) FILTER (WHERE enrollment_source = 'INDIVIDUAL') AS individualLowestCompletionPercentage,
                
                  COUNT(DISTINCT bundle_id) FILTER (WHERE enrollment_source = 'BUNDLE') AS bundlesEnrolled,
                  ROUND(AVG(course_completion_percentage) FILTER (WHERE enrollment_source = 'BUNDLE')::numeric, 2) AS bundleAvgCompletionPercentage,
                  MAX(course_completion_percentage) FILTER (WHERE enrollment_source = 'BUNDLE') AS bundleHighestCompletionPercentage,
                  MIN(course_completion_percentage) FILTER (WHERE enrollment_source = 'BUNDLE') AS bundleLowestCompletionPercentage,
                
                  -- New group-based fields
                  COUNT(DISTINCT group_id) AS groups_part_of,
                  COUNT(DISTINCT course_id) FILTER (WHERE enrollment_source = 'GROUP') AS groupCourseEnrollments,
                  COUNT(DISTINCT bundle_id) FILTER (WHERE enrollment_source = 'GROUP_BUNDLE') AS groupBundleEnrollments,
                
                  -- Updated logic for total enrollments
                  COUNT(DISTINCT course_id ) FILTER (WHERE is_enrollment_active) AS total_enrollments,
                
                  -- Status fields
                  COUNT(DISTINCT user_id || '-' || course_id) FILTER (WHERE status = 'Completed') AS courses_completed,
                  COUNT(DISTINCT user_id || '-' || course_id) FILTER (WHERE status = 'In Progress') AS courses_in_progress,
                  COUNT(DISTINCT user_id || '-' || course_id) FILTER (WHERE status = 'Not Started') AS courses_not_started,
                
                  -- Adherence fields
                  COUNT(DISTINCT user_id || '-' || course_id) FILTER (WHERE adherence = 'On Time') AS courses_completed_on_time,
                  COUNT(DISTINCT user_id || '-' || course_id) FILTER (WHERE adherence = 'Late') AS course_completed_late,
                  COUNT(DISTINCT user_id || '-' || course_id) FILTER (WHERE adherence = 'On Track') AS courses_on_track,
                  COUNT(DISTINCT user_id || '-' || course_id) FILTER (WHERE adherence = 'Behind Schedule') AS courses_not_on_track,
                  COUNT(DISTINCT user_id || '-' || course_id) FILTER (WHERE adherence = 'Not Due Yet') AS courses_yet_to_start,
                  COUNT(DISTINCT user_id || '-' || course_id) FILTER (WHERE adherence = 'Overdue') AS deadline_missed_courses
                
                FROM user_details
                GROUP BY user_id, full_name, email, role, manager
                ORDER BY user_id
                LIMIT :limit OFFSET :offset;
                
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("limit", limit)
                .addValue("offset", offset);

        return jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(UserReportOutDTO.class));
    }

    public long getTotalUserCount() {
        String countSql = "SELECT COUNT(DISTINCT user_id) FROM user_details";
        return jdbcTemplate.getJdbcTemplate().queryForObject(countSql, Long.class);
    }


}
