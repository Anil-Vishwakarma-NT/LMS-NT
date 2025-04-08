package com.nt.LMS.repository;

import com.nt.LMS.entities.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group , Long> {

    Group findById(long group_id);

    Group findByGroup_Name(String group_name);

    @Query("Select u from Group u where creator_id = :creator_id or creator_id = 1")
    List<Group> getGroups(long creator_id);


}
