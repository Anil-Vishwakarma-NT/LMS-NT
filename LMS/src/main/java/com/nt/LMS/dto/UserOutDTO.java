package com.nt.LMS.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOutDTO {

    private long userId;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String manager;



}
