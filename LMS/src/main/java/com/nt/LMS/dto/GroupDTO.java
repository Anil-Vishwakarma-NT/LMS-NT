package com.nt.LMS.dto;


import com.nt.LMS.entities.Group;
import lombok.Data;


@Data
public class GroupDTO {



       private String groupName;
       private String userName;
       private long groupId;
       private long userId;


    public GroupDTO(Group group) {
        this.groupId= group.getGroupId();
        this.groupName = group.getGroupName();
        this.userId = group.getCreatorId();

    }

}
