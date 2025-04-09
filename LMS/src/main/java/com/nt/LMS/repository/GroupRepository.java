package com.nt.LMS.repository;

import com.nt.LMS.entities.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group , Long> {

    Group findById(long groupId);


    @Query("Select u from Group u where creatorId = :creatorId or creatorId = 1")
    List<Group> getGroups(long creatorId);


}
