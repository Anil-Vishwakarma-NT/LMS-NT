package com.nt.user_service_lms.repository;

import com.nt.user_service_lms.dto.outDTO.BundleReportOutDTO;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BundleReportRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public BundleReportRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<BundleReportOutDTO> fetchBundleKpiReport(int limit, int offset) {
        jdbcTemplate.getJdbcTemplate().execute("REFRESH MATERIALIZED VIEW bundle_details");

        String sql = """
    SELECT
      bundle_id AS bundleId,
      bundle_name AS bundleName,

      COUNT(DISTINCT user_id) FILTER (WHERE enrollment_source = 'BUNDLE') AS individualUsersEnrolled,
      MAX(course_completion_percentage) FILTER (WHERE enrollment_source = 'BUNDLE') AS individualHighestCompletion,
      MIN(course_completion_percentage) FILTER (WHERE enrollment_source = 'BUNDLE') AS individualLowestCompletion,
      ROUND(AVG(course_completion_percentage) FILTER (WHERE enrollment_source = 'BUNDLE')::numeric, 2) AS individualAvgCompletion,

      COUNT(DISTINCT group_id) FILTER (WHERE enrollment_source = 'GROUP_BUNDLE') AS groupsEnrolled,
      ROUND(MAX(course_completion_percentage) FILTER (WHERE enrollment_source = 'GROUP_BUNDLE')::numeric, 2) AS groupHighestCompletion,
      ROUND(MIN(course_completion_percentage) FILTER (WHERE enrollment_source = 'GROUP_BUNDLE')::numeric, 2) AS groupLowestCompletion,
      ROUND(AVG(course_completion_percentage) FILTER (WHERE enrollment_source = 'GROUP_BUNDLE')::numeric, 2) AS groupAvgCompletion,

      MAX(total_users_enrolled) AS totalUsersEnrolled,
      COUNT(DISTINCT course_id) AS totalCourses,
      COUNT(DISTINCT user_id || '-' || course_id) AS totalEnrollments,

      COUNT(DISTINCT user_id || '-' || course_id) FILTER (WHERE status = 'Completed') AS coursesCompleted,
      COUNT(DISTINCT user_id || '-' || course_id) FILTER (WHERE status = 'In Progress') AS coursesInProgress,
      COUNT(DISTINCT user_id || '-' || course_id) FILTER (WHERE status = 'Not Started') AS coursesNotStarted,

      COUNT(DISTINCT user_id || '-' || course_id) FILTER (WHERE adherence = 'On Time') AS coursesCompletedOnTime,
      COUNT(DISTINCT user_id || '-' || course_id) FILTER (WHERE adherence = 'Late') AS coursesCompletedLate,
      COUNT(DISTINCT user_id || '-' || course_id) FILTER (WHERE adherence = 'On Track') AS coursesOnTrack,
      COUNT(DISTINCT user_id || '-' || course_id) FILTER (WHERE adherence = 'Behind Schedule') AS coursesBehindSchedule,
      COUNT(DISTINCT user_id || '-' || course_id) FILTER (WHERE adherence = 'Not Due Yet') AS coursesNotDueYet,
      COUNT(DISTINCT user_id || '-' || course_id) FILTER (WHERE adherence = 'Overdue') AS coursesOverdue

    FROM bundle_details
    GROUP BY bundle_id, bundle_name
    ORDER BY bundle_id
    LIMIT :limit OFFSET :offset
""";


        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("limit", limit)
                .addValue("offset", offset);

        return jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(BundleReportOutDTO.class));
    }

    public long getTotalBundleCount() {
        String countSql = "SELECT COUNT(DISTINCT bundle_id) FROM bundle_details";
        return jdbcTemplate.getJdbcTemplate().queryForObject(countSql, Long.class);
    }

}
