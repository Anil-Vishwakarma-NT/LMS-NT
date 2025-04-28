package com.nt.LMS.repository;

import com.nt.LMS.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
