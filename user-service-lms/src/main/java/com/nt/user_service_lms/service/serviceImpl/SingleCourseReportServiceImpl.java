package com.nt.user_service_lms.service.serviceImpl;

import com.nt.user_service_lms.dto.outDTO.SingleCourseReportOutDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SingleCourseReportServiceImpl {

    @PersistenceContext
    private final EntityManager entityManager;

    @Transactional
    public SingleCourseReportOutDTO getCourseReport(Long courseId) {
        entityManager.createNativeQuery("REFRESH MATERIALIZED VIEW course_details").executeUpdate();

        String sql = "SELECT * FROM course_details WHERE course_id = :courseId";
        List<Object[]> results = entityManager
                .createNativeQuery(sql)
                .setParameter("courseId", courseId)
                .getResultList();

        if (results.isEmpty()) {
            return null;
        }

        Object[] first = results.get(0);
        SingleCourseReportOutDTO report = SingleCourseReportOutDTO.builder()
                .courseId(((Number) first[0]).longValue())
                .courseTitle((String) first[1])
                .courseDescription((String) first[2])
                .courseLevel((String) first[3])
                .enrolledUsers(results.stream().map(r -> SingleCourseReportOutDTO.EnrolledUserDTO.builder()
                        .userId(((Number) r[5]).longValue())
                        .fullName((r[7] != null) ? String.valueOf(r[7]) : null)
                        .enrollmentSource((r[12] != null) ? String.valueOf(r[12]) : null)
                        .assignedAt((r[15] != null) ? ((Timestamp) r[15]).toLocalDateTime() : null)
                        .deadline((r[16] != null) ? ((Timestamp) r[16]).toLocalDateTime().toLocalDate() : null)
                        .bundleId((r[19] != null) ? ((Number) r[19]).longValue() : null)
                        .bundleName((r[20] != null) ? String.valueOf(r[20]) : null)
                        .groupId((r[22] != null) ? ((Number) r[22]).longValue() : null)
                        .groupName((r[23] != null) ? String.valueOf(r[23]) : null)
                        .courseCompletionPercentage((r[25] != null) ? ((Number) r[25]).doubleValue() : 0.0)
                        .status((r[27] != null) ? String.valueOf(r[27]) : null)
                        .adherence((r[28] != null) ? String.valueOf(r[28]) : null)

                        .build()).collect(Collectors.toList()))
                .build();

        return report;
    }
}
