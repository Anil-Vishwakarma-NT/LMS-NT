package com.nt.user_service_lms.service.serviceImpl;

import com.nt.user_service_lms.dto.outDTO.CourseReportOutDTO;
import com.nt.user_service_lms.repository.CourseReportRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CourseReportServiceImpl {

    private final CourseReportRepository repository;

    public CourseReportServiceImpl(CourseReportRepository repository) {
        this.repository = repository;
    }

    public Map<String, Object> getAllCourseKpis(int page, int size) {
        int offset = page * size;
        List<CourseReportOutDTO> records = repository.fetchCourseKpiReport(size, offset);
        long total = repository.getTotalCourseCount();

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", total);
        return result;
    }
}
