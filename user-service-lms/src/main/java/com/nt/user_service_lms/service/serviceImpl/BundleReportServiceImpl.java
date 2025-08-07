package com.nt.user_service_lms.service.serviceImpl;

import com.nt.user_service_lms.dto.outDTO.BundleReportOutDTO;
import com.nt.user_service_lms.repository.BundleReportRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service implementation for handling bundle KPI reports.
 * <p>
 * Provides methods to retrieve paginated bundle KPI (Key Performance Indicator)
 * data for reporting purposes in the LMS.
 * </p>
 *
 * @author
 */
@Service
public class BundleReportServiceImpl {

    /**
     * Repository used to fetch bundle report data from the database.
     */
    private final BundleReportRepository repository;

    /**
     * Constructor to initialize the {@link BundleReportRepository}.
     *
     * @param repository the repository used for bundle KPI data access
     */
    public BundleReportServiceImpl(final BundleReportRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieves paginated KPI data for all bundles.
     *
     * @param page the current page number (0-based index)
     * @param size the number of records per page
     * @return a map containing:
     *         <ul>
     *           <li><b>"records"</b> - list of {@link BundleReportOutDTO}</li>
     *           <li><b>"total"</b> - total count of bundles</li>
     *         </ul>
     */
    public Map<String, Object> getAllBundleKpis(final int page, final int size) {
        int offset = page * size;
        List<BundleReportOutDTO> records = repository.fetchBundleKpiReport(size, offset);
        long total = repository.getTotalBundleCount();

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", total);
        return result;
    }
}
