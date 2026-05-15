package com.rest_erp.backend_bi_rest_erp.service.finance;

import com.rest_erp.backend_bi_rest_erp.dto.finance.*;
import com.rest_erp.backend_bi_rest_erp.repository.CommonRepository;
import com.rest_erp.backend_bi_rest_erp.repository.finance.FinanceKpiRepository;
import com.rest_erp.backend_bi_rest_erp.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
@Service
@RequiredArgsConstructor
public class FinanceKpiService {

    private final CommonRepository commonRepository;
    private final FinanceKpiRepository financeKpiRepository;
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public FinanceKpiResponse getFinanceKpis(
            LocalDate startDate,
            LocalDate endDate,
            FinanceFilterRequest filters
    ) {
        Integer companyKey = TenantContext.getCompanyKey();

        String currency = commonRepository.getCompanyCurrency(companyKey);

        Integer startDateKey = toDateKey(startDate);
        Integer endDateKey = toDateKey(endDate);

        BigDecimal totalRevenue = financeKpiRepository.getTotalRevenue(
                companyKey,
                startDateKey,
                endDateKey,
                filters
        );

        BigDecimal totalExpenses = financeKpiRepository.getTotalExpenses(
                companyKey,
                startDateKey,
                endDateKey,
                filters
        );

        BigDecimal netProfit = totalRevenue.subtract(totalExpenses);
        BigDecimal grossMarginPercentage = calculateGrossMargin(totalRevenue, totalExpenses);

        BigDecimal cashBalance = financeKpiRepository.getCashBalance(
                companyKey,
                startDateKey,
                endDateKey,
                filters
        );

        BigDecimal bankAccountBalance = financeKpiRepository.getBankAccountBalance(
                companyKey,
                startDateKey,
                endDateKey,
                filters
        );

        BigDecimal totalLiabilities = financeKpiRepository.getTotalLiabilities(
                companyKey,
                startDateKey,
                endDateKey,
                filters
        );

        BigDecimal accountsReceivable = financeKpiRepository.getAccountsReceivable(
                companyKey,
                startDateKey,
                endDateKey,
                filters
        );

        BigDecimal accountsPayable = financeKpiRepository.getAccountsPayable(
                companyKey,
                startDateKey,
                endDateKey,
                filters
        );

        Integer numberOfOpenInvoices = financeKpiRepository.getNumberOfOpenInvoices(
                companyKey,
                startDateKey,
                endDateKey,
                filters
        );

        Integer dueInvoices = financeKpiRepository.getDueInvoices(
                companyKey,
                startDateKey,
                endDateKey,
                filters
        );

        BigDecimal assetValue = financeKpiRepository.getAssetValue(
                companyKey,
                startDateKey,
                endDateKey,
                filters
        );

        BigDecimal depreciationExpense = financeKpiRepository.getDepreciationExpense(
                companyKey,
                startDateKey,
                endDateKey,
                filters
        );

        BigDecimal vatCollected = financeKpiRepository.getVatCollected(
                companyKey,
                startDateKey,
                endDateKey,
                filters
        );

        BigDecimal vatFromBills = financeKpiRepository.getVatFromBills(
                companyKey,
                startDateKey,
                endDateKey,
                filters
        );

        BigDecimal vatPayable = vatCollected.subtract(vatFromBills);

        BigDecimal currentLiabilities = financeKpiRepository.getCurrentLiabilities(
                companyKey,
                startDateKey,
                endDateKey,
                filters
        );

        BigDecimal liquidityRatio = calculateLiquidityRatio(
                cashBalance,
                accountsReceivable,
                currentLiabilities
        );

        return FinanceKpiResponse.builder()
                .currency(currency)
                .totalRevenue(totalRevenue)
                .totalExpenses(totalExpenses)
                .netProfit(netProfit)
                .grossMarginPercentage(grossMarginPercentage)
                .cashBalance(cashBalance)
                .bankAccountBalance(bankAccountBalance)
                .totalLiabilities(totalLiabilities)
                .liquidityRatio(liquidityRatio)
                .accountsReceivable(accountsReceivable)
                .accountsPayable(accountsPayable)
                .numberOfOpenInvoices(numberOfOpenInvoices)
                .dueInvoices(dueInvoices)
                .assetValue(assetValue)
                .depreciationExpense(depreciationExpense)
                .vatCollected(vatCollected)
                .vatPayable(vatPayable)
                .build();
    }

    public List<FinanceRevenueProfitTrendItem> getRevenueProfitTrend(
            LocalDate startDate,
            LocalDate endDate,
            FinanceFilterRequest filters
    ) {
        Integer companyKey = TenantContext.getCompanyKey();

        Integer startDateKey = toDateKey(startDate);
        Integer endDateKey = toDateKey(endDate);

        return financeKpiRepository.getRevenueProfitTrend(
                companyKey,
                startDateKey,
                endDateKey,
                filters
        );
    }

    public List<FinanceCashFlowTrendItem> getCashFlowTrend(
            LocalDate startDate,
            LocalDate endDate,
            FinanceFilterRequest filters
    ) {
        Integer companyKey = TenantContext.getCompanyKey();

        Integer startDateKey = toDateKey(startDate);
        Integer endDateKey = toDateKey(endDate);

        return financeKpiRepository.getCashFlowTrend(
                companyKey,
                startDateKey,
                endDateKey,
                filters
        );
    }

    public List<FinanceOutstandingInvoiceItem> getTopOutstandingInvoices(
            LocalDate startDate,
            LocalDate endDate,
            FinanceFilterRequest filters
    ) {
        Integer companyKey = TenantContext.getCompanyKey();

        Integer startDateKey = toDateKey(startDate);
        Integer endDateKey = toDateKey(endDate);

        return financeKpiRepository.getTopOutstandingInvoices(
                companyKey,
                startDateKey,
                endDateKey,
                filters
        );
    }

    public FinanceLiabilityAssetItem getLiabilityVsAssets(
            LocalDate startDate,
            LocalDate endDate,
            FinanceFilterRequest filters
    ) {
        Integer companyKey = TenantContext.getCompanyKey();

        Integer startDateKey = toDateKey(startDate);
        Integer endDateKey = toDateKey(endDate);

        return financeKpiRepository.getLiabilityVsAssets(
                companyKey,
                startDateKey,
                endDateKey,
                filters
        );
    }

    public List<FinanceAssetDistributionItem> getAssetDistribution(
            LocalDate endDate,
            FinanceFilterRequest filters
    ) {
        Integer companyKey = TenantContext.getCompanyKey();
        Integer endDateKey = toDateKey(endDate);

        return financeKpiRepository.getAssetDistribution(
                companyKey,
                endDateKey,
                filters
        );
    }

    public FinanceComplianceSummaryResponse getComplianceSummary(
            LocalDate startDate,
            LocalDate endDate,
            FinanceFilterRequest filters
    ) {
        Integer companyKey = TenantContext.getCompanyKey();

        Integer startDateKey = toDateKey(startDate);
        Integer endDateKey = toDateKey(endDate);

        return financeKpiRepository.getComplianceSummary(
                companyKey,
                startDateKey,
                endDateKey,
                filters
        );
    }

    private BigDecimal calculateGrossMargin(BigDecimal revenue, BigDecimal expenses) {
        if (revenue == null || revenue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return revenue.subtract(expenses)
                .divide(revenue, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateLiquidityRatio(
            BigDecimal cashBalance,
            BigDecimal accountsReceivable,
            BigDecimal totalLiabilities
    ) {
        if (totalLiabilities == null || totalLiabilities.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return cashBalance.add(accountsReceivable)
                .divide(totalLiabilities, 4, RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private Integer toDateKey(LocalDate date) {
        return date.getYear() * 10000
                + date.getMonthValue() * 100
                + date.getDayOfMonth();
    }
}