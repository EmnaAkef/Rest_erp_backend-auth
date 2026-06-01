package com.rest_erp.backend_bi_rest_erp.bi.service.finance;

import com.rest_erp.backend_bi_rest_erp.bi.dto.finance.FinanceAssetDistributionItem;
import com.rest_erp.backend_bi_rest_erp.bi.dto.finance.FinanceCashFlowTrendItem;
import com.rest_erp.backend_bi_rest_erp.bi.dto.finance.FinanceFilingDateItem;
import com.rest_erp.backend_bi_rest_erp.bi.dto.finance.FinanceFilterOptionsResponse;
import com.rest_erp.backend_bi_rest_erp.bi.dto.finance.FinanceFilterRequest;
import com.rest_erp.backend_bi_rest_erp.bi.dto.finance.FinanceKpiResponse;
import com.rest_erp.backend_bi_rest_erp.bi.dto.finance.FinanceLiabilityAssetItem;
import com.rest_erp.backend_bi_rest_erp.bi.dto.finance.FinanceOutstandingInvoiceItem;
import com.rest_erp.backend_bi_rest_erp.bi.dto.finance.FinanceRevenueProfitTrendItem;
import com.rest_erp.backend_bi_rest_erp.bi.dto.finance.FinanceTaxPaymentItem;
import com.rest_erp.backend_bi_rest_erp.bi.repository.CommonRepository;
import com.rest_erp.backend_bi_rest_erp.bi.repository.finance.FinanceKpiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import static com.rest_erp.backend_bi_rest_erp.bi.tenant.TenantContext.getCompanyKey;

@Service
@RequiredArgsConstructor
public class FinanceKpiService {

    private final CommonRepository commonRepository;
    private final FinanceKpiRepository financeKpiRepository;

    public FinanceKpiResponse getFinanceKpis(LocalDate startDate, LocalDate endDate, FinanceFilterRequest filters) {

        Integer companyKey = getCompanyKey();

        String currency = commonRepository.getCompanyCurrency(companyKey);

        Integer startDateKey = toDateKey(startDate);
        Integer endDateKey = toDateKey(endDate);

        BigDecimal totalRevenue = financeKpiRepository.getTotalRevenue(companyKey, startDateKey, endDateKey, filters);
        BigDecimal totalExpenses = financeKpiRepository.getTotalExpenses(companyKey, startDateKey, endDateKey, filters);

        BigDecimal netProfit = totalRevenue.subtract(totalExpenses);
        BigDecimal grossMarginPercentage = calculateGrossMargin(totalRevenue, totalExpenses);

        BigDecimal cashBalance = financeKpiRepository.getCashBalance(companyKey, startDateKey, endDateKey, filters);
        BigDecimal bankAccountBalance = financeKpiRepository.getBankAccountBalance(companyKey, startDateKey, endDateKey, filters);
        BigDecimal totalLiabilities = financeKpiRepository.getTotalLiabilities(companyKey, startDateKey, endDateKey, filters);

        BigDecimal accountsReceivable = financeKpiRepository.getAccountsReceivable(companyKey, startDateKey, endDateKey, filters);
        BigDecimal accountsPayable = financeKpiRepository.getAccountsPayable(companyKey, startDateKey, endDateKey, filters);

        Integer numberOfOpenInvoices = financeKpiRepository.getNumberOfOpenInvoices(companyKey, startDateKey, endDateKey, filters);
        Integer dueInvoices = financeKpiRepository.getDueInvoices(companyKey, startDateKey, endDateKey, filters);

        BigDecimal assetValue = financeKpiRepository.getAssetValue(companyKey, startDateKey, endDateKey, filters);
        BigDecimal depreciationExpense = financeKpiRepository.getDepreciationExpense(companyKey, startDateKey, endDateKey, filters);

        BigDecimal vatCollected = financeKpiRepository.getVatCollected(companyKey, startDateKey, endDateKey, filters);
        BigDecimal vatFromBills = financeKpiRepository.getVatFromBills(companyKey, startDateKey, endDateKey, filters);
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

    public List<FinanceRevenueProfitTrendItem> getRevenueProfitTrend(LocalDate startDate, LocalDate endDate, FinanceFilterRequest filters) {
        Integer companyKey = getCompanyKey();

        Integer startDateKey = toDateKey(startDate);
        Integer endDateKey = toDateKey(endDate);

        return financeKpiRepository.getRevenueProfitTrend(companyKey, startDateKey, endDateKey, filters);
    }
    public List<FinanceCashFlowTrendItem> getCashFlowTrend(LocalDate startDate, LocalDate endDate, FinanceFilterRequest filters) {
        Integer companyKey = getCompanyKey();

        Integer startDateKey = toDateKey(startDate);
        Integer endDateKey = toDateKey(endDate);

        return financeKpiRepository.getCashFlowTrend(companyKey, startDateKey, endDateKey, filters);
    }
    public List<FinanceOutstandingInvoiceItem> getTopOutstandingInvoices(LocalDate startDate, LocalDate endDate, FinanceFilterRequest filters) {
        Integer companyKey = getCompanyKey();

        Integer startDateKey = toDateKey(startDate);
        Integer endDateKey = toDateKey(endDate);

        return financeKpiRepository.getTopOutstandingInvoices(companyKey, startDateKey, endDateKey, filters);
    }
    public FinanceLiabilityAssetItem getLiabilityVsAssets(LocalDate startDate, LocalDate endDate, FinanceFilterRequest filters) {
        Integer companyKey = getCompanyKey();

        Integer startDateKey = toDateKey(startDate);
        Integer endDateKey = toDateKey(endDate);

        return financeKpiRepository.getLiabilityVsAssets(companyKey, startDateKey, endDateKey, filters);
    }
    public List<FinanceAssetDistributionItem> getAssetDistribution(LocalDate endDate, FinanceFilterRequest filters) {
        Integer companyKey = getCompanyKey();
        Integer endDateKey = toDateKey(endDate);

        return financeKpiRepository.getAssetDistribution(companyKey, endDateKey, filters);
    }

    public List<FinanceTaxPaymentItem> getRecentTaxPayments(LocalDate startDate, LocalDate endDate, FinanceFilterRequest filters) {
        Integer companyKey = getCompanyKey();

        return financeKpiRepository.getRecentTaxPayments(
                companyKey,
                toDateKey(startDate),
                toDateKey(endDate),
                filters
        );
    }

    public List<FinanceFilingDateItem> getNextFilingDates() {
        return financeKpiRepository.getNextFilingDates();
    }

    public FinanceFilterOptionsResponse getFinanceFilterOptions() {
        Integer companyKey = getCompanyKey();
        return financeKpiRepository.getFinanceFilterOptions(companyKey);
    }
    public List<String> searchFinanceFilterOptions(String field, String q) {
        Integer companyKey = getCompanyKey();

        return financeKpiRepository.searchFinanceFilterOptions(
                companyKey,
                field,
                q == null ? "" : q.trim()
        );
    }
}
