package com.nt.user_service_lms.dto.outDTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBundleOutDTO {

    private long bundleId;

    private String bundleName;

    private long number_of_courses;

    private double progress;

}
