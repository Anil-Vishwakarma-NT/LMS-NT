package com.nt.LMS.converter;

import com.nt.LMS.dto.GroupOutDTO;
import com.nt.LMS.dto.UserOutDTO;
import com.nt.LMS.entities.Group;
import com.nt.LMS.entities.User;

public class GroupDTOConverter {
    public GroupOutDTO groupToOutDto(Group group , String creator){
        GroupOutDTO groupout = new GroupOutDTO();
        groupout.setGroupId(group.getGroupId());
        groupout.setGroupName(group.getGroupName());
        if (creator != null) {
            groupout.setCreatorName(creator);
        }
        return groupout;
    }
}
