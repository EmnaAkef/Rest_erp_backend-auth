package com.rest_erp.backend_bi_rest_erp.service.overview;

import com.rest_erp.backend_bi_rest_erp.dto.overview.OverviewKpiResponse;
import com.rest_erp.backend_bi_rest_erp.repository.overview.OverviewKpiRepository;
import com.rest_erp.backend_bi_rest_erp.tenant.TenantContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.rest_erp.backend_bi_rest_erp.dto.overview.OverviewFinancialTrendItem;
import java.util.List;
import com.rest_erp.backend_bi_rest_erp.dto.overview.OverviewFinancialTrendItem;
import com.rest_erp.backend_bi_rest_erp.dto.overview.OverviewCashSummaryItem;
@Service
public class OverviewKpiService {

    private final OverviewKpiRepository overviewKpiRepository;

    public OverviewKpiService(OverviewKpiRepository overviewKpiRepository) {
        this.overviewKpiRepository = overviewKpiRepository;
    }

    public OverviewKpiResponse getOverviewKpis(LocalDate startDate, LocalDate endDate) {
        Integer companyKey = TenantContext.getCompanyKey();

        Integer startDateKey = toDateKey(startDate);
        Integer endDateKey = toDateKey(endDate);

        Integer totalEmployees = overviewKpiRepository.getTotalEmployees(companyKey);

        BigDecimal presenceRate = overviewKpiRepository.getPresenceRate(
                companyKey,
                startDateKey,
                endDateKey
        );

        BigDecimal totalRevenue = overviewKpiRepository.getTotalRevenue(
                companyKey,
                startDateKey,
                endDateKey
        );

        BigDecimal totalExpenses = overviewKpiRepository.getTotalExpenses(
                companyKey,
                startDateKey,
                endDateKey
        );

        BigDecimal netProfit = totalRevenue.subtract(totalExpenses);

        BigDecimal winRate = overviewKpiRepository.getWinRate(
                companyKey,
                startDateKey,
                endDateKey
        );

        BigDecimal pipelineValue = overviewKpiRepository.getPipelineValue(companyKey);

        return new OverviewKpiResponse(
                totalEmployees,
                presenceRate,
                totalRevenue,
                netProfit,
                winRate,
                pipelineValue
        );
    }

    private Integer toDateKey(LocalDate date) {
        return date.getYear() * 10000
                + date.getMonthValue() * 100
                + date.getDayOfMonth();
    }

    public List<OverviewFinancialTrendItem> getFinancialTrend(LocalDate startDate, LocalDate endDate) {
        Integer companyKey = TenantContext.getCompanyKey();

        Integer startDateKey = toDateKey(startDate);
        Integer endDateKey = toDateKey(endDate);

        return overviewKpiRepository.getFinancialTrend(companyKey, startDateKey, endDateKey);
    }
    public OverviewCashSummaryItem getCashSummary(LocalDate startDate, LocalDate endDate) {
        Integer companyKey = TenantContext.getCompanyKey();

        Integer startDateKey = toDateKey(startDate);
        Integer endDateKey = toDateKey(endDate);

        return overviewKpiRepository.getCashSummary(companyKey, startDateKey, endDateKey);
    }
}