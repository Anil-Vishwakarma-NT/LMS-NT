package com.nt.user_service_lms.service.serviceImpl;

import com.nt.user_service_lms.dto.outDTO.UserReportOutDTO;
import com.nt.user_service_lms.repository.UserReportRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserReportServiceImpl {

    private final UserReportRepository repository;

    public UserReportServiceImpl(UserReportRepository repository) {
        this.repository = repository;
    }

    public Map<String, Object> getAllUserKpis(int page, int size) {
        int offset = page * size;
        List<UserReportOutDTO> records = repository.fetchUserKpiReport(size, offset);
        long total = repository.getTotalUserCount();

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", total);
        return result;
    }


}
