package com.nt.user_service_lms.repository;


import com.nt.user_service_lms.dto.outDTO.AdminDashboardStatsOutDTO;
import com.nt.user_service_lms.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address.
     *
     * @param email The email address of the user.
     * @return An Optional containing the user if found, or empty if no user with the provided email exists.
     */
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * Finds a user by their username, ignoring case.
     *
     * @param userName The username of the user.
     * @return An Optional containing the user if found, or empty if no user with the provided username exists.
     */
    Optional<User> findByUserNameIgnoreCase(String userName);

    /**
     * Finds a user by their user ID.
     *
     * @param userId The ID of the user.
     * @return An Optional containing the user if found, or empty if no user with the provided ID exists.
     */
    Optional<User> findById(long userId);

    /**
     * Finds all users managed by a particular manager.
     *
     * @param managerId The ID of the manager.
     * @return A list of users managed by the specified manager.
     */
    List<User> findByManagerId(long managerId);

    /**
     * Checks if a user exists by their ID.
     *
     * @param id The ID of the user.
     * @return true if a user with the given ID exists, false otherwise.
     */
    boolean existsById(long id);

    /**
     * Fetches details of the 5 most recently created active users (excluding the user with ID 1).
     * The details include full name, email, role, manager's name, and creation date.
     *
     * @return A list of object arrays containing user details.
     */
    @Query(value = """
            SELECT
                CONCAT(u.firstname, ' ', u.lastname) AS fullName,
                u.email AS email,
                r.name AS role,
                CONCAT(m.firstname, ' ', m.lastname) AS managerName,
                u.created_at AS createdAt
            FROM users u
            LEFT JOIN role r ON u.role_id = r.role_id
            LEFT JOIN users m ON u.manager_id = m.user_id
            WHERE u.is_active = true AND u.user_id <> 1
            ORDER BY u.created_at DESC
            LIMIT 5
            """, nativeQuery = true)
    List<Object[]> fetchRecentUserDetails();

    /**
     * Finds existing user IDs from the provided list.
     *
     * @param userIds List of user IDs to check.
     * @return List of user IDs that exist in the database.
     */
    @Query("SELECT u.userId FROM User u WHERE u.userId IN :userIds")
    List<Long> findExistingIds(@Param("userIds") List<Long> userIds);

    @Query(value = "SELECT " +
            "    (SELECT COUNT(*) FROM users WHERE is_active = true) AS userCount, " +
            "    (SELECT COUNT(*) FROM groups WHERE is_active = true) AS groupCount, " +
            "    (SELECT COUNT(*) FROM course WHERE is_active = true) AS courseCount, " +
            "    (SELECT COUNT(*) FROM bundle WHERE is_active = true) AS bundleCount, " +
            "    (\n" +
            "        -- This block calculates the true number of enrollment \"events\"\n" +
            "        (\n" +
            "            -- Part 1: Count unique BUNDLE enrollment events\n" +
            "            SELECT COUNT(*) FROM (\n" +
            "                SELECT DISTINCT\n" +
            "                    -- An actor is either a group or a user. We create a unique key like 'g12' or 'u45'.\n" +
            "                    CASE WHEN group_id IS NOT NULL THEN 'g' || group_id ELSE 'u' || user_id END as actor_key,\n" +
            "                    bundle_id\n" +
            "                FROM enrollments\n" +
            "                WHERE bundle_id IS NOT NULL AND status = 'ACTIVE'\n" +
            "            ) as bundle_events\n" +
            "        ) + (\n" +
            "            -- Part 2: Count unique COURSE-ONLY enrollment events\n" +
            "            SELECT COUNT(*) FROM (\n" +
            "                SELECT DISTINCT\n" +
            "                    CASE WHEN group_id IS NOT NULL THEN 'g' || group_id ELSE 'u' || user_id END as actor_key,\n" +
            "                    course_id\n" +
            "                FROM enrollments\n" +
            "                -- This condition is key: it only considers course enrollments that are NOT part of a bundle.\n" +
            "                WHERE bundle_id IS NULL AND status = 'ACTIVE'\n" +
            "            ) as course_events\n" +
            "        )\n" +
            "    ) AS totalEnrollments",
            nativeQuery = true)
    AdminDashboardStatsOutDTO getAdminDashboardStats();
}
