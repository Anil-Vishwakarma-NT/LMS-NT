package com.nt.LMS.repository;


import com.nt.LMS.entities.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.beans.Transient;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);


//    @Query("Select u from User u where u.user_id = :user_id")
    User findById(long user_id);


    @Modifying
    @Transactional
    @Query("Update User u Set u.manager_id = :new_id where u.manager_id = :current_id")
    void updateManager(@Param("new_id") long new_manager , @Param("current_id") long current_manager);


    @Modifying
    @Query("Update User u Set u.role.role_id =:new_role where u.userId = :userId")
    long updateRole(@Param("new_role") long newrole , @Param("userId") long userId);


    @Query("Select u from User u where u.userId != 1")
    List<User> getAllEmployees();

    @Query("Select u from User u where u.manager_id = :manager_id")
    List<User> getEmployeesUnderManager(long manager_id);

}
