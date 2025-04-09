package com.nt.LMS.repository;

import com.nt.LMS.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    @Query("Select u from Role u where u.name = :rolename")
    Role findByRoleName(String rolename);
}
