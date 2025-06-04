package com.nt.LMS.constants;

/**
 * Contains constant messages related to Group operations.
 */
public final class GroupConstants {

    // Prevent instantiation
    private GroupConstants() {
        throw new UnsupportedOperationException("Utility class");
    }

    /** Message indicating group was created successfully. */
    public static final String GROUP_CREATED = "Group created successfully";

    /** Message indicating group was deleted successfully. */
    public static final String GROUP_DELETED = "Group deleted successfully";

    /** Message indicating operation failure. */
    public static final String GROUP_FAILURE = "Failed to complete operation";

    /** Message indicating user was added to group successfully. */
    public static final String USER_ADDED_TO_GROUP = "User added to group successfully";

    /** Message indicating user not found in the group. */
    public static final String USER_NOT_FOUND_IN_GROUP = "User is not present in group";

    /** Message indicating user was removed successfully from the group. */
    public static final String USER_REMOVED_SUCCESSFULLY = "User removed successfully";

    /** Message indicating the group was not found. */
    public static final String GROUP_NOT_FOUND = "Group with given credentials is not present";

    /** Message indicating the user is already a member of the group. */
    public static final String USER_ALREADY_PRESENT_IN_GROUP = "User already present in group";
}
