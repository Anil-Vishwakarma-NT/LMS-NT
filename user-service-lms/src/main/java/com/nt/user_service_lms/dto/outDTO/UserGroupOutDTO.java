package com.nt.user_service_lms.dto.outDTO;

import lombok.Data;

/**
 * Data Transfer Object representing the details of a group associated with a user.
 * <p>
 * This class encapsulates basic information such as group name, group ID,
 * progress made by the user in the group, and the total number of enrollments.
 * </p>
 */
@Data
public class UserGroupOutDTO {

    /**
     * The name of the group.
     */
    private String groupName;

    /**
     * The unique identifier of the group.
     */
    private long groupId;

    /**
     * The user's progress within the group, represented as a percentage.
     */
    private double progress;

    /**
     * The total number of enrollments in the group.
     */
    private long enrols;
}
