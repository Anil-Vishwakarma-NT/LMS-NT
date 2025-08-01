package com.nt.user_service_lms.repository;

import com.nt.user_service_lms.dto.outDTO.GroupReportOutDTO;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GroupReportRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public GroupReportRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<GroupReportOutDTO> fetchGroupKpiReport(int limit, int offset) {
        jdbcTemplate.getJdbcTemplate().execute("REFRESH MATERIALIZED VIEW group_details");

        String sql = """
            SELECT
              group_id AS groupId,
              group_name AS groupName,

              COUNT(DISTINCT course_id) FILTER (WHERE enrollment_source = 'GROUP') AS individualCoursesEnrolled,
              ROUND(AVG(course_completion_percentage) FILTER (WHERE enrollment_source = 'GROUP')::numeric, 2) AS individualCoursesAvgCompletionPercentage,
              MAX(course_completion_percentage) FILTER (WHERE enrollment_source = 'GROUP') AS individualCoursesHighestCompletionPercentage,
              MIN(course_completion_percentage) FILTER (WHERE enrollment_source = 'GROUP') AS individualCoursesLowestCompletionPercentage,

              COUNT(DISTINCT bundle_id) FILTER (WHERE enrollment_source = 'GROUP_BUNDLE') AS bundlesEnrolled,
              ROUND(AVG(course_completion_percentage) FILTER (WHERE enrollment_source = 'GROUP_BUNDLE')::numeric, 2) AS bundleAvgCompletionPercentage,
              MAX(course_completion_percentage) FILTER (WHERE enrollment_source = 'GROUP_BUNDLE') AS bundleHighestCompletionPercentage,
              MIN(course_completion_percentage) FILTER (WHERE enrollment_source = 'GROUP_BUNDLE') AS bundleLowestCompletionPercentage,

              COUNT(DISTINCT course_id) AS total_courses_enrolled,
              COUNT(DISTINCT user_id) AS total_users_in_group,
              COUNT(*) FILTER (WHERE is_enrollment_active) AS total_enrollments,

              COUNT(DISTINCT user_id || '-' || course_id) FILTER (WHERE status = 'Completed') AS coursesCompleted,
              COUNT(DISTINCT user_id || '-' || course_id) FILTER (WHERE status = 'In Progress') AS coursesInProgress,
              COUNT(DISTINCT user_id || '-' || course_id) FILTER (WHERE status = 'Not Started') AS coursesNotStarted,

              COUNT(DISTINCT user_id || '-' || course_id) FILTER (WHERE adherence = 'On Time') AS coursesCompletedOnTime,
              COUNT(DISTINCT user_id || '-' || course_id) FILTER (WHERE adherence = 'Late') AS courseCompletedLate,
              COUNT(DISTINCT user_id || '-' || course_id) FILTER (WHERE adherence = 'On Track') AS coursesOnTrack,
              COUNT(DISTINCT user_id || '-' || course_id) FILTER (WHERE adherence = 'Behind Schedule') AS coursesNotOnTrack,
              COUNT(DISTINCT user_id || '-' || course_id) FILTER (WHERE adherence = 'Not Due Yet') AS coursesYetToStart,
              COUNT(DISTINCT user_id || '-' || course_id) FILTER (WHERE adherence = 'Overdue') AS deadlineMissedCourses

            FROM group_details
            GROUP BY group_id, group_name
            ORDER BY group_id
            LIMIT :limit OFFSET :offset
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("limit", limit)
                .addValue("offset", offset);

        return jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(GroupReportOutDTO.class));
    }

    public long getTotalGroupCount() {
        String countSql = "SELECT COUNT(DISTINCT group_id) FROM group_details";
        return jdbcTemplate.getJdbcTemplate().queryForObject(countSql, Long.class);
    }
}
