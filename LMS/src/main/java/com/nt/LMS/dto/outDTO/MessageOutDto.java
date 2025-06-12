package com.nt.LMS.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageOutDto {
    /**
     * The message for the user.
     * This is required for response and can be blank.
     */
    private String message;
}
