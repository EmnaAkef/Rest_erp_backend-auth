package com.rest_erp.backend_bi_rest_erp.service.sales;

import com.rest_erp.backend_bi_rest_erp.dto.sales.*;
import com.rest_erp.backend_bi_rest_erp.repository.CommonRepository;
import com.rest_erp.backend_bi_rest_erp.repository.sales.SalesKpiRepository;
import com.rest_erp.backend_bi_rest_erp.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SalesKpiService {

    private final SalesKpiRepository salesKpiRepository;
    private final CommonRepository commonRepository;

    public SalesKpiResponse getSalesKpis(LocalDate startDate, LocalDate endDate, SalesFilterRequest filters) {

        Integer companyKey = TenantContext.getCompanyKey();


        String currency = commonRepository.getCompanyCurrency(companyKey);

        BigDecimal totalRevenue = salesKpiRepository.getTotalRevenue(companyKey, startDate, endDate, filters);
        Long numberOfDeals = salesKpiRepository.getNumberOfDeals(companyKey, startDate, endDate, filters);
        BigDecimal winRate = salesKpiRepository.getWinRate(companyKey, startDate, endDate, filters);
        BigDecimal averageDealValue = salesKpiRepository.getAverageDealValue(companyKey, startDate, endDate, filters);
        Long salesOrdersCount = salesKpiRepository.getSalesOrdersCount(companyKey, startDate, endDate, filters);
        BigDecimal outstandingReceivables = salesKpiRepository.getOutstandingReceivables(companyKey, startDate, endDate, filters);
        Long pipelineDealsCount = salesKpiRepository.getPipelineDealsCount(
                companyKey,
                startDate,
                endDate,
                filters
        );

        BigDecimal pipelineValue = salesKpiRepository.getPipelineValue(
                companyKey,
                startDate,
                endDate,
                filters
        );

        Long activeCustomers = salesKpiRepository.getActiveCustomers(companyKey, startDate, endDate, filters);
        Long totalCustomers = salesKpiRepository.getTotalCustomers(companyKey, filters);

        Long inactiveCustomers = Math.max(
                0L,
                (totalCustomers != null ? totalCustomers : 0L) - (activeCustomers != null ? activeCustomers : 0L)
        );

        BigDecimal averageCustomerValue = BigDecimal.ZERO;
        if (activeCustomers != null && activeCustomers > 0) {
            averageCustomerValue = totalRevenue.divide(
                    BigDecimal.valueOf(activeCustomers),
                    2,
                    java.math.RoundingMode.HALF_UP
            );
        }

        BigDecimal conversionRate = salesKpiRepository.getConversionRate(companyKey, startDate, endDate, filters);

        return SalesKpiResponse.builder()
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .numberOfDeals(numberOfDeals != null ? numberOfDeals : 0L)
                .winRate(winRate != null ? winRate : BigDecimal.ZERO)
                .averageDealValue(averageDealValue != null ? averageDealValue : BigDecimal.ZERO)
                .salesOrdersCount(salesOrdersCount != null ? salesOrdersCount : 0L)
                .outstandingReceivables(outstandingReceivables != null ? outstandingReceivables : BigDecimal.ZERO)
                .pipelineDealsCount(pipelineDealsCount != null ? pipelineDealsCount : 0L)
                .pipelineValue(pipelineValue != null ? pipelineValue : BigDecimal.ZERO)
                .activeCustomers(activeCustomers != null ? activeCustomers : 0L)
                .inactiveCustomers(inactiveCustomers)
                .averageCustomerValue(averageCustomerValue)
                .conversionRate(conversionRate != null ? conversionRate : BigDecimal.ZERO)
                .currency(currency)
                .build();
    }

    public List<SalesRevenueTrendItem> getRevenueTrend(
            LocalDate startDate,
            LocalDate endDate,
            SalesFilterRequest filters
    ) {
        Integer companyKey = TenantContext.getCompanyKey();

        List<Object[]> rows = salesKpiRepository.getRevenueTrend(
                companyKey,
                startDate,
                endDate,
                filters
        );

        List<SalesRevenueTrendItem> result = new ArrayList<>();

        for (Object[] row : rows) {
            String label = row[0] != null ? row[0].toString() : "";
            BigDecimal value = row[1] != null ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO;

            result.add(
                    SalesRevenueTrendItem.builder()
                            .label(label)
                            .value(value)
                            .build()
            );
        }

        return result;
    }

    public List<Map<String, Object>> getPipelineDistribution(
            LocalDate startDate,
            LocalDate endDate,
            SalesFilterRequest filters
    ) {
        Integer companyKey = TenantContext.getCompanyKey();

        List<Object[]> rows = salesKpiRepository.getPipelineDistribution(
                companyKey,
                startDate,
                endDate,
                filters
        );

        return rows.stream()
                .map(row -> {
                    Map<String, Object> map = new HashMap<>();

                    map.put("status", row[0]);
                    map.put("count", row[1] != null ? ((Number) row[1]).longValue() : 0);

                    return map;
                })
                .toList();
    }

    public List<RecentSalesOrderItem> getRecentSalesOrders(
            LocalDate startDate,
            LocalDate endDate,
            SalesFilterRequest filters
    ) {
        Integer companyKey = TenantContext.getCompanyKey();
        String currency = commonRepository.getCompanyCurrency(companyKey);

        List<Object[]> rows = salesKpiRepository.getRecentSalesOrders(
                companyKey,
                startDate,
                endDate,
                filters
        );

        List<RecentSalesOrderItem> result = new ArrayList<>();

        for (Object[] row : rows) {
            result.add(
                    RecentSalesOrderItem.builder()
                            .id(row[0] != null ? row[0].toString() : "")
                            .customer(row[1] != null ? row[1].toString() : "Unknown Customer")
                            .date(row[2] != null ? LocalDate.parse(row[2].toString()) : null)
                            .amount(row[3] != null ? new BigDecimal(row[3].toString()) : BigDecimal.ZERO)
                            .status(row[4] != null ? row[4].toString() : "")
                            .currency(currency)
                            .build()
            );
        }

        return result;
    }

    public List<TopSalespersonItem> getTopSalespersons(
            LocalDate startDate,
            LocalDate endDate,
            SalesFilterRequest filters
    ) {
        Integer companyKey = TenantContext.getCompanyKey();
        String currency = commonRepository.getCompanyCurrency(companyKey);

        List<Object[]> rows = salesKpiRepository.getTopSalespersons(
                companyKey,
                startDate,
                endDate,
                filters
        );

        List<TopSalespersonItem> result = new ArrayList<>();

        for (Object[] row : rows) {
            result.add(
                    TopSalespersonItem.builder()
                            .name(row[0] != null ? row[0].toString() : "Unknown Salesperson")
                            .amount(row[1] != null ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO)
                            .currency(currency)
                            .build()
            );
        }

        return result;
    }

    public List<RevenueByCustomerItem> getRevenueByCustomer(
            LocalDate startDate,
            LocalDate endDate,
            SalesFilterRequest filters
    ) {
        Integer companyKey = TenantContext.getCompanyKey();

        List<Object[]> rows = salesKpiRepository.getRevenueByCustomer(
                companyKey,
                startDate,
                endDate,
                filters
        );

        List<RevenueByCustomerItem> result = new ArrayList<>();

        for (Object[] row : rows) {
            result.add(
                    RevenueByCustomerItem.builder()
                            .name(row[0] != null ? row[0].toString() : "Unknown Customer")
                            .amount(row[1] != null ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO)
                            .build()
            );
        }

        return result;
    }

    public List<RevenueByProductItem> getRevenueByProduct(
            LocalDate startDate,
            LocalDate endDate,
            SalesFilterRequest filters
    ) {
        Integer companyKey = TenantContext.getCompanyKey();

        List<Object[]> rows = salesKpiRepository.getRevenueByProduct(
                companyKey,
                startDate,
                endDate,
                filters
        );

        List<RevenueByProductItem> result = new ArrayList<>();

        for (Object[] row : rows) {
            result.add(
                    RevenueByProductItem.builder()
                            .name(row[0] != null ? row[0].toString() : "Unknown Product")
                            .amount(row[1] != null ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO)
                            .build()
            );
        }

        return result;
    }

    public List<Map<String, Object>> getCustomerRetention(
            LocalDate startDate,
            LocalDate endDate,
            SalesFilterRequest filters
    ) {
        Integer companyKey = TenantContext.getCompanyKey();

        List<Object[]> rows = salesKpiRepository.getCustomerRetention(
                companyKey,
                startDate,
                endDate,
                filters
        );

        return rows.stream()
                .map(row -> {
                    Map<String, Object> map = new HashMap<>();

                    map.put("label", row[0]);
                    map.put("value", row[1] != null ? ((Number) row[1]).doubleValue() : 0);

                    return map;
                })
                .toList();
    }

    public List<Map<String, Object>> getHighValueDeals(
            LocalDate startDate,
            LocalDate endDate,
            SalesFilterRequest filters
    ) {
        Integer companyKey = TenantContext.getCompanyKey();

        List<Object[]> rows = salesKpiRepository.getHighValueDeals(
                companyKey,
                startDate,
                endDate,
                filters
        );

        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] row : rows) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", "Deal #" + row[0]);
            item.put("value", row[1]);
            result.add(item);
        }

        return result;
    }

    public List<SalesFilterOption> getCustomerOptions() {
        Integer companyKey = TenantContext.getCompanyKey();

        List<Object[]> rows = salesKpiRepository.getCustomerOptions(companyKey);

        List<SalesFilterOption> result = new ArrayList<>();

        for (Object[] row : rows) {
            result.add(
                    SalesFilterOption.builder()
                            .value(row[0])
                            .label(row[1] != null ? row[1].toString() : "Unknown Customer")
                            .build()
            );
        }

        return result;
    }

    public List<SalesFilterOption> getProductOptions() {
        Integer companyKey = TenantContext.getCompanyKey();

        List<Object[]> rows = salesKpiRepository.getProductOptions(companyKey);

        List<SalesFilterOption> result = new ArrayList<>();

        for (Object[] row : rows) {
            result.add(
                    SalesFilterOption.builder()
                            .value(row[0])
                            .label(row[1] != null ? row[1].toString() : "Unknown Product")
                            .build()
            );
        }

        return result;
    }

    public List<SalesFilterOption> getSalespersonOptions() {
        Integer companyKey = TenantContext.getCompanyKey();

        List<Object[]> rows = salesKpiRepository.getSalespersonOptions(companyKey);

        List<SalesFilterOption> result = new ArrayList<>();

        for (Object[] row : rows) {
            result.add(
                    SalesFilterOption.builder()
                            .value(row[0])
                            .label(row[1] != null ? row[1].toString() : "Unknown Salesperson")
                            .build()
            );
        }

        return result;
    }

    public List<SalesFilterOption> getWorkstatusOptions() {
        Integer companyKey = TenantContext.getCompanyKey();

        List<String> rows = salesKpiRepository.getWorkstatusOptions(companyKey);

        List<SalesFilterOption> result = new ArrayList<>();

        for (String statusLabel : rows) {
            result.add(
                    SalesFilterOption.builder()
                            .value(statusLabel)
                            .label(statusLabel)
                            .build()
            );
        }

        return result;
    }

    public List<SalesFilterOption> getCustomerCategoryOptions() {
        Integer companyKey = TenantContext.getCompanyKey();

        List<String> rows = salesKpiRepository.getCustomerCategoryOptions(companyKey);

        List<SalesFilterOption> result = new ArrayList<>();

        for (String category : rows) {
            result.add(
                    SalesFilterOption.builder()
                            .value(category)
                            .label(category)
                            .build()
            );
        }

        return result;
    }

    public List<SalesFilterOption> getProductCategoryOptions() {
        Integer companyKey = TenantContext.getCompanyKey();

        List<String> rows = salesKpiRepository.getProductCategoryOptions(companyKey);

        List<SalesFilterOption> result = new ArrayList<>();

        for (String category : rows) {
            result.add(
                    SalesFilterOption.builder()
                            .value(category)
                            .label(category)
                            .build()
            );
        }

        return result;
    }


}