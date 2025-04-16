package com.nt.LMS.repository;

import com.nt.LMS.entities.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group , Long> {

    Optional<Group> findById(long groupId);

    boolean existsById(long groupId);

    List<Group> findByCreatorId(long creatorId);


}
