package com.nt.user_service_lms.service.serviceImpl;

import java.util.HashMap;
import java.util.Map;

public class CourseReportKPIQueries {

    public static final String base_query =
            "WITH course_base AS ( " +
                    "SELECT c.course_id, c.title AS course_name, c.description AS course_description, " +
                    "c.level, c.is_active, cc.course_content_id, cc.title AS content_title, cc.description AS content_description " +
                    "FROM course c LEFT JOIN course_content cc ON c.course_id = cc.course_id " +
                    "), " +
                    "deduplicated_enrollments AS ( " +
                    "SELECT DISTINCT ON (user_id, course_id) user_id, course_id, assigned_at, deadline " +
                    "FROM enrollments ORDER BY user_id, course_id, assigned_at ASC " +
                    "), " +
                    "course_with_enrollments AS ( " +
                    "SELECT cb.*, de.user_id, de.assigned_at, de.deadline, " +
                    "u.firstname || ' ' || u.lastname AS user_full_name " +
                    "FROM course_base cb " +
                    "LEFT JOIN deduplicated_enrollments de ON cb.course_id = de.course_id " +
                    "LEFT JOIN users u ON de.user_id = u.user_id " +
                    "), " +
                    "course_full_progress AS ( " +
                    "SELECT cwe.course_id, cwe.course_name, cwe.course_description, cwe.level, cwe.is_active, " +
                    "cwe.course_content_id, cwe.content_title, cwe.content_description, cwe.user_id, " +
                    "cwe.user_full_name AS user_enrolled, cwe.assigned_at, cwe.deadline, " +
                    "up.content_completion_percentage, up.course_completion_percentage, up.first_completed_at, up.content_type " +
                    "FROM course_with_enrollments cwe " +
                    "LEFT JOIN user_progress up ON cwe.user_id = up.user_id AND cwe.course_id = up.course_id AND cwe.course_content_id = up.content_id " +
                    ")";

    public static final Map<String, String> course_kpi_queries = new HashMap<>();

    static {
        course_kpi_queries.put(
                "Global: Total Courses",
                "SELECT COUNT(DISTINCT course_id) AS total_courses\n" +
                        "FROM course_full_progress"
        );

        course_kpi_queries.put(
                "Global: Active Courses",
                "SELECT COUNT(DISTINCT course_id) AS active_courses\n" +
                        "FROM course_full_progress\n" +
                        "WHERE is_active = true"
        );

        course_kpi_queries.put(
                "Global: Inactive Courses",
                "SELECT COUNT(DISTINCT course_id) AS inactive_courses\n" +
                        "FROM course_full_progress\n" +
                        "WHERE is_active = false"
        );

        course_kpi_queries.put(
                "Global: Beginner-Level Courses",
                "SELECT COUNT(DISTINCT course_id) AS beginner_level_courses\n" +
                        "FROM course_full_progress\n" +
                        "WHERE level = 'BEGINNER'"
        );

        course_kpi_queries.put(
                "Global: Intermediate-Level Courses",
                "SELECT COUNT(DISTINCT course_id) AS intermediate_level_courses\n" +
                        "FROM course_full_progress\n" +
                        "WHERE level = 'INTERMEDIATE'"
        );

        course_kpi_queries.put(
                "Global: Professional-Level Courses",
                "SELECT COUNT(DISTINCT course_id) AS professional_level_courses\n" +
                        "FROM course_full_progress\n" +
                        "WHERE level = 'PROFESSIONAL'"
        );

        course_kpi_queries.put(
                "Global: Course Enrollments",
                "SELECT COUNT(DISTINCT user_id || '-' || course_id) AS total_course_enrollments\n" +
                        "FROM course_full_progress"
        );

        course_kpi_queries.put(
                "Global: Enrolled Courses Completed",
                "SELECT COUNT(*) AS total_completed_courses\n" +
                        "FROM (\n" +
                        "    SELECT user_id, course_id, MAX(course_completion_percentage) AS course_completion_percentage\n" +
                        "    FROM course_full_progress\n" +
                        "    GROUP BY user_id, course_id\n" +
                        ") x\n" +
                        "WHERE course_completion_percentage >= 95"
        );
        
        course_kpi_queries.put(
                "Global: Enrolled Courses In-Progress",
                "SELECT COUNT(*) AS total_in_progress_courses\n" +
                        "FROM (\n" +
                        "    SELECT user_id, course_id, MAX(course_completion_percentage) AS course_completion_percentage,\n" +
                        "           MIN(deadline) AS deadline\n" +
                        "    FROM course_full_progress\n" +
                        "    GROUP BY user_id, course_id\n" +
                        ") x\n" +
                        "WHERE course_completion_percentage BETWEEN 0.01 AND 94.99\n" +
                        "  AND CURRENT_DATE <= deadline"
        );
        
        course_kpi_queries.put(
                "Global: Enrolled Courses Not Started",
                "SELECT COUNT(*) AS total_not_started_courses\n" +
                        "FROM (\n" +
                        "    SELECT user_id, course_id, MAX(course_completion_percentage) AS course_completion_percentage,\n" +
                        "           MIN(deadline) AS deadline\n" +
                        "    FROM course_full_progress\n" +
                        "    GROUP BY user_id, course_id\n" +
                        ") x\n" +
                        "WHERE (course_completion_percentage = 0 or course_completion_percentage is null)\n" +
                        "  AND CURRENT_DATE <= deadline"
        );

        course_kpi_queries.put(
                "Global: Enrolled Courses Completed on Time",
                "SELECT COUNT(*) AS completed_on_time\n" +
                        "FROM (\n" +
                        "    SELECT user_id, course_id, MAX(course_completion_percentage) AS course_completion_percentage,\n" +
                        "           MIN(first_completed_at) AS first_completed_at,\n" +
                        "           MIN(deadline) AS deadline\n" +
                        "    FROM course_full_progress\n" +
                        "    GROUP BY user_id, course_id\n" +
                        ") x\n" +
                        "WHERE course_completion_percentage >= 95\n" +
                        "  AND first_completed_at <= deadline"
        );

        course_kpi_queries.put(
                "Global: Enrolled Courses Completed Late",
                "SELECT COUNT(*) AS completed_late\n" +
                        "FROM (\n" +
                        "    SELECT user_id, course_id, MAX(course_completion_percentage) AS course_completion_percentage,\n" +
                        "           MIN(first_completed_at) AS first_completed_at,\n" +
                        "           MIN(deadline) AS deadline\n" +
                        "    FROM course_full_progress\n" +
                        "    GROUP BY user_id, course_id\n" +
                        ") x\n" +
                        "WHERE course_completion_percentage >= 95\n" +
                        "  AND first_completed_at > deadline"
        );

        course_kpi_queries.put(
                "Global: Enrolled Courses on Track",
                "SELECT COUNT(*) AS in_progress_on_time\n" +
                        "FROM (\n" +
                        "    SELECT user_id, course_id, MAX(course_completion_percentage) AS course_completion_percentage,\n" +
                        "           MIN(deadline) AS deadline\n" +
                        "    FROM course_full_progress\n" +
                        "    GROUP BY user_id, course_id\n" +
                        ") x\n" +
                        "WHERE course_completion_percentage BETWEEN 0.01 AND 94.99\n" +
                        "  AND CURRENT_DATE <= deadline"
        );

        course_kpi_queries.put(
                "Global: Enrolled Courses Not on Track",
                "SELECT COUNT(*) AS in_progress_late\n" +
                        "FROM (\n" +
                        "    SELECT user_id, course_id, MAX(course_completion_percentage) AS course_completion_percentage,\n" +
                        "           MIN(deadline) AS deadline\n" +
                        "    FROM course_full_progress\n" +
                        "    GROUP BY user_id, course_id\n" +
                        ") x\n" +
                        "WHERE course_completion_percentage BETWEEN 0.01 AND 94.99\n" +
                        "  AND CURRENT_DATE > deadline"
        );

        course_kpi_queries.put(
                "Global: Enrolled Courses Yet to be Started (On Time)",
                "SELECT COUNT(*) AS not_started_on_time\n" +
                        "FROM (\n" +
                        "    SELECT user_id, course_id, MAX(course_completion_percentage) AS course_completion_percentage,\n" +
                        "           MIN(deadline) AS deadline\n" +
                        "    FROM course_full_progress\n" +
                        "    GROUP BY user_id, course_id\n" +
                        ") x\n" +
                        "WHERE (course_completion_percentage = 0 or course_completion_percentage is null)\n" +
                        "  AND CURRENT_DATE <= deadline"
        );

        course_kpi_queries.put(
                "Global: Enrolled Courses Yet to be Started (Deadline Missed)",
                "SELECT COUNT(*) AS not_started_late\n" +
                        "FROM (\n" +
                        "    SELECT user_id, course_id, MAX(course_completion_percentage) AS course_completion_percentage,\n" +
                        "           MIN(deadline) AS deadline\n" +
                        "    FROM course_full_progress\n" +
                        "    GROUP BY user_id, course_id\n" +
                        ") x\n" +
                        "WHERE (course_completion_percentage = 0 or course_completion_percentage is null)\n" +
                        "  AND CURRENT_DATE > deadline"
        );

        //per course

        course_kpi_queries.put(
                "Course: Total Enrolled Users",
                "SELECT COUNT(DISTINCT user_id) AS total_enrolled_users " +
                        "FROM course_full_progress " +
                        "WHERE course_id = ?"
        );

        course_kpi_queries.put(
                "Course: Avg Completion %",
                "SELECT \n" +
                        "  ROUND(AVG(COALESCE(course_completion_percentage, 0))::numeric, 2) AS average_completion_percentage\n" +
                        "FROM (\n" +
                        "  SELECT course_id, user_id, MAX(course_completion_percentage) AS course_completion_percentage\n" +
                        "  FROM course_full_progress\n" +
                        "  WHERE course_id = ?\n" +
                        "  GROUP BY course_id, user_id\n" +
                        ") x"
        );

        course_kpi_queries.put(
                "Course: Highest Completion %",
                "SELECT \n" +
                        "  MAX(COALESCE(course_completion_percentage, 0)) AS highest_completion_percentage\n" +
                        "FROM (\n" +
                        "  SELECT user_id, MAX(course_completion_percentage) AS course_completion_percentage\n" +
                        "  FROM course_full_progress\n" +
                        "  WHERE course_id = ?\n" +
                        "  GROUP BY user_id\n" +
                        ") x"
        );

        course_kpi_queries.put(
                "Course: Lowest Completion %",
                "SELECT \n" +
                        "  MIN(COALESCE(course_completion_percentage, 0)) AS lowest_completion_percentage\n" +
                        "FROM (\n" +
                        "  SELECT user_id, MAX(course_completion_percentage) AS course_completion_percentage\n" +
                        "  FROM course_full_progress\n" +
                        "  WHERE course_id = ?\n" +
                        "  GROUP BY user_id\n" +
                        ") x"
        );

        course_kpi_queries.put(
                "Course: Users Completed the Course",
                "SELECT \n" +
                        "  COALESCE(SUM(CASE WHEN course_completion_percentage >= 95 THEN 1 ELSE 0 END), 0) AS users_completed_course\n" +
                        "FROM (\n" +
                        "  SELECT course_id, user_id, MAX(course_completion_percentage) AS course_completion_percentage\n" +
                        "  FROM course_full_progress\n" +
                        "  WHERE course_id = ?\n" +
                        "  GROUP BY course_id, user_id\n" +
                        ") x"
        );

        course_kpi_queries.put(
                "Course: Users In Progress",
                "SELECT \n" +
                        "  COALESCE(SUM(CASE \n" +
                        "                 WHEN course_completion_percentage BETWEEN 0.01 AND 94.99 \n" +
                        "                 THEN 1 ELSE 0 \n" +
                        "               END), 0) AS users_in_progress\n" +
                        "FROM (\n" +
                        "  SELECT course_id, user_id, MAX(course_completion_percentage) AS course_completion_percentage\n" +
                        "  FROM course_full_progress\n" +
                        "  WHERE course_id = ?\n" +
                        "  GROUP BY course_id, user_id\n" +
                        ") x"
        );

        course_kpi_queries.put(
                "Course: Users Not Started",
                "SELECT \n" +
                        "  COALESCE(SUM(CASE \n" +
                        "                 WHEN (course_completion_percentage = 0 OR course_completion_percentage IS NULL)\n" +
                        "                 THEN 1 ELSE 0 \n" +
                        "               END), 0) AS users_not_started\n" +
                        "FROM (\n" +
                        "  SELECT course_id, user_id, MAX(course_completion_percentage) AS course_completion_percentage\n" +
                        "  FROM course_full_progress\n" +
                        "  WHERE course_id = ?\n" +
                        "  GROUP BY course_id, user_id\n" +
                        ") x"
        );

        course_kpi_queries.put(
                "Course: Users Completed on Time",
                "SELECT \n" +
                        "  SUM(CASE WHEN course_completion_percentage >= 95 \n" +
                        "            AND first_completed_at <= deadline\n" +
                        "           THEN 1 ELSE 0 END) AS users_completed_on_time\n" +
                        "FROM (\n" +
                        "  SELECT user_id, MAX(course_completion_percentage) AS course_completion_percentage,\n" +
                        "         MIN(first_completed_at) AS first_completed_at,\n" +
                        "         MIN(deadline) AS deadline\n" +
                        "  FROM course_full_progress\n" +
                        "  WHERE course_id = ?\n" +
                        "  GROUP BY user_id\n" +
                        ") x"
        );

        course_kpi_queries.put(
                "Course: Users Completed Late",
                "SELECT \n" +
                        "  SUM(CASE WHEN course_completion_percentage >= 95 \n" +
                        "            AND first_completed_at > deadline\n" +
                        "           THEN 1 ELSE 0 END) AS users_completed_late\n" +
                        "FROM (\n" +
                        "  SELECT user_id, MAX(course_completion_percentage) AS course_completion_percentage,\n" +
                        "         MIN(first_completed_at) AS first_completed_at,\n" +
                        "         MIN(deadline) AS deadline\n" +
                        "  FROM course_full_progress\n" +
                        "  WHERE course_id = ?\n" +
                        "  GROUP BY user_id\n" +
                        ") x"
        );

        course_kpi_queries.put(
                "Course: Users In Progress & On Time",
                "SELECT \n" +
                        "  SUM(CASE WHEN (course_completion_percentage BETWEEN 0.01 AND 94.99) \n" +
                        "            AND CURRENT_DATE <= deadline\n" +
                        "           THEN 1 ELSE 0 END) AS users_in_progress_on_time\n" +
                        "FROM (\n" +
                        "  SELECT user_id, MAX(course_completion_percentage) AS course_completion_percentage,\n" +
                        "         MIN(deadline) AS deadline\n" +
                        "  FROM course_full_progress\n" +
                        "  WHERE course_id = ?\n" +
                        "  GROUP BY user_id\n" +
                        ") x"
        );

        course_kpi_queries.put(
                "Course: Users In Progress & Late",
                "SELECT \n" +
                        "  SUM(CASE WHEN (course_completion_percentage BETWEEN 0.01 AND 94.99) \n" +
                        "            AND CURRENT_DATE > deadline\n" +
                        "           THEN 1 ELSE 0 END) AS users_in_progress_late\n" +
                        "FROM (\n" +
                        "  SELECT user_id, MAX(course_completion_percentage) AS course_completion_percentage,\n" +
                        "         MIN(deadline) AS deadline\n" +
                        "  FROM course_full_progress\n" +
                        "  WHERE course_id = ?\n" +
                        "  GROUP BY user_id\n" +
                        ") x"
        );

        course_kpi_queries.put(
                "Course: Users Not Started & On Time",
                "SELECT \n" +
                        "  SUM(CASE WHEN (course_completion_percentage = 0 OR course_completion_percentage IS NULL)\n" +
                        "            AND CURRENT_DATE <= deadline\n" +
                        "           THEN 1 ELSE 0 END) AS users_not_started_on_time\n" +
                        "FROM (\n" +
                        "  SELECT user_id, MAX(course_completion_percentage) AS course_completion_percentage,\n" +
                        "         MIN(deadline) AS deadline\n" +
                        "  FROM course_full_progress\n" +
                        "  WHERE course_id = ?\n" +
                        "  GROUP BY user_id\n" +
                        ") x"
        );

        course_kpi_queries.put(
                "Course: Users Not Started & Late",
                "SELECT \n" +
                        "  SUM(CASE WHEN (course_completion_percentage = 0 OR course_completion_percentage IS NULL)\n" +
                        "            AND CURRENT_DATE > deadline\n" +
                        "           THEN 1 ELSE 0 END) AS users_not_started_late\n" +
                        "FROM (\n" +
                        "  SELECT user_id, MAX(course_completion_percentage) AS course_completion_percentage,\n" +
                        "         MIN(deadline) AS deadline\n" +
                        "  FROM course_full_progress\n" +
                        "  WHERE course_id = ?\n" +
                        "  GROUP BY user_id\n" +
                        ") x"
        );

        course_kpi_queries.put(
                "Course Details",
                "SELECT DISTINCT course_id, course_name, course_description, level, is_active\n" +
                        "FROM course_full_progress\n" +
                        "WHERE course_id = ?\n"
        );

        course_kpi_queries.put(
                "Course Content Details",
                "SELECT DISTINCT course_content_id, content_title, content_description, \n" +
                        "       COALESCE(MAX(content_completion_percentage), 0) AS content_completion_percentage\n" +
                        "FROM course_full_progress\n" +
                        "WHERE course_id = ?\n" +
                        "GROUP BY course_content_id, content_title, content_description\n"
        );

        course_kpi_queries.put(
                "Course-User Enrollment Details",
                "SELECT user_enrolled, \n" +
                        "       MAX(course_completion_percentage) AS course_completion_percentage,\n" +
                        "       MIN(deadline) AS deadline,\n" +
                        "       CASE \n" +
                        "         WHEN MAX(course_completion_percentage) >= 95 THEN 'Completed'\n" +
                        "         WHEN MAX(course_completion_percentage) BETWEEN 0.01 AND 94.99 THEN 'In Progress'\n" +
                        "         ELSE 'Not Started'\n" +
                        "       END AS status,\n" +
                        "       CASE \n" +
                        "         WHEN MAX(course_completion_percentage) >= 95 AND MIN(first_completed_at) <= MIN(deadline) THEN 'On Time'\n" +
                        "         WHEN MAX(course_completion_percentage) >= 95 AND MIN(first_completed_at) > MIN(deadline) THEN 'Late'\n" +
                        "         WHEN MAX(course_completion_percentage) BETWEEN 0.01 AND 94.99 AND CURRENT_DATE <= MIN(deadline) THEN 'On Track'\n" +
                        "         WHEN MAX(course_completion_percentage) BETWEEN 0.01 AND 94.99 AND CURRENT_DATE > MIN(deadline) THEN 'Behind Schedule'\n" +
                        "         WHEN (MAX(course_completion_percentage) = 0 OR MAX(course_completion_percentage) IS NULL) AND CURRENT_DATE <= MIN(deadline) THEN 'Not Due Yet'\n" +
                        "         ELSE 'Overdue'\n" +
                        "       END AS adherence\n" +
                        "FROM course_full_progress\n" +
                        "WHERE course_id = ?\n" +
                        "GROUP BY user_enrolled\n"
        );
    }
}
