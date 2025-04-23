package com.nt.LMS.service;

import com.nt.LMS.dto.GroupOutDTO;
import com.nt.LMS.dto.MessageOutDto;
import com.nt.LMS.dto.UserOutDTO;

import java.util.List;

/**
 * Service interface for managing groups.
 */
public interface GroupService {

    /**
     * Creates a new group.
     *
     * @param groupName The name of the group to be created.
     * @param username The username of the creator.
     * @return A MessageOutDto indicating the result of the operation.
     */
    MessageOutDto createGroup(String groupName, String username);

    /**
     * Deletes a group.
     *
     * @param groupId The ID of the group to be deleted.
     * @return A MessageOutDto indicating the result of the operation.
     */
    MessageOutDto delGroup(long groupId);

    /**
     * Adds a user to a group.
     *
     * @param userId The ID of the user to be added.
     * @param groupId The ID of the group to add the user to.
     * @return A MessageOutDto indicating the result of the operation.
     */
    MessageOutDto addUserToGroup(long userId, long groupId);

    /**
     * Removes a user from a group.
     *
     * @param userId The ID of the user to be removed.
     * @param groupId The ID of the group to remove the user from.
     * @return A MessageOutDto indicating the result of the operation.
     */
    MessageOutDto removeUserFromGroup(long userId, long groupId);

    /**
     * Retrieves all users in a specific group.
     *
     * @param groupId The ID of the group to fetch users from.
     * @return A list of UserOutDTO representing the users in the group.
     */
    List<UserOutDTO> getUsersInGroup(long groupId);

    /**
     * Retrieves all groups associated with a specific email.
     *
     * @param email The email of the user to retrieve groups for.
     * @return A list of GroupOutDTO representing the groups for the user.
     */
    List<GroupOutDTO> getGroups(String email);

    /**
     * Retrieves all groups in the system.
     *
     * @return A list of GroupOutDTO representing all groups.
     */
    List<GroupOutDTO> getAllGroups();

    long countGroups();
}
