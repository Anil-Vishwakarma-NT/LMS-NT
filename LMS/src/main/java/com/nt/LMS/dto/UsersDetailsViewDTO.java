package com.nt.LMS.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * DTO representing detailed view of a user with role and manager information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsersDetailsViewDTO {

    private String fullName;
    private String email;
    private String role;
    private String managerName;
    private Timestamp createdAt;
}
