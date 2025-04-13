package com.nt.LMS.dto;


import com.nt.LMS.entities.Group;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class GroupInDTO {



       private String groupName;
       private String userName;
       private long groupId;
       private long userId;


    public GroupInDTO(Group group) {
        this.groupId= group.getGroupId();
        this.groupName = group.getGroupName();
        this.userId = group.getCreatorId();

    }

}
