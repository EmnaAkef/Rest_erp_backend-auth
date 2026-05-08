package com.rest_erp.backend_bi_rest_erp.service.finance;

import com.rest_erp.backend_bi_rest_erp.dto.finance.FinanceKpiResponse;
import com.rest_erp.backend_bi_rest_erp.repository.finance.FinanceKpiRepository;
import com.rest_erp.backend_bi_rest_erp.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import com.rest_erp.backend_bi_rest_erp.dto.finance.FinanceRevenueProfitTrendItem;
import com.rest_erp.backend_bi_rest_erp.dto.finance.FinanceCashFlowTrendItem;
import com.rest_erp.backend_bi_rest_erp.dto.finance.FinanceOutstandingInvoiceItem;
import com.rest_erp.backend_bi_rest_erp.dto.finance.FinanceLiabilityAssetItem;
import com.rest_erp.backend_bi_rest_erp.dto.finance.FinanceAssetDistributionItem;
import java.util.List;
import com.rest_erp.backend_bi_rest_erp.repository.CommonRepository;
@Service
@RequiredArgsConstructor
public class FinanceKpiService {

    private final CommonRepository commonRepository;
    private final FinanceKpiRepository financeKpiRepository;



    public FinanceKpiResponse getFinanceKpis(LocalDate startDate, LocalDate endDate) {

        Integer companyKey = TenantContext.getCompanyKey();

        String currency = commonRepository.getCompanyCurrency(companyKey);

        Integer startDateKey = toDateKey(startDate);
        Integer endDateKey = toDateKey(endDate);

        BigDecimal totalRevenue = financeKpiRepository.getTotalRevenue(companyKey, startDateKey, endDateKey);
        BigDecimal totalExpenses = financeKpiRepository.getTotalExpenses(companyKey, startDateKey, endDateKey);

        BigDecimal netProfit = totalRevenue.subtract(totalExpenses);
        BigDecimal grossMarginPercentage = calculateGrossMargin(totalRevenue, totalExpenses);

        BigDecimal cashBalance = financeKpiRepository.getCashBalance(companyKey, startDateKey, endDateKey);
        BigDecimal bankAccountBalance = financeKpiRepository.getBankAccountBalance(companyKey, startDateKey, endDateKey);
        BigDecimal totalLiabilities = financeKpiRepository.getTotalLiabilities(companyKey, startDateKey, endDateKey);

        BigDecimal accountsReceivable = financeKpiRepository.getAccountsReceivable(companyKey, startDateKey, endDateKey);
        BigDecimal accountsPayable = financeKpiRepository.getAccountsPayable(companyKey, startDateKey, endDateKey);

        Integer numberOfOpenInvoices = financeKpiRepository.getNumberOfOpenInvoices(companyKey, startDateKey, endDateKey);
        Integer dueInvoices = financeKpiRepository.getDueInvoices(companyKey, startDateKey, endDateKey);

        BigDecimal assetValue = financeKpiRepository.getAssetValue(companyKey, startDateKey, endDateKey);
        BigDecimal depreciationExpense = financeKpiRepository.getDepreciationExpense(companyKey, startDateKey, endDateKey);

        BigDecimal vatCollected = financeKpiRepository.getVatCollected(companyKey, startDateKey, endDateKey);
        BigDecimal vatFromBills = financeKpiRepository.getVatFromBills(companyKey, startDateKey, endDateKey);
        BigDecimal vatPayable = vatCollected.subtract(vatFromBills);

        BigDecimal currentLiabilities = financeKpiRepository.getCurrentLiabilities(
                companyKey,
                startDateKey,
                endDateKey
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

    public List<FinanceRevenueProfitTrendItem> getRevenueProfitTrend(LocalDate startDate, LocalDate endDate) {
        Integer companyKey = TenantContext.getCompanyKey();

        Integer startDateKey = toDateKey(startDate);
        Integer endDateKey = toDateKey(endDate);

        return financeKpiRepository.getRevenueProfitTrend(companyKey, startDateKey, endDateKey);
    }
    public List<FinanceCashFlowTrendItem> getCashFlowTrend(LocalDate startDate, LocalDate endDate) {
        Integer companyKey = TenantContext.getCompanyKey();

        Integer startDateKey = toDateKey(startDate);
        Integer endDateKey = toDateKey(endDate);

        return financeKpiRepository.getCashFlowTrend(companyKey, startDateKey, endDateKey);
    }
    public List<FinanceOutstandingInvoiceItem> getTopOutstandingInvoices(LocalDate startDate, LocalDate endDate) {
        Integer companyKey = TenantContext.getCompanyKey();

        Integer startDateKey = toDateKey(startDate);
        Integer endDateKey = toDateKey(endDate);

        return financeKpiRepository.getTopOutstandingInvoices(companyKey, startDateKey, endDateKey);
    }
    public FinanceLiabilityAssetItem getLiabilityVsAssets(LocalDate startDate, LocalDate endDate) {
        Integer companyKey = TenantContext.getCompanyKey();

        Integer startDateKey = toDateKey(startDate);
        Integer endDateKey = toDateKey(endDate);

        return financeKpiRepository.getLiabilityVsAssets(companyKey, startDateKey, endDateKey);
    }
    public List<FinanceAssetDistributionItem> getAssetDistribution(LocalDate endDate) {
        Integer companyKey = TenantContext.getCompanyKey();
        Integer endDateKey = toDateKey(endDate);

        return financeKpiRepository.getAssetDistribution(companyKey, endDateKey);
    }
}