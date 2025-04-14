package com.nt.LMS.service;

import com.nt.LMS.dto.GroupOutDTO;
import com.nt.LMS.dto.MessageOutDto;
import com.nt.LMS.dto.UserOutDTO;

import java.util.List;

public interface GroupService {
    public MessageOutDto createGroup(String groupName, String username);

    public MessageOutDto delGroup(long groupId);

    public MessageOutDto addUserToGroup(long userId, long groupId);

    public MessageOutDto removeUserFromGroup(long userId, long groupId);

    public List<UserOutDTO> getUsersInGroup(long groupId);

    public List<GroupOutDTO> getGroups(String email);

    public List<GroupOutDTO> getAllGroups();
}
