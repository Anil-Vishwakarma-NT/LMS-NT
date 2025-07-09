package com.nt.user_service_lms.dto.outDTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupUserOutDTO {

    long userId;
    String firstName;
    String lastName;
    double progress;
    long enrols;

}
