package com.nt.lms.api_gateway_lms.repository;

import com.nt.lms.api_gateway_lms.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for performing CRUD and custom operations on the {@link Users} entity.
 * <p>
 * Extends {@link JpaRepository} to provide built-in methods for interacting with the database,
 * and includes additional query methods for user-specific logic.
 * </p>
 */
@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    /**
     * Finds a user by their email address (case-insensitive).
     *
     * @param email The email address of the user.
     * @return An {@link Optional} containing the user if found, or empty if no user with the provided email exists.
     */
    Optional<Users> findByEmailIgnoreCase(String email);

    /**
     * Finds a user by their username (case-insensitive).
     *
     * @param userName The username of the user.
     * @return An {@link Optional} containing the user if found, or empty if no user with the provided username exists.
     */
    Optional<Users> findByUserNameIgnoreCase(String userName);

    /**
     * Finds a user by their unique user ID.
     *
     * @param userId The unique identifier of the user.
     * @return An {@link Optional} containing the user if found, or empty if no user with the provided ID exists.
     */
    Optional<Users> findById(long userId);

    /**
     * Retrieves a list of users who are managed by a given manager.
     *
     * @param managerId The user ID of the manager.
     * @return A list of {@link Users} managed by the specified manager.
     */
    List<Users> findByManagerId(long managerId);

    /**
     * Checks if a user exists with the specified ID.
     *
     * @param id The ID to check for existence.
     * @return {@code true} if a user exists with the given ID, otherwise {@code false}.
     */
    boolean existsById(long id);

    /**
     * Retrieves recent active user details including their full name, email, role,
     * manager name, and account creation date.
     * <p>
     * Excludes the super admin (user ID 1) from the results and returns the 5 most recently created users.
     * </p>
     *
     * @return A list of object arrays where each array contains:
     * <ul>
     *     <li>full name (String)</li>
     *     <li>email (String)</li>
     *     <li>role (String)</li>
     *     <li>manager name (String)</li>
     *     <li>created at (Timestamp)</li>
     * </ul>
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
}
