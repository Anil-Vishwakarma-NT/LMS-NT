package com.nt.LMS.repository;


import com.nt.LMS.entities.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);

    @Query("Select u.role.name from User u where userId = :userId")
            String getRoleById(long userId);


//    @Query("Select u from User u where u.userId = :userId")
    Optional<User> findById(long userId);


    @Modifying
    @Transactional
    @Query("Update User u Set u.managerId = :newId where u.managerId = :currentId")
    void updateManager(@Param("newId") long new_manager , @Param("currentId") long current_manager);


    @Modifying
    @Transactional
    @Query("Update User u Set u.role.roleId =:newRole where u.userId = :userId")
    int updateRole(@Param("newRole") long newrole , @Param("userId") long userId);


    @Query("Select u from User u where u.userId != 1 or u.role.roleId != 1")
    List<User> getEmployees();

    @Query("Select u from User u where u.managerId = :managerId")
    List<User> getEmployeesUnderManager(long managerId);

}
