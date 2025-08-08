package com.nt.user_service_lms.service.serviceImpl;
import com.nt.user_service_lms.dto.outDTO.SingleGroupReportOutDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SingleGroupReportServiceImpl {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public SingleGroupReportOutDTO getGroupReport(Long groupId) {
        // Refresh materialized view to ensure up-to-date data
        entityManager.createNativeQuery("REFRESH MATERIALIZED VIEW  group_details").executeUpdate();

        String sql = "SELECT * FROM group_details WHERE group_id = :groupId";
        List<Object[]> resultList = entityManager
                .createNativeQuery(sql)
                .setParameter("groupId", groupId)
                .getResultList();

        if (resultList.isEmpty()) return null;

        String groupName = (String) resultList.get(0)[2]; // group_name

        List<SingleGroupReportOutDTO.GroupUserCourseDTO> userCourses = resultList.stream().map(row ->
                SingleGroupReportOutDTO.GroupUserCourseDTO.builder()
                        .userId(getLong(row[11]))
                        .fullName((String) row[13])
                        .courseId(getLong(row[6]))
                        .courseTitle((String) row[7])
                        .courseDescription((String) row[8])
                        .courseLevel((String) row[9])
                        .enrollmentSource((String) row[18])
                        .assignedAt(toDateTime(row[21]))
                        .deadline(toDateTime(row[22]))
                        .courseCompletionPercentage(row[26] == null ? 0.0 : ((Number) row[26]).doubleValue())
                        .status((String) row[28])
                        .adherence((String) row[29])
                        .bundleId(getLong(row[3]))
                        .bundleName((String) row[4])
                        .build()
        ).collect(Collectors.toList());

        return SingleGroupReportOutDTO.builder()
                .groupId(groupId)
                .groupName(groupName)
                .userCourses(userCourses)
                .build();
    }

    private LocalDateTime toDateTime(Object o) {
        return o instanceof Timestamp ts ? ts.toLocalDateTime() : null;
    }

    private Long getLong(Object o) {
        return o instanceof Number n ? n.longValue() : null;
    }
}
