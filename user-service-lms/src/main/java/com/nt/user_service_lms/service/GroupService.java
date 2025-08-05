package com.nt.user_service_lms.service;

import com.nt.user_service_lms.dto.inDTO.GroupInDTO;
import com.nt.user_service_lms.dto.outDTO.BundleOutDTO;
import com.nt.user_service_lms.dto.outDTO.CourseInfoOutDTO;
import com.nt.user_service_lms.dto.outDTO.GroupBundleOutDTO;
import com.nt.user_service_lms.dto.outDTO.GroupCourseOutDTO;
import com.nt.user_service_lms.dto.outDTO.GroupOutDTO;
import com.nt.user_service_lms.dto.outDTO.GroupSummaryOutDTO;
import com.nt.user_service_lms.dto.outDTO.GroupUserOutDTO;
import com.nt.user_service_lms.dto.outDTO.MessageOutDTO;
import com.nt.user_service_lms.dto.outDTO.StandardResponseOutDTO;
import com.nt.user_service_lms.dto.outDTO.UserGroupOutDTO;

import java.util.List;

/**
 * Interface for managing group-related operations.
 */
public interface GroupService {

    /**
     * Creates a new group.
     *
     * @param groupName  the name of the group to be created
     * @param username   the username of the creator
     * @param employeeId the list of employee IDs to be added to the group
     * @return a StandardResponseOutDTO containing a MessageOutDto indicating the result
     */
    StandardResponseOutDTO<MessageOutDTO> createGroup(String groupName, String username, List<Long> employeeId);

    /**
     * Deletes a group.
     *
     * @param groupId the ID of the group to be deleted
     * @return a StandardResponseOutDTO containing a MessageOutDto indicating the result
     */
    StandardResponseOutDTO<MessageOutDTO> deleteGroup(long groupId);

    /**
     * Adds a user to a group.
     *
     * @param groupInDTO the group input data transfer object
     * @param username   the username of the user to be added
     * @return a StandardResponseOutDTO containing a MessageOutDto indicating the result
     */
    StandardResponseOutDTO<MessageOutDTO> addUserToGroup(GroupInDTO groupInDTO, String username);

    /**
     * Updates the name of a group.
     *
     * @param groupId   the ID of the group to be updated
     * @param groupName the new name for the group
     * @return a StandardResponseOutDTO containing a MessageOutDto indicating the result
     */
    StandardResponseOutDTO<MessageOutDTO> updateGroup(long groupId, String groupName);

    /**
     * Removes a user from a group.
     *
     * @param userId  the ID of the user to be removed
     * @param groupId the ID of the group to remove the user from
     * @return a StandardResponseOutDTO containing a MessageOutDto indicating the result
     */
    StandardResponseOutDTO<MessageOutDTO> removeUserFromGroup(long userId, long groupId);

    /**
     * Retrieves bundle details a user is enrolled in.
     *
     * @param userId  the ID of the user
     * @param groupId
     * @return a StandardResponseOutDTO containing a list of BundleOutDTO
     */
    StandardResponseOutDTO<List<BundleOutDTO>> getUserBundles(long groupId, long userId);

    /**
     * Retrieves all groups associated with a specific email.
     *
     * @param email the email of the user to retrieve groups for
     * @return a StandardResponseOutDTO containing a list of GroupOutDTO representing the groups for the user
     */
    StandardResponseOutDTO<List<GroupOutDTO>> getGroups(String email);

    /**
     * Retrieves all groups in the system.
     *
     * @return a StandardResponseOutDTO containing a list of GroupOutDTO representing all groups
     */
    StandardResponseOutDTO<List<GroupOutDTO>> getAllGroups();

    /**
     * Counts the total number of groups.
     *
     * @return the total number of groups
     */
    long countGroups();

    /**
     * Retrieves all active groups.
     *
     * @return a StandardResponseOutDTO containing a list of active GroupOutDTO
     */
    StandardResponseOutDTO<List<GroupOutDTO>> getAllActiveGroups();

    /**
     * Retrieves the courses for a user in a group.
     *
     * @param groupId the ID of the group
     * @param userId  the ID of the user
     * @return a StandardResponseOutDTO containing a list of CourseInfoOutDTO
     */
    StandardResponseOutDTO<List<CourseInfoOutDTO>> getUserCourses(long groupId, long userId);

    /**
     * Retrieves summaries of the 5 most recent groups.
     *
     * @return a StandardResponseOutDTO containing a list of GroupSummaryOutDTO for the most recently created groups
     */
    StandardResponseOutDTO<List<GroupSummaryOutDTO>> getRecentGroupSummaries();

    /**
     * Retrieves course details for a group.
     *
     * @param groupId the ID of the group
     * @return a StandardResponseOutDTO containing a list of GroupCourseOutDTO
     */
    StandardResponseOutDTO<List<GroupCourseOutDTO>> getCourseDetail(long groupId);

    /**
     * Retrieves user details for a group.
     *
     * @param groupId the ID of the group
     * @return a StandardResponseOutDTO containing a list of GroupUserOutDTO
     */
    StandardResponseOutDTO<List<GroupUserOutDTO>> getUserDetail(long groupId);

    /**
     * Retrieves group details a user is enrolled in.
     *
     * @param email the ID of the user
     * @return a StandardResponseOutDTO containing a list of UserGroupOutDTO
     */
    StandardResponseOutDTO<List<UserGroupOutDTO>> getUserGroupDetail(String email);

    /**
     * Retrieves course details of a group  .
     *
     * @param groupId the ID of the user
     * @return a StandardResponseOutDTO containing a list of GroupCourseOutDTO
     */
    StandardResponseOutDTO<List<GroupCourseOutDTO>> getCourseEmpDetail(long groupId);

    /**
     * Retrieves bundle details of a group.
     *
     * @param groupId the ID of the user
     * @return a StandardResponseOutDTO containing a list of GroupBundleOutDTO
     */
    StandardResponseOutDTO<List<GroupBundleOutDTO>> getGroupBundles(Long groupId);
}
