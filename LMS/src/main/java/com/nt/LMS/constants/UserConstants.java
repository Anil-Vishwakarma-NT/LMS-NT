package com.nt.LMS.constants;

/**
 * This class contains constant values used throughout the application related to user operations.
 * It includes messages related to user actions, such as registration, login, and deletion.
 * This class cannot be instantiated.
 */
public final class UserConstants {

    /**
     * Message indicating that a user already exists with the provided email.
     */
    public static final String USER_ALREADY_EXISTS = "User already exists with this email.";

    /**
     * Message indicating that the email or password provided is invalid.
     */
    public static final String INVALID_CREDENTIALS = "Invalid email or password";

    /**
     * Message indicating that the user does not exist.
     */
    public static final String USER_NOT_FOUND = "User does not exist";

    /**
     * Message indicating that the user has been successfully registered.
     */
    public static final String USER_REGISTRATION_SUCCESS = "User registered successfully.";

    /**
     * Message indicating that the user has logged out successfully.
     */
    public static final String USER_LOGOUT_MESSAGE = "User logout successfully";

    /**
     * Message indicating that the email or password cannot be null.
     */
    public static final String EMAIL_NOT_NULL_MESSAGE = "Email and password must not be null";

    /**
     * Message indicating that the refresh token provided is invalid.
     */
    public static final String REFRESH_TOKEN_CREDENTIALS = "Invalid refresh token";

    /**
     * Message indicating that the refresh token has expired.
     */
    public static final String REFRESH_TOKEN_EXPIRE_MASSAGE = "Refresh token expired";

    /**
     * Message indicating that the username already exists.
     */
    public static final String USERNAME_ALREADY_EXISTS = "Username already exists";

    /**
     * Message indicating that the user has been deleted successfully.
     */
    public static final String USER_DELETION_MESSAGE = "User deleted successfully";

    /**
     * Message indicating that there was an error while fetching entries from the database.
     */
    public static final String DATABASE_ERROR = "Error occurred while fetching entries from database";

    /**
     * General error message.
     */
    public static final String ERROR = "Error occurred";

    /**
     * Message indicating that the update operation was completed.
     */
    public static final String UPDATED = "Updation complete";

    /**
     * Message indicating that the user role is not valid.
     */
    public static final String INVALID_USER_ROLE = "User Role is not valid";

    public static final String USER_UPDATED_SUCCESSFULLY = "User details updated successfully";

    /**
     * The ID of the administrator user.
     */
    private static final Long ADMIN_ID = 2L;

    /**
     * to access admin id.
     * @return long
     */
    public static Long getAdminId() {
        return ADMIN_ID;
    }


    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private UserConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated.");
    }

    public static String INVALID_REQUEST = "Invalid request , can not proceed.";
}
