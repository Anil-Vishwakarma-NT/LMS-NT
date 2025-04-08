package com.nt.LMS.dto;

import com.nt.LMS.entities.User;
import lombok.Data;

@Data
public class Admindto {

    private Long userId;
    private String role;

    public Admindto(){

    }

    public Admindto(User user) {
        this.userId = user.getUserId();
        this.role = user.getRole() != null ? user.getRole().getName() : null; // Assuming 'getRoleName()' is a method in Role
    }
}
