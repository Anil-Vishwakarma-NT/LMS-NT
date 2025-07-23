package com.nt.user_service_lms.service.serviceImpl;

import java.util.HashMap;
import java.util.Map;

public class UserReportKPIQueries {

    public static final String base_query =
            "WITH user_basic_details AS ( " +
                    "SELECT user_id, firstname || ' ' || lastname AS name, email, role_id, is_active FROM users ), " +

                    "user_complete_details AS ( " +
                    "SELECT ubd.user_id, ubd.name, ubd.email, r.name AS role, ubd.is_active " +
                    "FROM user_basic_details ubd LEFT JOIN role r ON ubd.role_id = r.role_id ), " +

                    "user_enrollment_details AS ( " +
                    "SELECT DISTINCT ON (user_id, course_id) user_id, course_id, assigned_by, assigned_at, deadline " +
                    "FROM enrollments ORDER BY user_id, course_id, assigned_at ASC ), " +

                    "user_progress_details AS ( " +
                    "SELECT ued.user_id, ued.course_id, c.title AS course_name, " +
                    "u.firstname || ' ' || u.lastname AS assigned_by_name, ued.assigned_at, ued.deadline, " +
                    "up.course_completion_percentage, up.first_completed_at " +
                    "FROM user_enrollment_details ued " +
                    "LEFT JOIN user_progress up ON ued.user_id = up.user_id AND ued.course_id = up.course_id " +
                    "LEFT JOIN users u ON u.user_id = ued.assigned_by " +
                    "LEFT JOIN course c ON ued.course_id = c.course_id ), " +

                    "final_user_report AS ( " +
                    "SELECT ucd.user_id, ucd.name, ucd.email, ucd.role, ucd.is_active, upd.course_id AS enrolled_course_id, " +
                    "upd.course_name AS enrolled_course, upd.assigned_by_name AS assigni, upd.assigned_at, upd.deadline, " +
                    "upd.course_completion_percentage, upd.first_completed_at " +
                    "FROM user_complete_details ucd LEFT JOIN user_progress_details upd ON ucd.user_id = upd.user_id )";


    public static final Map<String, String> user_kpi_queries = new HashMap<>();

    static {
        user_kpi_queries.put(
                "Global: Total Users",
                "SELECT COUNT(DISTINCT user_id) AS total_users\n" +
                        "FROM final_user_report"
        );

        user_kpi_queries.put(
                "Global: Total Enrolled Users",
                "SELECT COUNT(DISTINCT user_id) AS total_enrolled_users\n" +
                        "FROM final_user_report\n" +
                        "WHERE enrolled_course_id IS NOT NULL"
        );

        user_kpi_queries.put(
                "Global: Total Non-Enrolled Users",
                "SELECT COUNT(DISTINCT user_id) AS total_non_enrolled_users\n" +
                        "FROM final_user_report\n" +
                        "WHERE user_id NOT IN (\n" +
                        "  SELECT DISTINCT user_id\n" +
                        "  FROM final_user_report\n" +
                        "  WHERE enrolled_course_id IS NOT NULL\n" +
                        ")"
        );

        user_kpi_queries.put(
                "Global: Total Active Users",
                "SELECT COUNT(DISTINCT user_id) AS active_users\n" +
                        "FROM final_user_report\n" +
                        "WHERE is_active = true"
        );

        user_kpi_queries.put(
                "Global: Total Inactive Users",
                "SELECT COUNT(DISTINCT user_id) AS inactive_users\n" +
                        "FROM final_user_report\n" +
                        "WHERE is_active = false"
        );

        user_kpi_queries.put(
                "Global: Total Employees",
                "SELECT COUNT(DISTINCT user_id) AS employee_count\n" +
                        "FROM final_user_report\n" +
                        "WHERE role = 'employee'"
        );

        user_kpi_queries.put(
                "Global: Total Managers",
                "SELECT COUNT(DISTINCT user_id) AS manager_count\n" +
                        "FROM final_user_report\n" +
                        "WHERE role = 'manager'"
        );

        user_kpi_queries.put(
                "Global: Total User-Course Enrollments",
                "SELECT COUNT(*) AS total_course_enrollments\n" +
                        "FROM (\n" +
                        "  SELECT DISTINCT user_id, enrolled_course_id\n" +
                        "  FROM final_user_report\n" +
                        "  WHERE enrolled_course_id IS NOT NULL\n" +
                        ") sub;"
        );

        user_kpi_queries.put(
                "Global: Average Completion %",
                "SELECT \n" +
                        "  ROUND(AVG(COALESCE(course_completion_percentage, 0))::NUMERIC, 2) AS avg_completion\n" +
                        "FROM (\n" +
                        "  SELECT \n" +
                        "    user_id, \n" +
                        "    enrolled_course_id, \n" +
                        "    MAX(COALESCE(course_completion_percentage, 0)) AS course_completion_percentage\n" +
                        "  FROM final_user_report\n" +
                        "  WHERE enrolled_course_id IS NOT NULL\n" +
                        "  GROUP BY user_id, enrolled_course_id\n" +
                        ") AS deduped"
        );

        user_kpi_queries.put(
                "Global: Highest Completion %",
                "SELECT highest_course_completion_percentage\n" +
                        "FROM (\n" +
                        "  SELECT \n" +
                        "    user_id, \n" +
                        "    enrolled_course, \n" +
                        "    MAX(COALESCE(course_completion_percentage, 0)) AS highest_course_completion_percentage\n" +
                        "  FROM final_user_report\n" +
                        "  WHERE enrolled_course_id IS NOT NULL\n" +
                        "  GROUP BY user_id, enrolled_course\n" +
                        ") AS deduped\n" +
                        "ORDER BY highest_course_completion_percentage DESC\n" +
                        "LIMIT 1"
        );

        user_kpi_queries.put(
                "Global: Lowest Completion %",
                "SELECT lowest_course_completion_percentage\n" +
                        "FROM (\n" +
                        "  SELECT \n" +
                        "    user_id,\n" +
                        "    enrolled_course,\n" +
                        "    MAX(COALESCE(course_completion_percentage, 0)) AS lowest_course_completion_percentage\n" +
                        "  FROM final_user_report\n" +
                        "  WHERE enrolled_course_id IS NOT NULL\n" +
                        "  GROUP BY user_id, enrolled_course\n" +
                        ") AS deduped\n" +
                        "ORDER BY lowest_course_completion_percentage ASC\n" +
                        "LIMIT 1"
        );

        user_kpi_queries.put(
                "Global: Total Enrollments Completed",
                "SELECT COUNT(*) AS total_completed\n" +
                        "FROM (\n" +
                        "  SELECT \n" +
                        "    user_id,\n" +
                        "    enrolled_course_id,\n" +
                        "    MAX(COALESCE(course_completion_percentage, 0)) AS course_completion_percentage\n" +
                        "  FROM final_user_report\n" +
                        "  WHERE enrolled_course_id IS NOT NULL\n" +
                        "  GROUP BY user_id, enrolled_course_id\n" +
                        ") AS deduped\n" +
                        "WHERE course_completion_percentage >= 95"
        );

        user_kpi_queries.put(
                "Global: Total Enrollments in Progress",
                "SELECT COUNT(*) AS total_in_progress\n" +
                        "FROM (\n" +
                        "  SELECT \n" +
                        "    user_id,\n" +
                        "    enrolled_course_id,\n" +
                        "    MAX(COALESCE(course_completion_percentage, 0)) AS course_completion_percentage\n" +
                        "  FROM final_user_report\n" +
                        "  WHERE enrolled_course_id IS NOT NULL\n" +
                        "  GROUP BY user_id, enrolled_course_id\n" +
                        ") AS deduped\n" +
                        "WHERE course_completion_percentage > 0 AND course_completion_percentage < 95"
        );

        user_kpi_queries.put(
                "Global: Total Enrollments Not Started",
                "SELECT COUNT(*) AS total_not_started\n" +
                        "FROM (\n" +
                        "  SELECT \n" +
                        "    user_id,\n" +
                        "    enrolled_course_id,\n" +
                        "    MAX(COALESCE(course_completion_percentage, 0)) AS course_completion_percentage\n" +
                        "  FROM final_user_report\n" +
                        "  WHERE enrolled_course_id IS NOT NULL\n" +
                        "  GROUP BY user_id, enrolled_course_id\n" +
                        ") AS deduped\n" +
                        "WHERE course_completion_percentage = 0"
        );

        user_kpi_queries.put(
                "Global: Enrollments Completed On Time",
                "SELECT COUNT(*) AS completed_on_time\n" +
                        "FROM (\n" +
                        "  SELECT \n" +
                        "    user_id,\n" +
                        "    enrolled_course_id,\n" +
                        "    MAX(COALESCE(course_completion_percentage, 0)) AS course_completion_percentage,\n" +
                        "    MAX(first_completed_at) AS first_completed_at,\n" +
                        "    MAX(deadline) AS deadline\n" +
                        "  FROM final_user_report\n" +
                        "  WHERE enrolled_course_id IS NOT NULL\n" +
                        "  GROUP BY user_id, enrolled_course_id\n" +
                        ") AS deduped\n" +
                        "WHERE course_completion_percentage >= 95\n" +
                        "  AND (first_completed_at <= deadline OR deadline IS NULL)"
        );

        user_kpi_queries.put(
                "Global: Enrollments Completed Late",
                "SELECT COUNT(*) AS completed_late\n" +
                        "FROM (\n" +
                        "  SELECT \n" +
                        "    user_id,\n" +
                        "    enrolled_course_id,\n" +
                        "    MAX(COALESCE(course_completion_percentage, 0)) AS course_completion_percentage,\n" +
                        "    MAX(first_completed_at) AS first_completed_at,\n" +
                        "    MAX(deadline) AS deadline\n" +
                        "  FROM final_user_report\n" +
                        "  WHERE enrolled_course_id IS NOT NULL\n" +
                        "  GROUP BY user_id, enrolled_course_id\n" +
                        ") AS deduped\n" +
                        "WHERE course_completion_percentage >= 95\n" +
                        "  AND first_completed_at > deadline"
        );

        user_kpi_queries.put(
                "Global: Enrollments on Track",
                "SELECT COUNT(*) AS in_progress_on_time\n" +
                        "FROM (\n" +
                        "  SELECT \n" +
                        "    user_id,\n" +
                        "    enrolled_course_id,\n" +
                        "    MAX(COALESCE(course_completion_percentage, 0)) AS course_completion_percentage,\n" +
                        "    MAX(deadline) AS deadline\n" +
                        "  FROM final_user_report\n" +
                        "  WHERE enrolled_course_id IS NOT NULL\n" +
                        "  GROUP BY user_id, enrolled_course_id\n" +
                        ") AS deduped\n" +
                        "WHERE course_completion_percentage > 0\n" +
                        "  AND course_completion_percentage < 95\n" +
                        "  AND (deadline IS NULL OR CURRENT_DATE <= deadline)"
        );

        user_kpi_queries.put(
                "Global: Enrollments Not on Track",
                "SELECT COUNT(*) AS in_progress_late\n" +
                        "FROM (\n" +
                        "  SELECT \n" +
                        "    user_id,\n" +
                        "    enrolled_course_id,\n" +
                        "    MAX(COALESCE(course_completion_percentage, 0)) AS course_completion_percentage,\n" +
                        "    MAX(deadline) AS deadline\n" +
                        "  FROM final_user_report\n" +
                        "  WHERE enrolled_course_id IS NOT NULL\n" +
                        "  GROUP BY user_id, enrolled_course_id\n" +
                        ") AS deduped\n" +
                        "WHERE course_completion_percentage > 0\n" +
                        "  AND course_completion_percentage < 95\n" +
                        "  AND deadline IS NOT NULL\n" +
                        "  AND CURRENT_DATE > deadline"
        );

        user_kpi_queries.put(
                "Global: Enrollments Yet to be Started (On Time)",
                "SELECT COUNT(*) AS not_started_on_time\n" +
                        "FROM (\n" +
                        "  SELECT \n" +
                        "    user_id,\n" +
                        "    enrolled_course_id,\n" +
                        "    MAX(COALESCE(course_completion_percentage, 0)) AS course_completion_percentage,\n" +
                        "    MAX(deadline) AS deadline\n" +
                        "  FROM final_user_report\n" +
                        "  WHERE enrolled_course_id IS NOT NULL\n" +
                        "  GROUP BY user_id, enrolled_course_id\n" +
                        ") AS deduped\n" +
                        "WHERE course_completion_percentage = 0\n" +
                        "  AND deadline IS NOT NULL\n" +
                        "  AND CURRENT_DATE <= deadline"
        );

        user_kpi_queries.put(
                "Global: Enrollments Yet to be Started (Deadline Missed)",
                "SELECT COUNT(*) AS not_started_late\n" +
                        "FROM (\n" +
                        "  SELECT \n" +
                        "    user_id,\n" +
                        "    enrolled_course_id,\n" +
                        "    MAX(COALESCE(course_completion_percentage, 0)) AS course_completion_percentage,\n" +
                        "    MAX(deadline) AS deadline\n" +
                        "  FROM final_user_report\n" +
                        "  WHERE enrolled_course_id IS NOT NULL\n" +
                        "  GROUP BY user_id, enrolled_course_id\n" +
                        ") AS deduped\n" +
                        "WHERE course_completion_percentage = 0\n" +
                        "  AND deadline IS NOT NULL\n" +
                        "  AND CURRENT_DATE > deadline"
        );

        //per user
        user_kpi_queries.put(
                "User: Total Enrolled Courses",
                "SELECT \n" +
                        "  COUNT(DISTINCT enrolled_course_id) AS total_courses_enrolled\n" +
                        "FROM final_user_report\n" +
                        "WHERE user_id = ?"
        );

        user_kpi_queries.put(
                "User: Average Completion %",
                "SELECT \n" +
                        "  ROUND(AVG(course_completion)::numeric, 2) AS avg_completion\n" +
                        "FROM (\n" +
                        "  SELECT \n" +
                        "    user_id, \n" +
                        "    enrolled_course_id, \n" +
                        "    MAX(COALESCE(course_completion_percentage, 0)) AS course_completion\n" +
                        "  FROM final_user_report\n" +
                        "  WHERE user_id = ?\n" +
                        "    AND enrolled_course_id IS NOT NULL\n" +
                        "  GROUP BY user_id, enrolled_course_id\n" +
                        ") sub"
        );

        user_kpi_queries.put(
                "User: Highest Completion %",
                "SELECT highest_course_completion\n" +
                        "FROM (\n" +
                        "  SELECT \n" +
                        "    enrolled_course,\n" +
                        "    MAX(COALESCE(course_completion_percentage, 0)) AS highest_course_completion\n" +
                        "  FROM final_user_report\n" +
                        "  WHERE user_id = ?\n" +
                        "    AND enrolled_course_id IS NOT NULL\n" +
                        "  GROUP BY enrolled_course_id, enrolled_course\n" +
                        ") x\n" +
                        "ORDER BY highest_course_completion DESC\n" +
                        "LIMIT 1"
        );

        user_kpi_queries.put(
                "User: Lowest Completion %",
                "SELECT lowest_course_completion\n" +
                        "FROM (\n" +
                        "  SELECT \n" +
                        "    enrolled_course,\n" +
                        "    MAX(COALESCE(course_completion_percentage, 0)) AS lowest_course_completion\n" +
                        "  FROM final_user_report\n" +
                        "  WHERE user_id = ?\n" +
                        "    AND enrolled_course_id IS NOT NULL\n" +
                        "  GROUP BY enrolled_course_id, enrolled_course\n" +
                        ") x\n" +
                        "ORDER BY lowest_course_completion ASC\n" +
                        "LIMIT 1"
        );

        user_kpi_queries.put(
                "User: Courses Completed",
                "SELECT\n" +
                        "    COUNT(*) AS completed\n" +
                        "FROM (\n" +
                        "    SELECT DISTINCT user_id, enrolled_course_id\n" +
                        "    FROM final_user_report\n" +
                        "    WHERE user_id = ?\n" +
                        "      AND COALESCE(course_completion_percentage, 0) >= 95\n" +
                        "      AND enrolled_course_id IS NOT NULL\n" +
                        ") sub"
        );

        user_kpi_queries.put(
                "User: Courses in Progress",
                "SELECT\n" +
                        "    COUNT(*) AS in_progress\n" +
                        "FROM (\n" +
                        "    SELECT DISTINCT user_id, enrolled_course_id\n" +
                        "    FROM final_user_report\n" +
                        "    WHERE user_id = ?\n" +
                        "      AND COALESCE(course_completion_percentage, 0) > 0\n" +
                        "      AND COALESCE(course_completion_percentage, 0) < 95\n" +
                        "      AND enrolled_course_id IS NOT NULL\n" +
                        ") sub"
        );

        user_kpi_queries.put(
                "User: Courses Not Started",
                "SELECT\n" +
                        "    COUNT(*) AS not_started\n" +
                        "FROM (\n" +
                        "    SELECT DISTINCT user_id, enrolled_course_id\n" +
                        "    FROM final_user_report\n" +
                        "    WHERE user_id = ?\n" +
                        "      AND COALESCE(course_completion_percentage, 0) = 0\n" +
                        "      AND enrolled_course_id IS NOT NULL\n" +
                        ") sub"
        );

        user_kpi_queries.put(
                "User: Courses Completed on Time",
                "SELECT\n" +
                        "    COUNT(*) AS completed_on_time\n" +
                        "FROM (\n" +
                        "    SELECT DISTINCT user_id, enrolled_course_id\n" +
                        "    FROM final_user_report\n" +
                        "    WHERE user_id = ?\n" +
                        "      AND COALESCE(course_completion_percentage, 0) >= 95\n" +
                        "      AND (\n" +
                        "        first_completed_at <= deadline\n" +
                        "        OR deadline IS NULL\n" +
                        "      )\n" +
                        "      AND enrolled_course_id IS NOT NULL\n" +
                        ") sub"
        );

        user_kpi_queries.put(
                "User: Courses Completed Late",
                "SELECT\n" +
                        "    COUNT(*) AS completed_late\n" +
                        "FROM (\n" +
                        "    SELECT DISTINCT user_id, enrolled_course_id\n" +
                        "    FROM final_user_report\n" +
                        "    WHERE user_id = ?\n" +
                        "      AND COALESCE(course_completion_percentage, 0) >= 95\n" +
                        "      AND first_completed_at > deadline\n" +
                        "      AND enrolled_course_id IS NOT NULL\n" +
                        ") sub"
        );

        user_kpi_queries.put(
                "User: Courses on Track",
                "SELECT\n" +
                        "    COUNT(*) AS in_progress_on_time\n" +
                        "FROM (\n" +
                        "    SELECT DISTINCT user_id, enrolled_course_id\n" +
                        "    FROM final_user_report\n" +
                        "    WHERE user_id = ?\n" +
                        "      AND COALESCE(course_completion_percentage, 0) BETWEEN 0.01 AND 94.99\n" +
                        "      AND (deadline IS NULL OR CURRENT_DATE <= deadline)\n" +
                        "      AND enrolled_course_id IS NOT NULL\n" +
                        ") sub"
        );

        user_kpi_queries.put(
                "User: Courses Not on Track",
                "\n" +
                        "SELECT\n" +
                        "    COUNT(*) AS in_progress_late\n" +
                        "FROM (\n" +
                        "    SELECT DISTINCT user_id, enrolled_course_id\n" +
                        "    FROM final_user_report\n" +
                        "    WHERE user_id = ?\n" +
                        "      AND COALESCE(course_completion_percentage, 0) BETWEEN 0.01 AND 94.99\n" +
                        "      AND deadline IS NOT NULL\n" +
                        "      AND CURRENT_DATE > deadline\n" +
                        "      AND enrolled_course_id IS NOT NULL\n" +
                        ") sub"
        );

        user_kpi_queries.put(
                "User: Courses Yet to be Started (On Time)",
                "SELECT\n" +
                        "    COUNT(*) AS not_started_on_time\n" +
                        "FROM (\n" +
                        "    SELECT DISTINCT user_id, enrolled_course_id\n" +
                        "    FROM final_user_report\n" +
                        "    WHERE user_id = ?\n" +
                        "      AND COALESCE(course_completion_percentage, 0) = 0\n" +
                        "      AND deadline IS NOT NULL\n" +
                        "      AND CURRENT_DATE <= deadline\n" +
                        "      AND enrolled_course_id IS NOT NULL\n" +
                        ") sub"
        );

        user_kpi_queries.put(
                "User: Courses Yet to be Started (Deadline Missed)",
                "SELECT\n" +
                        "    COUNT(*) AS not_started_late\n" +
                        "FROM (\n" +
                        "    SELECT DISTINCT user_id, enrolled_course_id\n" +
                        "    FROM final_user_report\n" +
                        "    WHERE user_id = ?\n" +
                        "      AND COALESCE(course_completion_percentage, 0) = 0\n" +
                        "      AND deadline IS NOT NULL\n" +
                        "      AND CURRENT_DATE > deadline\n" +
                        "      AND enrolled_course_id IS NOT NULL\n" +
                        ") sub;\n"
        );

        user_kpi_queries.put(
                "User Details",
                "SELECT \n" +
                        "\tdistinct user_id,\n" +
                        "  name,\n" +
                        "  email,\n" +
                        "  role,\n" +
                        "  is_active\n" +
                        "FROM final_user_report\n" +
                        "WHERE user_id = ?"
        );

        user_kpi_queries.put(
                "Enrollment Details",
                "SELECT \n" +
                        "  enrolled_course AS course_name,\n" +
                        "  assigni AS assigned_by,\n" +
                        "  MIN(assigned_at) AS assigned_at,\n" +
                        "  MIN(deadline) AS deadline\n" +
                        "FROM final_user_report\n" +
                        "WHERE user_id = ? AND enrolled_course_id IS NOT NULL\n" +
                        "GROUP BY enrolled_course_id, enrolled_course, assigni"
        );

        user_kpi_queries.put(
                "Progress Details",
                "SELECT \n" +
                        "  enrolled_course AS course_name,\n" +
                        "  COALESCE(MAX(course_completion_percentage), 0) AS course_completion_percentage,\n" +
                        "  CASE \n" +
                        "    WHEN COALESCE(MAX(course_completion_percentage), 0) >= 95 THEN 'Completed'\n" +
                        "    WHEN COALESCE(MAX(course_completion_percentage), 0) BETWEEN 0.01 AND 94.99 THEN 'In Progress'\n" +
                        "    ELSE 'Not Started'\n" +
                        "  END AS status,\n" +
                        "  CASE \n" +
                        "    WHEN COALESCE(MAX(course_completion_percentage), 0) >= 95 AND MIN(first_completed_at) <= MIN(deadline) THEN 'On Time'\n" +
                        "    WHEN COALESCE(MAX(course_completion_percentage), 0) >= 95 AND MIN(first_completed_at) > MIN(deadline) THEN 'Late'\n" +
                        "    WHEN COALESCE(MAX(course_completion_percentage), 0) BETWEEN 0.01 AND 94.99 AND CURRENT_DATE <= MIN(deadline) THEN 'On Track'\n" +
                        "    WHEN COALESCE(MAX(course_completion_percentage), 0) BETWEEN 0.01 AND 94.99 AND CURRENT_DATE > MIN(deadline) THEN 'Behind Schedule'\n" +
                        "    WHEN (COALESCE(MAX(course_completion_percentage), 0) = 0) AND CURRENT_DATE <= MIN(deadline) THEN 'Not Due Yet'\n" +
                        "    ELSE 'Overdue'\n" +
                        "  END AS adherence\n" +
                        "FROM final_user_report\n" +
                        "WHERE user_id = ? AND enrolled_course_id IS NOT NULL\n" +
                        "GROUP BY enrolled_course_id, enrolled_course"
        );
    }
}
