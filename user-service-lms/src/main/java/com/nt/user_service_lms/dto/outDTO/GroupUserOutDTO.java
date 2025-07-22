package com.nt.user_service_lms.dto.outDTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a user in a group with progress and enrolment details.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupUserOutDTO {

    /**
     * The unique identifier of the user.
     */
    private long userId;

    /**
     * The first name of the user.
     */
    private String firstName;

    /**
     * The last name of the user.
     */
    private String lastName;

    /**
     * The progress of the user in the group.
     */
    private double progress;

    /**
     * The number of enrolments of the user.
     */
    private long enrols;
}
