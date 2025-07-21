package com.nt.user_service_lms.repository;

import com.nt.user_service_lms.entities.Group;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for performing CRUD operations and custom queries
 * on {@link com.nt.user_service_lms.entities.Group} entities.
 */
@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    /**
     * Finds a group by its unique identifier.
     *
     * @param groupId the unique identifier of the group
     * @return an {@link Optional} containing the found group, or empty if not found
     */
    Optional<Group> findById(long groupId);

    /**
     * Finds all groups created by a specific creator.
     *
     * @param creatorId the unique identifier of the creator
     * @return a list of groups created by the given creator
     */
    List<Group> findByCreatorId(long creatorId);

    /**
     * Checks if a group exists by its unique identifier.
     *
     * @param groupId the unique identifier of the group
     * @return {@code true} if the group exists, {@code false} otherwise
     */
    boolean existsById(long groupId);

    /**
     * Finds the five most recently created groups, ordered by group ID in descending order.
     *
     * @return a list of the five most recently created groups
     */
    List<Group> findTop5ByOrderByGroupIdDesc();

    /**
     * Finds the group IDs that exist in the database from a given list of group IDs.
     *
     * @param groupIds the list of group IDs to check
     * @return a list of existing group IDs
     */
    @Query("SELECT g.groupId FROM Group g WHERE g.groupId IN :groupIds")
    List<Long> findExistingIds(@Param("groupIds") List<Long> groupIds);

    /**
     * Soft deletes a group by setting its {@code isActive} flag to {@code false}.
     *
     * @param groupId the unique identifier of the group to soft delete
     */
    @Modifying
    @Transactional
    @Query("UPDATE Group ug SET ug.isActive = false WHERE ug.groupId = :groupId")
    void softDeleteByGroupId(@Param("groupId") Long groupId);


    Optional<Group> findByGroupId(Long groupId);

    /**
     * Finds all groups that are currently active.
     *
     * @return a list of active groups
     */
    List<Group> findByIsActiveTrue();

}
