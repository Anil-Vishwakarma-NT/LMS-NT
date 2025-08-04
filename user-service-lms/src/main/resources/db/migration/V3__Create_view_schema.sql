-- View: public.bundle_details
-- DROP MATERIALIZED VIEW IF EXISTS public.bundle_details;
CREATE MATERIALIZED VIEW IF NOT EXISTS public.bundle_details
TABLESPACE pg_default
AS
 WITH user_info AS (
         SELECT u.user_id,
            u.username,
            (u.firstname::text || ' '::text) || u.lastname::text AS full_name,
            u.email,
            (mgr.firstname::text || ' '::text) || mgr.lastname::text AS manager,
            r.name AS role,
            u.is_active AS is_user_active
           FROM users u
             LEFT JOIN users mgr ON u.manager_id = mgr.user_id
             LEFT JOIN role r ON u.role_id = r.role_id
        ), enrollments_bundle AS (
         SELECT enrollments.enrollment_id,
            enrollments.user_id,
            enrollments.group_id,
            enrollments.course_id,
            enrollments.bundle_id,
            enrollments.assigned_by,
            enrollments.assigned_at,
            enrollments.deadline,
            enrollments.status,
            enrollments.enrollment_source,
            enrollments.started_at,
            enrollments.completed_at,
            enrollments.created_at,
            enrollments.updated_at,
            enrollments.is_active
           FROM enrollments
          WHERE enrollments.enrollment_source::text = ANY (ARRAY['BUNDLE'::character varying, 'GROUP_BUNDLE'::character varying]::text[])
        ), course_bundle_map AS (
         SELECT cb.bundle_id,
            cb.course_id,
            b.bundle_name,
            b.is_active AS is_bundle_active
           FROM course_bundle cb
             LEFT JOIN bundle b ON cb.bundle_id = b.bundle_id
        ), user_progress_summary AS (
         SELECT DISTINCT user_progress.user_id,
            user_progress.course_id,
            max(user_progress.course_completion_percentage) OVER (PARTITION BY user_progress.user_id, user_progress.course_id) AS course_completion_percentage,
            min(user_progress.first_completed_at) OVER (PARTITION BY user_progress.user_id, user_progress.course_id) AS first_completed_at
           FROM user_progress
        ), total_users_per_bundle_course AS (
         SELECT enrollments.bundle_id,
            enrollments.course_id,
            count(DISTINCT enrollments.user_id) AS total_users_enrolled
           FROM enrollments
          WHERE enrollments.enrollment_source::text = ANY (ARRAY['BUNDLE'::character varying, 'GROUP_BUNDLE'::character varying]::text[])
          GROUP BY enrollments.bundle_id, enrollments.course_id
        ), final_data AS (
         SELECT e.enrollment_source,
            e.group_id,
            u.user_id,
            ui.username,
            ui.full_name,
            ui.email,
            ui.manager,
            ui.role,
            ui.is_user_active,
            e.status AS enrollment_status,
            e.assigned_by,
            e.assigned_at,
            e.deadline,
            e.started_at,
            e.completed_at,
            e.bundle_id,
            e.course_id,
            e.is_active AS is_enrollment_active,
            c.title AS course_title,
            c.description AS course_description,
            c.level AS course_level,
            c.is_active AS is_course_active,
            cbm.bundle_name,
            cbm.is_bundle_active,
            g.group_name,
            COALESCE(up.course_completion_percentage, 0::double precision) AS course_completion_percentage,
            up.first_completed_at
           FROM enrollments_bundle e
             LEFT JOIN users u ON e.user_id = u.user_id
             LEFT JOIN user_info ui ON e.user_id = ui.user_id
             LEFT JOIN course c ON e.course_id = c.course_id
             LEFT JOIN course_bundle_map cbm ON e.bundle_id = cbm.bundle_id AND e.course_id = cbm.course_id
             LEFT JOIN user_progress_summary up ON e.user_id = up.user_id AND e.course_id = up.course_id
             LEFT JOIN groups g ON e.group_id = g.group_id
        )
 SELECT final_data.enrollment_source,
    final_data.group_id,
    final_data.user_id,
    final_data.username,
    final_data.full_name,
    final_data.email,
    final_data.manager,
    final_data.role,
    final_data.is_user_active,
    final_data.enrollment_status,
    final_data.assigned_by,
    final_data.assigned_at,
    final_data.deadline,
    final_data.started_at,
    final_data.completed_at,
    final_data.bundle_id,
    final_data.course_id,
    final_data.is_enrollment_active,
    final_data.course_title,
    final_data.course_description,
    final_data.course_level,
    final_data.is_course_active,
    final_data.bundle_name,
    final_data.is_bundle_active,
    final_data.group_name,
    final_data.course_completion_percentage,
    final_data.first_completed_at,
    tupbc.total_users_enrolled,
        CASE
            WHEN final_data.course_completion_percentage >= 95::double precision THEN 'Completed'::text
            WHEN final_data.course_completion_percentage >= 0.01::double precision AND final_data.course_completion_percentage <= 94.99::double precision THEN 'In Progress'::text
            ELSE 'Not Started'::text
        END AS status,
        CASE
            WHEN final_data.course_completion_percentage >= 95::double precision AND final_data.first_completed_at <= final_data.deadline THEN 'On Time'::text
            WHEN final_data.course_completion_percentage >= 95::double precision AND final_data.first_completed_at > final_data.deadline THEN 'Late'::text
            WHEN final_data.course_completion_percentage >= 0.01::double precision AND final_data.course_completion_percentage <= 94.99::double precision AND CURRENT_DATE <= final_data.deadline THEN 'On Track'::text
            WHEN final_data.course_completion_percentage >= 0.01::double precision AND final_data.course_completion_percentage <= 94.99::double precision AND CURRENT_DATE > final_data.deadline THEN 'Behind Schedule'::text
            WHEN final_data.course_completion_percentage = 0::double precision AND CURRENT_DATE <= final_data.deadline THEN 'Not Due Yet'::text
            ELSE 'Overdue'::text
        END AS adherence
   FROM final_data
     LEFT JOIN total_users_per_bundle_course tupbc ON final_data.bundle_id = tupbc.bundle_id AND final_data.course_id = tupbc.course_id
  ORDER BY final_data.bundle_id, final_data.course_id, final_data.user_id
WITH DATA;
ALTER TABLE IF EXISTS public.bundle_details
    OWNER TO postgres;

-- View: public.course_details
-- DROP MATERIALIZED VIEW IF EXISTS public.course_details;
CREATE MATERIALIZED VIEW IF NOT EXISTS public.course_details
TABLESPACE pg_default
AS
 WITH enrollment_priority AS (
         SELECT enrollments.enrollment_id,
            enrollments.user_id,
            enrollments.group_id,
            enrollments.course_id,
            enrollments.bundle_id,
            enrollments.assigned_by,
            enrollments.assigned_at,
            enrollments.deadline,
            enrollments.status,
            enrollments.enrollment_source,
            enrollments.started_at,
            enrollments.completed_at,
            enrollments.created_at,
            enrollments.updated_at,
            enrollments.is_active,
            row_number() OVER (PARTITION BY enrollments.user_id, enrollments.course_id ORDER BY (
                CASE enrollments.enrollment_source
                    WHEN 'INDIVIDUAL'::text THEN 1
                    WHEN 'BUNDLE'::text THEN 2
                    WHEN 'GROUP'::text THEN 3
                    WHEN 'GROUP_BUNDLE'::text THEN 4
                    ELSE 5
                END), enrollments.assigned_at DESC) AS rn
           FROM enrollments
          WHERE enrollments.course_id IS NOT NULL
        ), best_enrollments AS (
         SELECT enrollment_priority.enrollment_id,
            enrollment_priority.user_id,
            enrollment_priority.group_id,
            enrollment_priority.course_id,
            enrollment_priority.bundle_id,
            enrollment_priority.assigned_by,
            enrollment_priority.assigned_at,
            enrollment_priority.deadline,
            enrollment_priority.status,
            enrollment_priority.enrollment_source,
            enrollment_priority.started_at,
            enrollment_priority.completed_at,
            enrollment_priority.created_at,
            enrollment_priority.updated_at,
            enrollment_priority.is_active,
            enrollment_priority.rn
           FROM enrollment_priority
          WHERE enrollment_priority.rn = 1
        ), course_bundle_map AS (
         SELECT cb.course_id,
            cb.bundle_id,
            b.bundle_name,
            b.is_active AS is_bundle_active
           FROM course_bundle cb
             LEFT JOIN bundle b ON cb.bundle_id = b.bundle_id
        ), user_progress_summary AS (
         SELECT DISTINCT user_progress.user_id,
            user_progress.course_id,
            max(user_progress.course_completion_percentage) OVER (PARTITION BY user_progress.user_id, user_progress.course_id) AS course_completion_percentage,
            min(user_progress.first_completed_at) OVER (PARTITION BY user_progress.user_id, user_progress.course_id) AS first_completed_at
           FROM user_progress
        ), user_info AS (
         SELECT u.user_id,
            u.username,
            (u.firstname::text || ' '::text) || u.lastname::text AS full_name,
            u.email,
            (mgr.firstname::text || ' '::text) || mgr.lastname::text AS manager,
            r.name AS role,
            u.is_active AS is_user_active
           FROM users u
             LEFT JOIN users mgr ON u.manager_id = mgr.user_id
             LEFT JOIN role r ON u.role_id = r.role_id
        ), final_data AS (
         SELECT be.course_id,
            c.title AS course_title,
            c.description AS course_description,
            c.level AS course_level,
            c.is_active AS is_course_active,
            u.user_id,
            u.username,
            u.full_name,
            u.email,
            u.manager,
            u.role,
            u.is_user_active,
            be.enrollment_source,
            be.status AS enrollment_status,
            be.assigned_by,
            be.assigned_at,
            be.deadline,
            be.started_at,
            be.completed_at,
            be.bundle_id,
            cbm.bundle_name,
            cbm.is_bundle_active,
            be.group_id,
            g.group_name,
            be.is_active AS is_enrollment_active,
            COALESCE(ups.course_completion_percentage, 0::double precision) AS course_completion_percentage,
            ups.first_completed_at
           FROM best_enrollments be
             LEFT JOIN course c ON be.course_id = c.course_id
             LEFT JOIN user_info u ON be.user_id = u.user_id
             LEFT JOIN course_bundle_map cbm ON be.bundle_id = cbm.bundle_id AND be.course_id = cbm.course_id
             LEFT JOIN user_progress_summary ups ON be.user_id = ups.user_id AND be.course_id = ups.course_id
             LEFT JOIN groups g ON be.group_id = g.group_id
        )
 SELECT course_id,
    course_title,
    course_description,
    course_level,
    is_course_active,
    user_id,
    username,
    full_name,
    email,
    manager,
    role,
    is_user_active,
    enrollment_source,
    enrollment_status,
    assigned_by,
    assigned_at,
    deadline,
    started_at,
    completed_at,
    bundle_id,
    bundle_name,
    is_bundle_active,
    group_id,
    group_name,
    is_enrollment_active,
    course_completion_percentage,
    first_completed_at,
        CASE
            WHEN course_completion_percentage >= 95::double precision THEN 'Completed'::text
            WHEN course_completion_percentage >= 0.01::double precision AND course_completion_percentage <= 94.99::double precision THEN 'In Progress'::text
            ELSE 'Not Started'::text
        END AS status,
        CASE
            WHEN course_completion_percentage >= 95::double precision AND first_completed_at <= deadline THEN 'On Time'::text
            WHEN course_completion_percentage >= 95::double precision AND first_completed_at > deadline THEN 'Late'::text
            WHEN course_completion_percentage >= 0.01::double precision AND course_completion_percentage <= 94.99::double precision AND CURRENT_DATE <= deadline THEN 'On Track'::text
            WHEN course_completion_percentage >= 0.01::double precision AND course_completion_percentage <= 94.99::double precision AND CURRENT_DATE > deadline THEN 'Behind Schedule'::text
            WHEN course_completion_percentage = 0::double precision AND CURRENT_DATE <= deadline THEN 'Not Due Yet'::text
            ELSE 'Overdue'::text
        END AS adherence
   FROM final_data
  ORDER BY course_id, user_id
WITH DATA;
ALTER TABLE IF EXISTS public.course_details
    OWNER TO postgres;

-- View: public.group_details
-- DROP MATERIALIZED VIEW IF EXISTS public.group_details;
CREATE MATERIALIZED VIEW IF NOT EXISTS public.group_details
TABLESPACE pg_default
AS
 WITH group_enrollments AS (
         SELECT enrollments.enrollment_id,
            enrollments.user_id,
            enrollments.group_id,
            enrollments.course_id,
            enrollments.bundle_id,
            enrollments.assigned_by,
            enrollments.assigned_at,
            enrollments.deadline,
            enrollments.status,
            enrollments.enrollment_source,
            enrollments.started_at,
            enrollments.completed_at,
            enrollments.created_at,
            enrollments.updated_at,
            enrollments.is_active
           FROM enrollments
          WHERE enrollments.enrollment_source::text = ANY (ARRAY['GROUP'::character varying, 'GROUP_BUNDLE'::character varying]::text[])
        ), course_bundle_map AS (
         SELECT cb.course_id,
            cb.bundle_id,
            b.bundle_name,
            b.is_active AS is_bundle_active
           FROM course_bundle cb
             LEFT JOIN bundle b ON cb.bundle_id = b.bundle_id
        ), user_progress_summary AS (
         SELECT DISTINCT user_progress.user_id,
            user_progress.course_id,
            max(user_progress.course_completion_percentage) OVER (PARTITION BY user_progress.user_id, user_progress.course_id) AS course_completion_percentage,
            min(user_progress.first_completed_at) OVER (PARTITION BY user_progress.user_id, user_progress.course_id) AS first_completed_at
           FROM user_progress
        ), final_data AS (
         SELECT g.group_id,
            g.group_name,
            cbm.bundle_id,
            cbm.bundle_name,
            cbm.is_bundle_active,
            c.course_id,
            c.title AS course_title,
            c.description AS course_description,
            c.level AS course_level,
            c.is_active AS is_course_active,
            u.user_id,
            u.username,
            (u.firstname::text || ' '::text) || u.lastname::text AS full_name,
            u.email,
            (mgr.firstname::text || ' '::text) || mgr.lastname::text AS manager,
            r.name AS role,
            u.is_active AS is_user_active,
            e.enrollment_source,
            e.status AS enrollment_status,
            e.assigned_by,
            e.assigned_at,
            e.deadline,
            e.started_at,
            e.completed_at,
            e.is_active AS is_enrollment_active,
            COALESCE(up.course_completion_percentage, 0::double precision) AS course_completion_percentage,
            up.first_completed_at
           FROM groups g
             LEFT JOIN group_enrollments e ON g.group_id = e.group_id
             LEFT JOIN course c ON e.course_id = c.course_id
             LEFT JOIN course_bundle_map cbm ON c.course_id = cbm.course_id AND e.bundle_id = cbm.bundle_id
             LEFT JOIN users u ON e.user_id = u.user_id
             LEFT JOIN users mgr ON u.manager_id = mgr.user_id
             LEFT JOIN role r ON u.role_id = r.role_id
             LEFT JOIN user_progress_summary up ON e.user_id = up.user_id AND e.course_id = up.course_id
        )
 SELECT row_number() OVER (ORDER BY group_id, bundle_id, course_id, user_id) AS id,
    group_id,
    group_name,
    bundle_id,
    bundle_name,
    is_bundle_active,
    course_id,
    course_title,
    course_description,
    course_level,
    is_course_active,
    user_id,
    username,
    full_name,
    email,
    manager,
    role,
    is_user_active,
    enrollment_source,
    enrollment_status,
    assigned_by,
    assigned_at,
    deadline,
    started_at,
    completed_at,
    is_enrollment_active,
    course_completion_percentage,
    first_completed_at,
        CASE
            WHEN course_completion_percentage >= 95::double precision THEN 'Completed'::text
            WHEN course_completion_percentage >= 0.01::double precision AND course_completion_percentage <= 94.99::double precision THEN 'In Progress'::text
            ELSE 'Not Started'::text
        END AS status,
        CASE
            WHEN course_completion_percentage >= 95::double precision AND first_completed_at <= deadline THEN 'On Time'::text
            WHEN course_completion_percentage >= 95::double precision AND first_completed_at > deadline THEN 'Late'::text
            WHEN course_completion_percentage >= 0.01::double precision AND course_completion_percentage <= 94.99::double precision AND CURRENT_DATE <= deadline THEN 'On Track'::text
            WHEN course_completion_percentage >= 0.01::double precision AND course_completion_percentage <= 94.99::double precision AND CURRENT_DATE > deadline THEN 'Behind Schedule'::text
            WHEN course_completion_percentage = 0::double precision AND CURRENT_DATE <= deadline THEN 'Not Due Yet'::text
            ELSE 'Overdue'::text
        END AS adherence
   FROM final_data
  ORDER BY group_id, bundle_id, course_id, user_id
WITH DATA;
ALTER TABLE IF EXISTS public.group_details
    OWNER TO postgres;

-- View: public.user_details
-- DROP MATERIALIZED VIEW IF EXISTS public.user_details;
CREATE MATERIALIZED VIEW IF NOT EXISTS public.user_details
TABLESPACE pg_default
AS
 WITH user_info AS (
         SELECT u.user_id,
            u.username,
            (u.firstname::text || ' '::text) || u.lastname::text AS full_name,
            u.email,
            (mgr.firstname::text || ' '::text) || mgr.lastname::text AS manager,
            r.name AS role,
            u.is_active AS is_user_active
           FROM users u
             LEFT JOIN users mgr ON u.manager_id = mgr.user_id
             LEFT JOIN role r ON u.role_id = r.role_id
        ), group_info AS (
         SELECT groups.group_id,
            groups.group_name
           FROM groups
        ), enrollments_user AS (
         SELECT enrollments.enrollment_id,
            enrollments.user_id,
            enrollments.group_id,
            enrollments.course_id,
            enrollments.bundle_id,
            enrollments.assigned_by,
            enrollments.assigned_at,
            enrollments.deadline,
            enrollments.status,
            enrollments.enrollment_source,
            enrollments.started_at,
            enrollments.completed_at,
            enrollments.created_at,
            enrollments.updated_at,
            enrollments.is_active
           FROM enrollments
          WHERE enrollments.enrollment_source::text = ANY (ARRAY['INDIVIDUAL'::character varying, 'BUNDLE'::character varying, 'GROUP'::character varying, 'GROUP_BUNDLE'::character varying]::text[])
        ), course_bundle_map AS (
         SELECT cb.bundle_id,
            cb.course_id,
            b.bundle_name,
            b.is_active AS is_bundle_active
           FROM course_bundle cb
             LEFT JOIN bundle b ON cb.bundle_id = b.bundle_id
        ), user_progress_summary AS (
         SELECT DISTINCT user_progress.user_id,
            user_progress.course_id,
            max(user_progress.course_completion_percentage) OVER (PARTITION BY user_progress.user_id, user_progress.course_id) AS course_completion_percentage,
            min(user_progress.first_completed_at) OVER (PARTITION BY user_progress.user_id, user_progress.course_id) AS first_completed_at
           FROM user_progress
        ), final_data AS (
         SELECT ui.user_id,
            ui.username,
            ui.full_name,
            ui.email,
            ui.manager,
            ui.role,
            ui.is_user_active,
            e.group_id,
            gi.group_name,
            e.enrollment_source,
            e.status AS enrollment_status,
            e.assigned_by,
            e.assigned_at,
            e.deadline,
            e.started_at,
            e.completed_at,
            e.bundle_id,
            e.course_id,
            e.is_active AS is_enrollment_active,
            c.title AS course_title,
            c.description AS course_description,
            c.level AS course_level,
            c.is_active AS is_course_active,
            cbm.bundle_name,
            cbm.is_bundle_active,
            COALESCE(up.course_completion_percentage, 0::double precision) AS course_completion_percentage,
            up.first_completed_at
           FROM user_info ui
             LEFT JOIN enrollments_user e ON ui.user_id = e.user_id
             LEFT JOIN course c ON e.course_id = c.course_id
             LEFT JOIN course_bundle_map cbm ON e.bundle_id = cbm.bundle_id AND e.course_id = cbm.course_id
             LEFT JOIN user_progress_summary up ON e.user_id = up.user_id AND e.course_id = up.course_id
             LEFT JOIN group_info gi ON e.group_id = gi.group_id
        )
 SELECT user_id,
    username,
    full_name,
    email,
    manager,
    role,
    is_user_active,
    group_id,
    group_name,
    enrollment_source,
    enrollment_status,
    assigned_by,
    assigned_at,
    deadline,
    started_at,
    completed_at,
    bundle_id,
    course_id,
    is_enrollment_active,
    course_title,
    course_description,
    course_level,
    is_course_active,
    bundle_name,
    is_bundle_active,
    course_completion_percentage,
    first_completed_at,
        CASE
            WHEN course_completion_percentage >= 95::double precision THEN 'Completed'::text
            WHEN course_completion_percentage >= 0.01::double precision AND course_completion_percentage <= 94.99::double precision THEN 'In Progress'::text
            ELSE 'Not Started'::text
        END AS status,
        CASE
            WHEN course_completion_percentage >= 95::double precision AND first_completed_at <= deadline THEN 'On Time'::text
            WHEN course_completion_percentage >= 95::double precision AND first_completed_at > deadline THEN 'Late'::text
            WHEN course_completion_percentage >= 0.01::double precision AND course_completion_percentage <= 94.99::double precision AND CURRENT_DATE <= deadline THEN 'On Track'::text
            WHEN course_completion_percentage >= 0.01::double precision AND course_completion_percentage <= 94.99::double precision AND CURRENT_DATE > deadline THEN 'Behind Schedule'::text
            WHEN course_completion_percentage = 0::double precision AND CURRENT_DATE <= deadline THEN 'Not Due Yet'::text
            ELSE 'Overdue'::text
        END AS adherence
   FROM final_data
  ORDER BY user_id, bundle_id, course_id
WITH DATA;
ALTER TABLE IF EXISTS public.user_details
    OWNER TO postgres;

-- View: public.user_course_report_view
-- DROP MATERIALIZED VIEW IF EXISTS public.user_course_report_view;
CREATE MATERIALIZED VIEW IF NOT EXISTS public.user_course_report_view
TABLESPACE pg_default
AS
 SELECT u.user_id,
    u.username,
    u.email,
    r.name AS role,
    ug.group_id,
    g.group_name,
    e.enrollment_id,
    e.enrollment_source,
        CASE
            WHEN e.bundle_id IS NOT NULL THEN 'BUNDLE'::text
            ELSE 'COURSE'::text
        END AS source_type,
    c.course_id,
    c.title AS course_title,
    e.bundle_id,
    b.bundle_name,
    e.assigned_by,
    e.assigned_at,
    e.deadline,
    e.started_at,
    e.completed_at,
    max(up.course_completion_percentage) AS course_completion_percentage,
        CASE
            WHEN max(up.course_completion_percentage) = 100::double precision THEN 'Completed'::text
            WHEN max(up.course_completion_percentage) > 0::double precision THEN 'In Progress'::text
            ELSE 'Not Started'::text
        END AS status,
        CASE
            WHEN max(up.course_completion_percentage) = 100::double precision AND e.completed_at <= e.deadline THEN 'On Time'::text
            WHEN max(up.course_completion_percentage) = 100::double precision AND e.completed_at > e.deadline THEN 'Late'::text
            WHEN max(up.course_completion_percentage) < 100::double precision AND e.deadline > now() THEN 'On Track'::text
            WHEN max(up.course_completion_percentage) < 100::double precision AND e.deadline <= now() THEN 'Behind Schedule'::text
            WHEN e.started_at IS NULL THEN 'Not Due Yet'::text
            ELSE 'Overdue'::text
        END AS adherence,
    up.content_id,
    cc.title AS content_title,
    cc.resource_link,
    cc.description AS content_description,
    cc.acknowledgement,
    up.content_type,
    up.last_position,
    up.content_completion_percentage,
    up.first_completed_at,
    up.last_updated
   FROM enrollments e
     JOIN users u ON e.user_id = u.user_id
     JOIN role r ON u.role_id = r.role_id
     LEFT JOIN bundle b ON e.bundle_id = b.bundle_id
     JOIN course c ON e.course_id = c.course_id
     LEFT JOIN course_content cc ON c.course_id = cc.course_id
     LEFT JOIN user_progress up ON up.user_id = u.user_id AND up.course_id = c.course_id AND up.content_id = cc.course_content_id
     LEFT JOIN user_group ug ON u.user_id = ug.user_id AND ug.is_active = true
     LEFT JOIN groups g ON ug.group_id = g.group_id
  WHERE e.is_active = true
  GROUP BY u.user_id, u.username, u.email, r.name, ug.group_id, g.group_name, e.enrollment_id, e.enrollment_source, e.bundle_id, c.course_id, c.title, b.bundle_name, e.assigned_by, e.assigned_at, e.deadline, e.started_at, e.completed_at, up.content_id, cc.title, cc.resource_link, cc.description, cc.acknowledgement, up.content_type, up.last_position, up.content_completion_percentage, up.first_completed_at, up.last_updated
WITH DATA;
ALTER TABLE IF EXISTS public.user_course_report_view
    OWNER TO postgres;