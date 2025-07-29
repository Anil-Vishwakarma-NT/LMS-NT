package com.nt.user_service_lms.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class AdminDashboardStatsOutDTO {
    private Long userCount;
    private Long groupCount;
    private Long courseCount;
    private Long bundleCount;
    private Long totalEnrollments;
}
