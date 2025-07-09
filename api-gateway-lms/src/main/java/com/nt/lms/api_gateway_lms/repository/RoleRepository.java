package com.nt.lms.api_gateway_lms.repository;

import com.nt.lms.api_gateway_lms.entities.Role;
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
