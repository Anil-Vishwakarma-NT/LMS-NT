-- schema.sql for H2 Integration Tests
-- This file will be automatically executed by H2 database on startup

-- Create role table first (referenced by users table)
CREATE TABLE IF NOT EXISTS role (
    role_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    CONSTRAINT chk_role_name CHECK (name IN ('ADMIN', 'MANAGER', 'EMPLOYEE'))
);

-- Create users table with foreign key references
CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    firstname VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    manager_id BIGINT,
    role_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    -- Foreign key constraints
    CONSTRAINT fk_users_role
        FOREIGN KEY (role_id) REFERENCES role(role_id),
    CONSTRAINT fk_users_manager
        FOREIGN KEY (manager_id) REFERENCES users(user_id)
);

-- Create groups table
CREATE TABLE IF NOT EXISTS groups (
    group_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    group_name VARCHAR(255) NOT NULL,
    creator_id BIGINT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,

    -- Foreign key constraint
    CONSTRAINT fk_groups_creator
        FOREIGN KEY (creator_id) REFERENCES users(user_id)
);

-- Create user_group junction table (many-to-many relationship)
CREATE TABLE IF NOT EXISTS user_group (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    group_id BIGINT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,

    -- Foreign key constraints
    CONSTRAINT fk_user_group_user
        FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_user_group_group
        FOREIGN KEY (group_id) REFERENCES groups(group_id)
);

-- Create refresh token table
CREATE TABLE IF NOT EXISTS refreshtoken (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,

    -- Foreign key constraint
    CONSTRAINT fk_refreshtoken_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Create course table
CREATE TABLE IF NOT EXISTS course (
    course_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    level VARCHAR(50),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign key constraint
    CONSTRAINT fk_course_owner
        FOREIGN KEY (owner_id) REFERENCES users(user_id),

    -- Check constraint for level values
    CONSTRAINT chk_course_level
        CHECK (level IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED'))
);

-- Create course_content table
CREATE TABLE IF NOT EXISTS course_content (
    course_content_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    resource_link VARCHAR(500),
    acknowledgement BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign key constraint
    CONSTRAINT fk_course_content_course
        FOREIGN KEY (course_id) REFERENCES course(course_id)
);

-- Create bundle table
CREATE TABLE IF NOT EXISTS bundle (
    bundle_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bundle_name VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create course_bundle junction table
CREATE TABLE IF NOT EXISTS course_bundle (
    course_bundle_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    bundle_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign key constraints
    CONSTRAINT fk_course_bundle_bundle
        FOREIGN KEY (bundle_id) REFERENCES bundle(bundle_id),
    CONSTRAINT fk_course_bundle_course
        FOREIGN KEY (course_id) REFERENCES course(course_id)
);

-- Create enrollments table
CREATE TABLE IF NOT EXISTS enrollments (
    enrollment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    group_id BIGINT,
    course_id BIGINT,
    bundle_id BIGINT,
    assigned_by BIGINT NOT NULL,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deadline TIMESTAMP,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    enrollment_source VARCHAR(50) NOT NULL,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    -- Foreign key constraints
    CONSTRAINT fk_enrollments_user
        FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_enrollments_group
        FOREIGN KEY (group_id) REFERENCES groups(group_id),
    CONSTRAINT fk_enrollments_course
        FOREIGN KEY (course_id) REFERENCES course(course_id),
    CONSTRAINT fk_enrollments_bundle
        FOREIGN KEY (bundle_id) REFERENCES bundle(bundle_id),
    CONSTRAINT fk_enrollments_assigned_by
        FOREIGN KEY (assigned_by) REFERENCES users(user_id),

    -- Check constraint for enrollment_source values
    CONSTRAINT chk_enrollment_source
        CHECK (enrollment_source IN ('INDIVIDUAL', 'GROUP', 'BUNDLE', 'GROUP_BUNDLE')),

    -- Check constraint for status values
    CONSTRAINT chk_enrollment_status
        CHECK (status IN ('ACTIVE', 'INACTIVE', 'COMPLETED', 'DEADLINE'))
);

-- Create quiz table
CREATE TABLE IF NOT EXISTS quiz (
    quiz_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_type VARCHAR(16) NOT NULL,
    parent_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    time_limit INTEGER,
    attempts_allowed INTEGER NOT NULL DEFAULT 1,
    passing_score DECIMAL(5,2),
    randomize_questions BOOLEAN NOT NULL DEFAULT FALSE,
    show_results BOOLEAN NOT NULL DEFAULT TRUE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_by INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Check constraint for parent_type
    CONSTRAINT chk_quiz_parent_type
        CHECK (parent_type IN ('course', 'content', 'bundle'))
);

-- Create quiz_attempt table
CREATE TABLE IF NOT EXISTS quiz_attempt (
    quiz_attempt_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    attempt BIGINT NOT NULL,
    quiz_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    finished_at TIMESTAMP,
    score_details TEXT,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign key constraints
    CONSTRAINT fk_quiz_attempt_quiz
        FOREIGN KEY (quiz_id) REFERENCES quiz(quiz_id),
    CONSTRAINT fk_quiz_attempt_user
        FOREIGN KEY (user_id) REFERENCES users(user_id),

    -- Check constraint for status
    CONSTRAINT chk_quiz_attempt_status
        CHECK (status IN ('IN_PROGRESS', 'COMPLETED', 'ABANDONED', 'TIMED_OUT'))
);

-- Create quiz_question table
CREATE TABLE IF NOT EXISTS quiz_question (
    question_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_id BIGINT NOT NULL,
    question_text TEXT NOT NULL,
    question_type VARCHAR(20) NOT NULL,
    options TEXT,
    correct_answer TEXT NOT NULL,
    points DECIMAL(5,2) NOT NULL,
    explanation TEXT,
    required BOOLEAN NOT NULL,
    question_position INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign key constraint
    CONSTRAINT fk_quiz_question_quiz
        FOREIGN KEY (quiz_id) REFERENCES quiz(quiz_id),

    -- Check constraint for question_type
    CONSTRAINT chk_quiz_question_type
        CHECK (question_type IN ('MCQ_SINGLE', 'MCQ_MULTIPLE', 'SHORT_ANSWER'))
);

-- Create user_progress table
CREATE TABLE IF NOT EXISTS user_progress (
    progress_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    content_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    content_type VARCHAR(255) NOT NULL,
    last_position DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    content_completion_percentage DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    course_completion_percentage DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    course_completed BOOLEAN NOT NULL DEFAULT FALSE,
    last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    first_completed_at TIMESTAMP,

    -- Foreign key constraints
    CONSTRAINT fk_user_progress_user
        FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_user_progress_content
        FOREIGN KEY (content_id) REFERENCES course_content(course_content_id),
    CONSTRAINT fk_user_progress_course
        FOREIGN KEY (course_id) REFERENCES course(course_id)
);

-- Create user_response table
CREATE TABLE IF NOT EXISTS user_response (
    response_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    quiz_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    attempt BIGINT NOT NULL,
    user_answer TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL,
    points_earned DECIMAL(5,2) NOT NULL,
    answered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign key constraints
    CONSTRAINT fk_user_response_user
        FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_user_response_quiz
        FOREIGN KEY (quiz_id) REFERENCES quiz(quiz_id),
    CONSTRAINT fk_user_response_question
        FOREIGN KEY (question_id) REFERENCES quiz_question(question_id)
);

-- Create quiz_activity_log table
CREATE TABLE IF NOT EXISTS quiz_activity_log (
    log_id INTEGER AUTO_INCREMENT PRIMARY KEY,
    user_id INTEGER NOT NULL,
    quiz_id INTEGER NOT NULL,
    attempt INTEGER NOT NULL,
    action_type VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign key constraints
    CONSTRAINT fk_quiz_activity_log_user
        FOREIGN KEY (user_id) REFERENCES users(user_id),
    CONSTRAINT fk_quiz_activity_log_quiz
        FOREIGN KEY (quiz_id) REFERENCES quiz(quiz_id)
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_role_id ON users(role_id);
CREATE INDEX IF NOT EXISTS idx_users_manager_id ON users(manager_id);
CREATE INDEX IF NOT EXISTS idx_user_group_user_id ON user_group(user_id);
CREATE INDEX IF NOT EXISTS idx_user_group_group_id ON user_group(group_id);
CREATE INDEX IF NOT EXISTS idx_course_owner_id ON course(owner_id);
CREATE INDEX IF NOT EXISTS idx_course_content_course_id ON course_content(course_id);
CREATE INDEX IF NOT EXISTS idx_enrollments_user_id ON enrollments(user_id);
CREATE INDEX IF NOT EXISTS idx_enrollments_course_id ON enrollments(course_id);
CREATE INDEX IF NOT EXISTS idx_quiz_parent_type_id ON quiz(parent_type, parent_id);
CREATE INDEX IF NOT EXISTS idx_quiz_attempt_quiz_id ON quiz_attempt(quiz_id);
CREATE INDEX IF NOT EXISTS idx_quiz_attempt_user_id ON quiz_attempt(user_id);
CREATE INDEX IF NOT EXISTS idx_user_progress_user_id ON user_progress(user_id);
CREATE INDEX IF NOT EXISTS idx_user_progress_course_id ON user_progress(course_id);