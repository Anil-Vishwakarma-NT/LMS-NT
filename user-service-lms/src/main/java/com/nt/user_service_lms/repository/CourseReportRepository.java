package com.nt.user_service_lms.repository;

import com.nt.user_service_lms.dto.outDTO.CourseReportOutDTO;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CourseReportRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CourseReportRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CourseReportOutDTO> fetchCourseKpiReport(int limit, int offset) {
        jdbcTemplate.getJdbcTemplate().execute("REFRESH MATERIALIZED VIEW course_details");

        String sql = """
                WITH group_completion AS (
                  SELECT
                    course_id,
                    group_id,
                    ROUND(AVG(course_completion_percentage)::numeric, 2) AS group_completion_percentage
                  FROM course_details
                  WHERE enrollment_source = 'GROUP'
                  GROUP BY course_id, group_id
                )
                
                SELECT
                  cd.course_id AS courseId,
                  cd.course_title AS courseTitle,
                  cd.course_description AS courseDescription,
                  cd.course_level AS courseLevel,
                
                  COUNT(DISTINCT cd.user_id) FILTER (WHERE cd.enrollment_source = 'INDIVIDUAL') AS individualEnrollments,
                  ROUND(AVG(cd.course_completion_percentage) FILTER (WHERE cd.enrollment_source = 'INDIVIDUAL')::numeric, 2) AS individualAvgCompletionPercentage,
                  MAX(cd.course_completion_percentage) FILTER (WHERE cd.enrollment_source = 'INDIVIDUAL') AS individualHighestCompletionPercentage,
                  MIN(cd.course_completion_percentage) FILTER (WHERE cd.enrollment_source = 'INDIVIDUAL') AS individualLowestCompletionPercentage,
                
                  COUNT(DISTINCT cd.group_id) FILTER (WHERE cd.enrollment_source = 'GROUP') AS groupsEnrolled,
                  ROUND(AVG(gc.group_completion_percentage)::numeric, 2) AS groupAvgCompletionPercentage,
                  MAX(gc.group_completion_percentage) AS groupHighestCompletionPercentage,
                  MIN(gc.group_completion_percentage) AS groupLowestCompletionPercentage,
                
                  COUNT(DISTINCT cd.bundle_id) AS bundlesCourseIsPartOf,
                  COUNT(DISTINCT cd.user_id) FILTER (WHERE cd.enrollment_source = 'BUNDLE') AS bundleIndividualEnrollments,
                  COUNT(DISTINCT cd.group_id) FILTER (WHERE cd.enrollment_source = 'GROUP_BUNDLE') AS bundleGroupEnrollments,
                
                  COUNT(DISTINCT cd.user_id || '-' || cd.course_id) AS totalEnrollments,
                
                  COUNT(DISTINCT cd.user_id) FILTER (WHERE cd.status = 'Completed') AS completed,
                  COUNT(DISTINCT cd.user_id) FILTER (WHERE cd.status = 'In Progress') AS inProgress,
                  COUNT(DISTINCT cd.user_id) FILTER (WHERE cd.status = 'Not Started') AS notStarted,
                
                  COUNT(DISTINCT cd.user_id) FILTER (WHERE cd.adherence = 'On Time') AS onTime,
                  COUNT(DISTINCT cd.user_id) FILTER (WHERE cd.adherence = 'Late') AS late,
                  COUNT(DISTINCT cd.user_id) FILTER (WHERE cd.adherence = 'On Track') AS onTrack,
                  COUNT(DISTINCT cd.user_id) FILTER (WHERE cd.adherence = 'Behind Schedule') AS behindSchedule,
                  COUNT(DISTINCT cd.user_id) FILTER (WHERE cd.adherence = 'Not Due Yet') AS notYetStarted,
                  COUNT(DISTINCT cd.user_id) FILTER (WHERE cd.adherence = 'Overdue') AS deadlineMissed
                
                FROM course_details cd
                LEFT JOIN group_completion gc ON cd.course_id = gc.course_id
                GROUP BY cd.course_id, cd.course_title, cd.course_description, cd.course_level
                ORDER BY cd.course_id
                LIMIT :limit OFFSET :offset;
                
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("limit", limit)
                .addValue("offset", offset);

        return jdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(CourseReportOutDTO.class));
    }

    public long getTotalCourseCount() {
        String countSql = "SELECT COUNT(DISTINCT course_id) FROM course_details";
        return jdbcTemplate.getJdbcTemplate().queryForObject(countSql, Long.class);
    }
}
