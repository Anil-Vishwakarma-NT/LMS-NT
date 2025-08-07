package com.nt.course_service_lms.dto.outDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDataOutDTO {

    /**
     * List of recent course summaries.
     */
    private List<CourseSummaryOutDTO> recentCourses;

    /**
     * List of recent bundle summaries.
     */
    private List<BundleSummaryOutDTO> recentBundles;
}
