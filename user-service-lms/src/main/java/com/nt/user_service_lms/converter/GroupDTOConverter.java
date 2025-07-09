package com.nt.user_service_lms.converter;

import com.nt.user_service_lms.dto.outDTO.GroupOutDTO;
import com.nt.user_service_lms.entities.Group;
import org.springframework.stereotype.Component;

/**
 * Converter class to transform Group entities into GroupOutDTOs.
 */
@Component
public final class GroupDTOConverter {

    /**
     * Converts a Group entity to a GroupOutDTO.
     *
     * @param group   the Group entity to convert
     * @param creator the name of the group's creator
     * @return the converted GroupOutDTO
     */
    public GroupOutDTO groupToOutDto(final Group group, final String creator) {
        GroupOutDTO groupout = new GroupOutDTO();
        groupout.setGroupId(group.getGroupId());
        groupout.setGroupName(group.getGroupName());
        if (creator != null) {
            groupout.setCreatorName(creator);
        }
        return groupout;
    }
}
