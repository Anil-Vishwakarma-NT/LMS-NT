package com.nt.user_service_lms.service.serviceImpl;

import com.nt.user_service_lms.dto.outDTO.BundleReportOutDTO;
import com.nt.user_service_lms.repository.BundleReportRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BundleReportServiceImpl {

    private final BundleReportRepository repository;

    public BundleReportServiceImpl(BundleReportRepository repository) {
        this.repository = repository;
    }

    public Map<String, Object> getAllBundleKpis(int page, int size) {
        int offset = page * size;
        List<BundleReportOutDTO> records = repository.fetchBundleKpiReport(size, offset);
        long total = repository.getTotalBundleCount();

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", total);
        return result;
    }
}
