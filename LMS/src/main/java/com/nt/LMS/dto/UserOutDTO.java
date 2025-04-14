package com.nt.LMS.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import javax.management.ConstructorParameters;

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
    private String message;



}
