package com.nt.LMS.repository;

import com.nt.LMS.entities.User;
import com.nt.LMS.entities.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {
    /**
     * To delete a group from Group Table.
     * @param groupId
     */
    void deleteByGroupId(Long groupId);
    /**
     * Find all group user mapping by groupId.
     * @param groupId
     * @return usergroup
     */
    List<UserGroup> findAllByGroupId(Long groupId);
    /**
     * Find all group user mapping by groupId and userId.
     * @param groupId
     * @param userId
     * @return UserGroup
     */
    Optional<UserGroup> findByUserIdAndGroupId(Long userId, Long groupId);

    Optional<UserGroup> findByUserId(Long userId);

    @Query(value = "SELECT COUNT(*) FROM user_group e WHERE e.user_id = :userId", nativeQuery = true)
    long  getAllUserGroups(@Param("userId") long userId);
}
