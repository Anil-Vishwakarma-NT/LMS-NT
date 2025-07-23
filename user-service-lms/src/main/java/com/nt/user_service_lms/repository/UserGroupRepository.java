package com.nt.user_service_lms.repository;

import com.nt.user_service_lms.entities.UserGroup;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing UserGroup entities.
 * Provides CRUD operations and custom queries for user-group relationships
 * in the Learning Management System.
 *
 * @author LMS Team
 * @version 1.0
 * @since 1.0
 */
@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {

    /**
     * Deletes a user group association by group ID.
     * This performs a hard delete, permanently removing the record from the database.
     *
     * @param groupId the ID of the group to delete associations for
     * @throws IllegalArgumentException if groupId is null
     */
    void deleteByGroupId(Long groupId);

    /**
     * Finds all user group mappings for a specific group.
     * Retrieves all users that belong to the specified group.
     *
     * @param groupId the ID of the group to find users for
     * @return a list of UserGroup entities associated with the group,
     * empty list if no associations are found
     * @throws IllegalArgumentException if groupId is null
     */
    List<UserGroup> findAllByGroupId(Long groupId);

    /**
     * Finds a specific user group mapping by user ID and group ID.
     * Useful for checking if a user belongs to a specific group.
     *
     * @param userId  the ID of the user
     * @param groupId the ID of the group
     * @return an Optional containing the UserGroup entity if found,
     * empty Optional if no association exists
     * @throws IllegalArgumentException if userId or groupId is null
     */
    Optional<UserGroup> findByUserIdAndGroupId(Long userId, Long groupId);

    /**
     * Finds a user group mapping by user ID.
     * Retrieves the group association for a specific user.
     *
     * @param userId the ID of the user
     * @return an Optional containing the UserGroup entity if found,
     * empty Optional if no association exists
     * @throws IllegalArgumentException if userId is null
     */
    Optional<UserGroup> findByUserId(Long userId);

    /**
     * Counts the total number of groups a user belongs to.
     * Uses a native SQL query to count user group associations.
     *
     * @param userId the ID of the user to count groups for
     * @return the number of groups the user belongs to
     * @throws IllegalArgumentException if userId is null
     */
    @Query(value = "SELECT COUNT(*) FROM user_group e WHERE e.user_id = :userId", nativeQuery = true)
    long getAllUserGroups(@Param("userId") long userId);

    /**
     * Retrieves all user IDs that belong to a specific group.
     * Useful for getting a list of users in a group without fetching full entities.
     *
     * @param groupId the ID of the group to find users for
     * @return a list of user IDs belonging to the group,
     * empty list if no users are found
     * @throws IllegalArgumentException if groupId is null
     */
    @Query("SELECT ug.userId FROM UserGroup ug WHERE ug.groupId = :groupId")
    List<Long> findUserIdsByGroupId(@Param("groupId") Long groupId);

    /**
     * Performs a soft delete of all user group associations for a specific group.
     * Sets the isActive flag to false instead of physically deleting the records.
     * This allows for data recovery and audit trail maintenance.
     *
     * @param groupId the ID of the group to soft delete associations for
     * @throws IllegalArgumentException if groupId is null
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserGroup ug SET ug.isActive = false WHERE ug.groupId = :groupId")
    void softDeleteByGroupId(Long groupId);

    /**
     * Performs a soft delete of a specific user group association.
     * Sets the isActive flag to false for the specific user-group relationship
     * instead of physically deleting the record.
     *
     * @param groupId the ID of the group
     * @param userId  the ID of the user
     * @throws IllegalArgumentException if groupId or userId is null
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserGroup ug SET ug.isActive = false WHERE ug.groupId = :groupId AND ug.userId = :userId")
    void softDeleteByGroupIdAndUserId(Long groupId, Long userId);
}
