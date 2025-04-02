package com.nt.LMS.dto;
import lombok.Data;

@Data
public class RegisterDto {
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private String password;
    private Long roleId;
//    private Long groupId;

}
