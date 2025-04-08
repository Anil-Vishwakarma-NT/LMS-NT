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
        this.groupId= group.getGroup_id();
        this.groupName = group.getGroup_name();
        this.userId = group.getCreator_id();

    }

}
