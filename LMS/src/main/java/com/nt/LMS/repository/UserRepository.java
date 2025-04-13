package com.nt.LMS.repository;


import com.nt.LMS.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUserNameIgnoreCase(String userName);
    Optional<User> findById(long userId);
    List<User> findByManagerId(long managerId);



}
