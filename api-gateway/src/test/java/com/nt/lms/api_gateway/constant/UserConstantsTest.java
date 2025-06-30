package com.nt.lms.api_gateway.constant;
import com.nt.lms.api_gateway.constant.UserConstants;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

public class UserConstantsTest {

    @Test
    void testConstantValues() {
        assertEquals("User already exists with this email.", UserConstants.USER_ALREADY_EXISTS);
        assertEquals("Invalid email or password", UserConstants.INVALID_CREDENTIALS);
        assertEquals("User does not exist", UserConstants.USER_NOT_FOUND);
        assertEquals("User registered successfully.", UserConstants.USER_REGISTRATION_SUCCESS);
        assertEquals("User logout successfully", UserConstants.USER_LOGOUT_MESSAGE);
        assertEquals("Email and password must not be null", UserConstants.EMAIL_NOT_NULL_MESSAGE);
        assertEquals("Invalid refresh token", UserConstants.REFRESH_TOKEN_CREDENTIALS);
        assertEquals("Refresh token expired", UserConstants.REFRESH_TOKEN_EXPIRE_MASSAGE);
        assertEquals("Username already exists", UserConstants.USERNAME_ALREADY_EXISTS);
        assertEquals("Role does not exists", UserConstants.INVALID_ROLE);
        assertEquals("User deleted successfully", UserConstants.USER_DELETION_MESSAGE);
        assertEquals("This user is not able to login", UserConstants.USER_DELETED);
        assertEquals("Error occurred while fetching entries from database", UserConstants.DATABASE_ERROR);
        assertEquals("Error occurred", UserConstants.ERROR);
        assertEquals("Updation complete", UserConstants.UPDATED);
        assertEquals("User Role is not valid", UserConstants.INVALID_USER_ROLE);
        assertEquals("User details updated successfully", UserConstants.USER_UPDATED_SUCCESSFULLY);
        assertEquals("Invalid request , can not proceed.", UserConstants.INVALID_REQUEST);
    }

    @Test
    void testGetAdminId() {
        assertEquals(1L, UserConstants.getAdminId());
    }

    @Test
    void testPrivateConstructorThrowsException() throws Exception {
        Constructor<UserConstants> constructor = UserConstants.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        Exception exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertTrue(exception.getCause() instanceof UnsupportedOperationException);
        assertEquals("Utility class cannot be instantiated.", exception.getCause().getMessage());
    }
}
