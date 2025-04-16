package com.nt.LMS.repository;

import com.nt.LMS.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    /**
     * Find role using roleName.
     * @param roleName
     * @return role entity.
     */
    Optional<Role> findByName(String roleName);
}
