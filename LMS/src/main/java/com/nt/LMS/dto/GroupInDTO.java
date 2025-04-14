package com.nt.LMS.dto;


import com.nt.LMS.entities.Group;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupInDTO {
    @Pattern(
            regexp = "^[a-zA-Z]+$",
            message = "Group name must contain only alphabets."
    )
       private String groupName;


//       private String userName;

    @Pattern(
            regexp = "^[0-9]",
            message = "Invalid Id"
    )
       private long groupId;

    @Pattern(
            regexp = "^[0-9]",
            message = "Invalid Id"
    )
       private long userId;
}
