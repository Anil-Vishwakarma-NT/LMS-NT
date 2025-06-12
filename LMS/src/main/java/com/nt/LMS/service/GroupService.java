package com.nt.LMS.service;

import com.nt.LMS.dto.outDTO.*;

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
    StandardResponseOutDTO<MessageOutDto> createGroup(String groupName, String username);

    /**
     * Deletes a group.
     *
     * @param groupId The ID of the group to be deleted.
     * @return A MessageOutDto indicating the result of the operation.
     */
    StandardResponseOutDTO<MessageOutDto> deleteGroup(long groupId);

    /**
     * Adds a user to a group.
     *
     * @param userId The ID of the user to be added.
     * @param groupId The ID of the group to add the user to.
     * @return A MessageOutDto indicating the result of the operation.
     */
    StandardResponseOutDTO<MessageOutDto> addUserToGroup(long userId, long groupId);

    /**
     * Removes a user from a group.
     *
     * @param userId The ID of the user to be removed.
     * @param groupId The ID of the group to remove the user from.
     * @return A MessageOutDto indicating the result of the operation.
     */
    StandardResponseOutDTO<MessageOutDto> removeUserFromGroup(long userId, long groupId);

    /**
     * Retrieves all users in a specific group.
     *
     * @param groupId The ID of the group to fetch users from.
     * @return A list of UserOutDTO representing the users in the group.
     */
    StandardResponseOutDTO<List<UserOutDTO>> getUsersInGroup(long groupId);

    /**
     * Retrieves all groups associated with a specific email.
     *
     * @param email The email of the user to retrieve groups for.
     * @return A list of GroupOutDTO representing the groups for the user.
     */
    StandardResponseOutDTO<List<GroupOutDTO>> getGroups(String email);

    /**
     * Retrieves all groups in the system.
     *
     * @return A list of GroupOutDTO representing all groups.
     */
    StandardResponseOutDTO<List<GroupOutDTO>> getAllGroups();

    long countGroups();

    /**
     * Retrieves summaries of the 5 most recent groups.
     *
     * @return a list containing summaries of the most recently created groups with member counts
     */
    StandardResponseOutDTO<List<GroupSummaryOutDTO>> getRecentGroupSummaries();
}
