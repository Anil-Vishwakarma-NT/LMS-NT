-- Insert dummy data for all tables

-- 1. Insert roles first (referenced by users)
INSERT INTO role (name) VALUES
('ADMIN'),
('MANAGER'),
('EMPLOYEE');

-- 2. Insert users (some without managers first, then with managers)
INSERT INTO users (username, firstname, lastname, email, password, manager_id, role_id) VALUES
-- Admin users (no managers)
('admin1', 'John', 'Administrator', 'admin1@nucleusteq.com', '$2a$12$CFhTnEUT095lprJwk9c7pek10m.XTrKYpxIE19lHRIkp2OkVwZIkO', 1, 1),
('admin2', 'Sarah', 'Admin', 'admin2@nucleusteq.com', '$2a$12$CFhTnEUT095lprJwk9c7pek10m.XTrKYpxIE19lHRIkp2OkVwZIkO', 1, 1),

-- Manager users (reporting to admin)
('manager1', 'Michael', 'Johnson', 'manager1@nucleusteq.com', '$2a$12$CFhTnEUT095lprJwk9c7pek10m.XTrKYpxIE19lHRIkp2OkVwZIkO', 1, 2),
('manager2', 'Emily', 'Davis', 'manager2@nucleusteq.com', '$2a$12$CFhTnEUT095lprJwk9c7pek10m.XTrKYpxIE19lHRIkp2OkVwZIkO', 1, 2),
('manager3', 'Robert', 'Wilson', 'manager3@nucleusteq.com', '$2a$12$CFhTnEUT095lprJwk9c7pek10m.XTrKYpxIE19lHRIkp2OkVwZIkO', 2, 2),

-- Employee users (reporting to managers)
('employee1', 'Alice', 'Smith', 'alice.smith@nucleusteq.com', '$2a$12$CFhTnEUT095lprJwk9c7pek10m.XTrKYpxIE19lHRIkp2OkVwZIkO', 3, 3),
('employee2', 'Bob', 'Brown', 'bob.brown@nucleusteq.com', '$2a$12$CFhTnEUT095lprJwk9c7pek10m.XTrKYpxIE19lHRIkp2OkVwZIkO', 3, 3),
('employee3', 'Charlie', 'Garcia', 'charlie.garcia@nucleusteq.com', '$2a$12$CFhTnEUT095lprJwk9c7pek10m.XTrKYpxIE19lHRIkp2OkVwZIkO', 4, 3),
('employee4', 'Diana', 'Miller', 'diana.miller@nucleusteq.com', '$2a$12$CFhTnEUT095lprJwk9c7pek10m.XTrKYpxIE19lHRIkp2OkVwZIkO', 4, 3),
('employee5', 'Edward', 'Anderson', 'edward.anderson@nucleusteq.com', '$2a$12$CFhTnEUT095lprJwk9c7pek10m.XTrKYpxIE19lHRIkp2OkVwZIkO', 5, 3),
('employee6', 'Fiona', 'Taylor', 'fiona.taylor@nucleusteq.com', '$2a$12$CFhTnEUT095lprJwk9c7pek10m.XTrKYpxIE19lHRIkp2OkVwZIkO', 5, 3),
('employee7', 'George', 'Thomas', 'george.thomas@nucleusteq.com', '$2a$12$CFhTnEUT095lprJwk9c7pek10m.XTrKYpxIE19lHRIkp2OkVwZIkO', 3, 3),
('employee8', 'Helen', 'Jackson', 'helen.jackson@nucleusteq.com', '$2a$12$CFhTnEUT095lprJwk9c7pek10m.XTrKYpxIE19lHRIkp2OkVwZIkO', 4, 3);

-- 3. Insert groups
INSERT INTO groups (group_name, creator_id) VALUES
('Sales Team', 3),
('Marketing Team', 4),
('Development Team', 5),
('HR Team', 3),
('Support Team', 4),
('Training Group Alpha', 1),
('Training Group Beta', 2);

-- 4. Insert user_group relationships
INSERT INTO user_group (user_id, group_id) VALUES
-- Sales Team
(6, 1), (7, 1), (12, 1),
-- Marketing Team
(8, 2), (9, 2), (13, 2),
-- Development Team
(10, 3), (11, 3), (6, 3),
-- HR Team
(7, 4), (8, 4),
-- Support Team
(9, 5), (10, 5), (11, 5),
-- Training Groups
(6, 6), (7, 6), (8, 6), (9, 6),
(10, 7), (11, 7), (12, 7), (13, 7);

---- 5. Insert refresh tokens
--INSERT INTO refreshtoken (user_id, token, expiry_date) VALUES
--(6, 'refresh_token_alice_123456', '2025-08-07 10:00:00'),
--(7, 'refresh_token_bob_789012', '2025-08-07 11:00:00'),
--(8, 'refresh_token_charlie_345678', '2025-08-07 12:00:00'),
--(9, 'refresh_token_diana_901234', '2025-08-07 13:00:00'),
--(10, 'refresh_token_edward_567890', '2025-08-07 14:00:00');

-- 6. Insert courses
INSERT INTO course (owner_id, title, description, level) VALUES
(1, 'Introduction to Data Science', 'Learn the basics of data science including statistics, Python, and machine learning fundamentals', 'BEGINNER'),
(2, 'Advanced Web Development', 'Master modern web development with React, Node.js, and cloud deployment', 'ADVANCED'),
(3, 'Project Management Fundamentals', 'Essential project management skills for team leads and managers', 'INTERMEDIATE'),
(4, 'Digital Marketing Strategies', 'Comprehensive guide to digital marketing including SEO, social media, and analytics', 'BEGINNER'),
(5, 'Database Design and Optimization', 'Learn to design efficient databases and optimize query performance', 'INTERMEDIATE'),
(1, 'Leadership and Communication', 'Develop leadership skills and improve team communication', 'INTERMEDIATE'),
(2, 'Cybersecurity Essentials', 'Foundation course in cybersecurity principles and best practices', 'BEGINNER'),
(3, 'Financial Analysis for Managers', 'Understanding financial statements and making data-driven decisions', 'ADVANCED');

-- 7. Insert course content
INSERT INTO course_content (course_id, title, description, resource_link) VALUES
-- Data Science Course Content
(1, 'Introduction to Statistics', 'Basic statistical concepts and terminology', 'https://example.com/stats-intro'),
(1, 'Python for Data Science', 'Python programming fundamentals for data analysis', 'https://example.com/python-basics'),
(1, 'Data Visualization', 'Creating charts and graphs to represent data', 'https://example.com/data-viz'),
(1, 'Machine Learning Basics', 'Introduction to supervised and unsupervised learning', 'https://example.com/ml-basics'),

-- Web Development Course Content
(2, 'React Fundamentals', 'Building user interfaces with React', 'https://example.com/react-basics'),
(2, 'Node.js Backend Development', 'Creating APIs with Node.js and Express', 'https://example.com/nodejs-api'),
(2, 'Database Integration', 'Connecting your application to databases', 'https://example.com/db-integration'),
(2, 'Cloud Deployment', 'Deploying applications to cloud platforms', 'https://example.com/cloud-deploy'),

-- Project Management Course Content
(3, 'Project Planning', 'Creating effective project plans and timelines', 'https://example.com/project-planning'),
(3, 'Team Management', 'Leading and motivating project teams', 'https://example.com/team-management'),
(3, 'Risk Management', 'Identifying and mitigating project risks', 'https://example.com/risk-management'),

-- Digital Marketing Course Content
(4, 'SEO Fundamentals', 'Search engine optimization basics', 'https://example.com/seo-basics'),
(4, 'Social Media Marketing', 'Leveraging social platforms for marketing', 'https://example.com/social-media'),
(4, 'Analytics and Reporting', 'Measuring marketing campaign effectiveness', 'https://example.com/analytics'),

-- Database Design Course Content
(5, 'Database Normalization', 'Designing efficient database schemas', 'https://example.com/db-normalization'),
(5, 'Query Optimization', 'Improving database query performance', 'https://example.com/query-optimization'),
(5, 'Index Strategies', 'Using indexes effectively', 'https://example.com/index-strategies'),

-- Leadership Course Content
(6, 'Communication Skills', 'Effective communication techniques', 'https://example.com/communication'),
(6, 'Team Building', 'Building cohesive teams', 'https://example.com/team-building'),
(6, 'Conflict Resolution', 'Managing and resolving conflicts', 'https://example.com/conflict-resolution'),

-- Cybersecurity Course Content
(7, 'Security Fundamentals', 'Basic cybersecurity concepts', 'https://example.com/security-basics'),
(7, 'Threat Assessment', 'Identifying and evaluating threats', 'https://example.com/threat-assessment'),
(7, 'Incident Response', 'Responding to security incidents', 'https://example.com/incident-response'),

-- Financial Analysis Course Content
(8, 'Financial Statement Analysis', 'Reading and interpreting financial statements', 'https://example.com/financial-statements'),
(8, 'Budgeting and Forecasting', 'Creating budgets and financial forecasts', 'https://example.com/budgeting'),
(8, 'Investment Analysis', 'Evaluating investment opportunities', 'https://example.com/investment-analysis');

-- 8. Insert bundles
INSERT INTO bundle (bundle_name) VALUES
('Complete Developer Bundle'),
('Management Excellence Bundle'),
('Digital Skills Bundle'),
('Leadership Development Bundle'),
('Technical Fundamentals Bundle');

-- 9. Insert course_bundle relationships
INSERT INTO course_bundle (bundle_id, course_id) VALUES
-- Complete Developer Bundle
(1, 1), (1, 2), (1, 5), (1, 7),
-- Management Excellence Bundle
(2, 3), (2, 6), (2, 8),
-- Digital Skills Bundle
(3, 1), (3, 4), (3, 7),
-- Leadership Development Bundle
(4, 3), (4, 6),
-- Technical Fundamentals Bundle
(5, 1), (5, 5), (5, 7);

-- 10. Insert enrollments
-- 10. Insert enrollments (Updated version)
INSERT INTO enrollments (user_id, group_id, course_id, bundle_id, assigned_by, deadline, status, enrollment_source, started_at, completed_at) VALUES

-- Individual course enrollments (user_id NOT NULL, group_id NULL, bundle_id NULL)
(6, NULL, 1, NULL, 3, '2025-08-15 23:59:59', 'ACTIVE', 'INDIVIDUAL', '2025-07-01 09:00:00', NULL),
(7, NULL, 2, NULL, 3, '2025-08-20 23:59:59', 'ACTIVE', 'INDIVIDUAL', '2025-07-02 10:00:00', NULL),
(8, NULL, 3, NULL, 4, '2025-08-25 23:59:59', 'COMPLETED', 'INDIVIDUAL', '2025-06-15 09:00:00', '2025-07-05 16:30:00'),
(9, NULL, 4, NULL, 4, '2025-08-30 23:59:59', 'ACTIVE', 'INDIVIDUAL', '2025-07-03 11:00:00', NULL),
(10, NULL, 5, NULL, 5, '2025-09-01 23:59:59', 'ACTIVE', 'INDIVIDUAL', '2025-07-04 14:00:00', NULL),

-- Group enrollments (one row per user in group for each course)
-- Sales Team (group_id=1) enrolled in Data Science course (course_id=1)
-- Users in Sales Team: 6, 7, 12
(6, 1, 1, NULL, 3, '2025-08-31 23:59:59', 'ACTIVE', 'GROUP', NULL, NULL),
(7, 1, 1, NULL, 3, '2025-08-31 23:59:59', 'ACTIVE', 'GROUP', NULL, NULL),
(12, 1, 1, NULL, 3, '2025-08-31 23:59:59', 'ACTIVE', 'GROUP', NULL, NULL),

-- Marketing Team (group_id=2) enrolled in Digital Marketing course (course_id=4)
-- Users in Marketing Team: 8, 9, 13
(8, 2, 4, NULL, 4, '2025-09-15 23:59:59', 'ACTIVE', 'GROUP', NULL, NULL),
(9, 2, 4, NULL, 4, '2025-09-15 23:59:59', 'ACTIVE', 'GROUP', NULL, NULL),
(13, 2, 4, NULL, 4, '2025-09-15 23:59:59', 'ACTIVE', 'GROUP', NULL, NULL),

-- Development Team (group_id=3) enrolled in Advanced Web Development course (course_id=2)
-- Users in Development Team: 10, 11, 6
(10, 3, 2, NULL, 5, '2025-09-30 23:59:59', 'ACTIVE', 'GROUP', NULL, NULL),
(11, 3, 2, NULL, 5, '2025-09-30 23:59:59', 'ACTIVE', 'GROUP', NULL, NULL),
(6, 3, 2, NULL, 5, '2025-09-30 23:59:59', 'ACTIVE', 'GROUP', NULL, NULL),

-- Bundle enrollments (one row per course in bundle for each user)
-- User 11 enrolled in Complete Developer Bundle (bundle_id=1)
-- Complete Developer Bundle contains courses: 1, 2, 5, 7
(11, NULL, 1, 1, 1, '2025-10-01 23:59:59', 'ACTIVE', 'BUNDLE', '2025-07-05 09:00:00', NULL),
(11, NULL, 2, 1, 1, '2025-10-01 23:59:59', 'ACTIVE', 'BUNDLE', '2025-07-05 09:00:00', NULL),
(11, NULL, 5, 1, 1, '2025-10-01 23:59:59', 'ACTIVE', 'BUNDLE', '2025-07-05 09:00:00', NULL),
(11, NULL, 7, 1, 1, '2025-10-01 23:59:59', 'ACTIVE', 'BUNDLE', '2025-07-05 09:00:00', NULL),

-- User 12 enrolled in Management Excellence Bundle (bundle_id=2)
-- Management Excellence Bundle contains courses: 3, 6, 8
(12, NULL, 3, 2, 2, '2025-09-15 23:59:59', 'ACTIVE', 'BUNDLE', '2025-07-06 10:00:00', NULL),
(12, NULL, 6, 2, 2, '2025-09-15 23:59:59', 'ACTIVE', 'BUNDLE', '2025-07-06 10:00:00', NULL),
(12, NULL, 8, 2, 2, '2025-09-15 23:59:59', 'ACTIVE', 'BUNDLE', '2025-07-06 10:00:00', NULL),

-- User 13 enrolled in Digital Skills Bundle (bundle_id=3)
-- Digital Skills Bundle contains courses: 1, 4, 7
(13, NULL, 1, 3, 1, '2025-10-31 23:59:59', 'ACTIVE', 'BUNDLE', '2025-07-07 11:00:00', NULL),
(13, NULL, 4, 3, 1, '2025-10-31 23:59:59', 'ACTIVE', 'BUNDLE', '2025-07-07 11:00:00', NULL),
(13, NULL, 7, 3, 1, '2025-10-31 23:59:59', 'ACTIVE', 'BUNDLE', '2025-07-07 11:00:00', NULL),

-- Group bundle enrollments (one row per user per course in bundle)
-- Training Group Alpha (group_id=6) enrolled in Leadership Development Bundle (bundle_id=4)
-- Training Group Alpha users: 6, 7, 8, 9
-- Leadership Development Bundle contains courses: 3, 6
(6, 6, 3, 4, 1, '2025-11-30 23:59:59', 'ACTIVE', 'GROUP_BUNDLE', NULL, NULL),
(6, 6, 6, 4, 1, '2025-11-30 23:59:59', 'ACTIVE', 'GROUP_BUNDLE', NULL, NULL),
(7, 6, 3, 4, 1, '2025-11-30 23:59:59', 'ACTIVE', 'GROUP_BUNDLE', NULL, NULL),
(7, 6, 6, 4, 1, '2025-11-30 23:59:59', 'ACTIVE', 'GROUP_BUNDLE', NULL, NULL),
(8, 6, 3, 4, 1, '2025-11-30 23:59:59', 'ACTIVE', 'GROUP_BUNDLE', NULL, NULL),
(8, 6, 6, 4, 1, '2025-11-30 23:59:59', 'ACTIVE', 'GROUP_BUNDLE', NULL, NULL),
(9, 6, 3, 4, 1, '2025-11-30 23:59:59', 'ACTIVE', 'GROUP_BUNDLE', NULL, NULL),
(9, 6, 6, 4, 1, '2025-11-30 23:59:59', 'ACTIVE', 'GROUP_BUNDLE', NULL, NULL),

-- Training Group Beta (group_id=7) enrolled in Technical Fundamentals Bundle (bundle_id=5)
-- Training Group Beta users: 10, 11, 12, 13
-- Technical Fundamentals Bundle contains courses: 1, 5, 7
(10, 7, 1, 5, 2, '2025-12-15 23:59:59', 'ACTIVE', 'GROUP_BUNDLE', NULL, NULL),
(10, 7, 5, 5, 2, '2025-12-15 23:59:59', 'ACTIVE', 'GROUP_BUNDLE', NULL, NULL),
(10, 7, 7, 5, 2, '2025-12-15 23:59:59', 'ACTIVE', 'GROUP_BUNDLE', NULL, NULL),
(11, 7, 1, 5, 2, '2025-12-15 23:59:59', 'ACTIVE', 'GROUP_BUNDLE', NULL, NULL),
(11, 7, 5, 5, 2, '2025-12-15 23:59:59', 'ACTIVE', 'GROUP_BUNDLE', NULL, NULL),
(11, 7, 7, 5, 2, '2025-12-15 23:59:59', 'ACTIVE', 'GROUP_BUNDLE', NULL, NULL),
(12, 7, 1, 5, 2, '2025-12-15 23:59:59', 'ACTIVE', 'GROUP_BUNDLE', NULL, NULL),
(12, 7, 5, 5, 2, '2025-12-15 23:59:59', 'ACTIVE', 'GROUP_BUNDLE', NULL, NULL),
(12, 7, 7, 5, 2, '2025-12-15 23:59:59', 'ACTIVE', 'GROUP_BUNDLE', NULL, NULL),
(13, 7, 1, 5, 2, '2025-12-15 23:59:59', 'ACTIVE', 'GROUP_BUNDLE', NULL, NULL),
(13, 7, 5, 5, 2, '2025-12-15 23:59:59', 'ACTIVE', 'GROUP_BUNDLE', NULL, NULL),
(13, 7, 7, 5, 2, '2025-12-15 23:59:59', 'ACTIVE', 'GROUP_BUNDLE', NULL, NULL);

-- 11. Insert quizzes
INSERT INTO quiz (parent_type, parent_id, title, description, time_limit, attempts_allowed, passing_score, randomize_questions, show_results, created_by) VALUES
-- Course quizzes
('course', 1, 'Data Science Fundamentals Quiz', 'Test your knowledge of basic data science concepts', 30, 3, 70.00, true, true, 1),
('course', 2, 'Web Development Assessment', 'Comprehensive test of web development skills', 45, 2, 75.00, true, true, 2),
('course', 3, 'Project Management Quiz', 'Evaluate your project management knowledge', 25, 3, 65.00, false, true, 3),
('course', 4, 'Digital Marketing Test', 'Assessment of digital marketing strategies', 35, 2, 70.00, true, true, 4),

-- Content quizzes
('content', 1, 'Statistics Quick Check', 'Quick quiz on statistical concepts', 10, 5, 60.00, false, true, 1),
('content', 5, 'React Basics Quiz', 'Test your React fundamentals', 15, 3, 70.00, true, true, 2),
('content', 9, 'Project Planning Assessment', 'Quiz on project planning techniques', 20, 2, 65.00, false, true, 3),

-- Bundle quizzes
('bundle', 1, 'Developer Bundle Final Exam', 'Comprehensive exam covering all developer topics', 90, 1, 80.00, true, true, 1),
('bundle', 2, 'Management Excellence Assessment', 'Final assessment for management bundle', 60, 2, 75.00, false, true, 2);

-- 12. Insert quiz questions
INSERT INTO quiz_question (quiz_id, question_text, question_type, options, correct_answer, points, explanation, required, question_position) VALUES
-- Data Science Quiz Questions
(1, 'What is the primary purpose of data visualization?', 'MCQ_SINGLE', '["To make data look pretty", "To communicate insights from data", "To hide data complexity", "To increase file size"]', 'To communicate insights from data', 10.00, 'Data visualization helps communicate insights and patterns in data effectively', true, 1),
(1, 'Which of the following are types of machine learning?', 'MCQ_MULTIPLE', '["Supervised learning", "Unsupervised learning", "Reinforcement learning", "Cognitive learning"]', '["Supervised learning", "Unsupervised learning", "Reinforcement learning"]', 15.00, 'These are the three main types of machine learning approaches', true, 2),
(1, 'What does the term "correlation" mean in statistics?', 'SHORT_ANSWER', '[]', 'A statistical measure that describes the relationship between two variables', 10.00, 'Correlation measures the strength and direction of a linear relationship between variables', true, 3),

-- Web Development Quiz Questions
(2, 'What is the Virtual DOM in React?', 'MCQ_SINGLE', '["A database", "A JavaScript representation of the real DOM", "A CSS framework", "A server technology"]', 'A JavaScript representation of the real DOM', 12.00, 'Virtual DOM is a programming concept where UI is kept in memory and synced with the real DOM', true, 1),
(2, 'Which HTTP methods are commonly used in REST APIs?', 'MCQ_MULTIPLE', '["GET", "POST", "PUT", "DELETE", "PATCH"]', '["GET", "POST", "PUT", "DELETE"]', 15.00, 'These are the primary HTTP methods used in RESTful services', true, 2),
(2, 'What is the purpose of middleware in Express.js?', 'SHORT_ANSWER', '[]', 'Functions that execute during the request-response cycle', 10.00, 'Middleware functions have access to request and response objects and can modify them', true, 3),

-- Project Management Quiz Questions
(3, 'What is the critical path in project management?', 'MCQ_SINGLE', '["The shortest route", "The longest sequence of dependent tasks", "The most expensive tasks", "The least important tasks"]', 'The longest sequence of dependent tasks', 15.00, 'Critical path determines the minimum time needed to complete a project', true, 1),
(3, 'Which are key components of risk management?', 'MCQ_MULTIPLE', '["Risk identification", "Risk assessment", "Risk mitigation", "Risk celebration"]', '["Risk identification", "Risk assessment", "Risk mitigation"]', 12.00, 'These are the main phases of the risk management process', true, 2),

-- Digital Marketing Quiz Questions
(4, 'What does SEO stand for?', 'SHORT_ANSWER', '[]', 'Search Engine Optimization', 8.00, 'SEO is the practice of optimizing websites to rank higher in search engine results', true, 1),
(4, 'Which metrics are important for measuring social media success?', 'MCQ_MULTIPLE', '["Engagement rate", "Reach", "Conversion rate", "Number of employees"]', '["Engagement rate", "Reach", "Conversion rate"]', 12.00, 'These metrics help measure the effectiveness of social media campaigns', true, 2),

-- Statistics Quick Check Questions
(5, 'What is the mean of the dataset: 2, 4, 6, 8, 10?', 'SHORT_ANSWER', '[]', '6', 5.00, 'Mean = (2+4+6+8+10)/5 = 30/5 = 6', true, 1),
(5, 'What type of data is "customer satisfaction rating (1-5)"?', 'MCQ_SINGLE', '["Nominal", "Ordinal", "Interval", "Ratio"]', 'Ordinal', 5.00, 'Ordinal data has a natural order but the intervals between values are not necessarily equal', true, 2);

-- 13. Insert quiz attempts
INSERT INTO quiz_attempt (attempt, quiz_id, user_id, started_at, finished_at, score_details, status) VALUES
(1, 1, 6, '2025-07-01 14:00:00', '2025-07-01 14:25:00', '{"total_points": 35, "earned_points": 28, "percentage": 80.0}', 'COMPLETED'),
(1, 2, 7, '2025-07-02 15:00:00', '2025-07-02 15:35:00', '{"total_points": 37, "earned_points": 30, "percentage": 81.1}', 'COMPLETED'),
(1, 3, 8, '2025-07-03 10:00:00', '2025-07-03 10:20:00', '{"total_points": 27, "earned_points": 25, "percentage": 92.6}', 'COMPLETED'),
(2, 1, 6, '2025-07-05 16:00:00', '2025-07-05 16:28:00', '{"total_points": 35, "earned_points": 32, "percentage": 91.4}', 'COMPLETED'),
(1, 5, 9, '2025-07-04 13:00:00', '2025-07-04 13:08:00', '{"total_points": 10, "earned_points": 8, "percentage": 80.0}', 'COMPLETED'),
(1, 6, 10, '2025-07-06 11:00:00', NULL, '{"total_points": 0, "earned_points": 0, "percentage": 0.0}', 'IN_PROGRESS');

-- 14. Insert user responses
INSERT INTO user_response (user_id, quiz_id, question_id, attempt, user_answer, is_correct, points_earned) VALUES
-- Alice's first attempt at Data Science quiz
(6, 1, 1, 1, '"To communicate insights from data"', true, 10.00),
(6, 1, 2, 1, '["Supervised learning", "Unsupervised learning"]', false, 8.00),
(6, 1, 3, 1, '"A measure of relationship between variables"', true, 10.00),

-- Alice's second attempt at Data Science quiz
(6, 1, 1, 2, '"To communicate insights from data"', true, 10.00),
(6, 1, 2, 2, '["Supervised learning", "Unsupervised learning", "Reinforcement learning"]', true, 15.00),
(6, 1, 3, 2, '"A statistical measure that describes the relationship between two variables"', true, 10.00),

-- Bob's Web Development quiz responses
(7, 2, 4, 1, '"A JavaScript representation of the real DOM"', true, 12.00),
(7, 2, 5, 1, '["GET", "POST", "PUT", "DELETE"]', true, 15.00),
(7, 2, 6, 1, '"Functions that execute during request-response cycle"', true, 10.00),

-- Charlie's Project Management quiz responses
(8, 3, 7, 1, '"The longest sequence of dependent tasks"', true, 15.00),
(8, 3, 8, 1, '["Risk identification", "Risk assessment", "Risk mitigation"]', true, 12.00),

-- Diana's Statistics quiz responses
(9, 5, 9, 1, '"6"', true, 5.00),
(9, 5, 10, 1, '"Ordinal"', true, 5.00);

-- 15. Insert user progress
INSERT INTO user_progress (user_id, content_id, course_id, content_type, last_position, content_completion_percentage, course_completion_percentage, course_completed, first_completed_at) VALUES
-- Alice's progress
(6, 1, 1, 'video', 0.85, 100.0, 75.0, false, '2025-07-01 10:30:00'),
(6, 2, 1, 'video', 0.65, 65.0, 75.0, false, NULL),
(6, 3, 1, 'video', 0.90, 90.0, 75.0, false, NULL),
(6, 4, 1, 'video', 0.0, 0.0, 75.0, false, NULL),

-- Bob's progress
(7, 5, 2, 'video', 1.0, 100.0, 50.0, false, '2025-07-02 14:20:00'),
(7, 6, 2, 'video', 0.75, 75.0, 50.0, false, NULL),
(7, 7, 2, 'video', 0.0, 0.0, 50.0, false, NULL),
(7, 8, 2, 'video', 0.0, 0.0, 50.0, false, NULL),

-- Charlie's progress (completed course)
(8, 9, 3, 'video', 1.0, 100.0, 100.0, true, '2025-06-20 16:45:00'),
(8, 10, 3, 'video', 1.0, 100.0, 100.0, true, '2025-06-25 15:30:00'),
(8, 11, 3, 'video', 1.0, 100.0, 100.0, true, '2025-07-01 11:15:00'),

-- Diana's progress
(9, 12, 4, 'video', 0.45, 45.0, 30.0, false, NULL),
(9, 13, 4, 'video', 0.80, 80.0, 30.0, false, NULL),
(9, 14, 4, 'video', 0.0, 0.0, 30.0, false, NULL),

-- Edward's progress
(10, 15, 5, 'video', 1.0, 100.0, 67.0, false, '2025-07-04 16:00:00'),
(10, 16, 5, 'video', 0.55, 55.0, 67.0, false, NULL),
(10, 17, 5, 'video', 0.0, 0.0, 67.0, false, NULL);

-- 16. Insert quiz activity log
INSERT INTO quiz_activity_log (user_id, quiz_id, attempt, action_type) VALUES
(6, 1, 1, 'QUIZ_STARTED'),
(6, 1, 1, 'QUESTION_ANSWERED'),
(6, 1, 1, 'QUESTION_ANSWERED'),
(6, 1, 1, 'QUESTION_ANSWERED'),
(6, 1, 1, 'QUIZ_COMPLETED'),
(6, 1, 2, 'QUIZ_STARTED'),
(6, 1, 2, 'QUESTION_ANSWERED'),
(6, 1, 2, 'QUESTION_ANSWERED'),
(6, 1, 2, 'QUESTION_ANSWERED'),
(6, 1, 2, 'QUIZ_COMPLETED'),
(7, 2, 1, 'QUIZ_STARTED'),
(7, 2, 1, 'QUESTION_ANSWERED'),
(7, 2, 1, 'QUESTION_ANSWERED'),
(7, 2, 1, 'QUESTION_ANSWERED'),
(7, 2, 1, 'QUIZ_COMPLETED'),
(8, 3, 1, 'QUIZ_STARTED'),
(8, 3, 1, 'QUESTION_ANSWERED'),
(8, 3, 1, 'QUESTION_ANSWERED'),
(8, 3, 1, 'QUIZ_COMPLETED'),
(9, 5, 1, 'QUIZ_STARTED'),
(9, 5, 1, 'QUESTION_ANSWERED'),
(9, 5, 1, 'QUESTION_ANSWERED'),
(9, 5, 1, 'QUIZ_COMPLETED'),
(10, 6, 1, 'QUIZ_STARTED'),
(10, 6, 1, 'QUESTION_ANSWERED');