package com.nt.user_service_lms.service.serviceImpl;

import com.nt.user_service_lms.dto.outDTO.GroupReportOutDTO;
import com.nt.user_service_lms.repository.GroupReportRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GroupReportServiceImpl {

    private final GroupReportRepository repository;

    public GroupReportServiceImpl(GroupReportRepository repository) {
        this.repository = repository;
    }

    public Map<String, Object> getAllGroupKpis(int page, int size) {
        int offset = page * size;
        List<GroupReportOutDTO> records = repository.fetchGroupKpiReport(size, offset);
        long total = repository.getTotalGroupCount();

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", total);
        return result;
    }

}
