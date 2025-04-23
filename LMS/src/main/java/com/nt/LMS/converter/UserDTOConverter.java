package com.nt.LMS.converter;

import com.nt.LMS.dto.UserOutDTO;
import com.nt.LMS.entities.User;
import org.springframework.stereotype.Component;

/**
 * A utility class to convert {@link User} entities to {@link UserOutDTO} objects.
 * This class is not designed for extension and should not be subclassed.
 */
@Component
public final class UserDTOConverter {

    /**
     * Converts a {@link User} entity to a {@link UserOutDTO}.
     *
     * @param user the {@link User} entity to convert. Must not be null.
     * @param manager the manager's name, can be null if not available.
     * @return a {@link UserOutDTO} representing the user entity.
     */
    public UserOutDTO userToOutDto(final User user, final String manager) {
        UserOutDTO userout = new UserOutDTO();
        userout.setUserId(user.getUserId());
        userout.setUsername(user.getUserName());
        userout.setEmail(user.getEmail());
        userout.setFirstName(user.getFirstName());
        userout.setLastName(user.getLastName());

        if (manager != null) {
            userout.setManager(manager);
            System.out.println(manager);
        }
        return userout;
    }
}
