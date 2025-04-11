package com.nt.LMS.repository;

import com.nt.LMS.entities.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {
    void deleteByGroupId(Long groupId);
    List<UserGroup> findAllByGroupId(Long groupId);
    Optional<UserGroup> findByUserIdAndGroupId(Long userId, Long groupId);
    void deleteByUserIdAndGroupId(Long userId, Long groupId);
}
