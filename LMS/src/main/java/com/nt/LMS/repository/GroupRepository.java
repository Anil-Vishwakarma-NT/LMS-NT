package com.nt.LMS.repository;

import com.nt.LMS.entities.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Group entity interactions.
 */
@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    /**
     * Finds a group by its ID.
     *
     * @param groupId The ID of the group to search for.
     * @return An Optional containing the found Group, or empty if not found.
     */
    Optional<Group> findById(long groupId);

    /**
     * Finds all groups created by a specific creator.
     *
     * @param creatorId The ID of the creator to filter groups by.
     * @return A list of groups created by the given creator.
     */
    List<Group> findByCreatorId(long creatorId);
    boolean existsById(long groupId);

}
