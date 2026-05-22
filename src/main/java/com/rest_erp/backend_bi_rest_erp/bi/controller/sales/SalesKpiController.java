package com.rest_erp.backend_bi_rest_erp.bi.controller.sales;

import com.rest_erp.backend_bi_rest_erp.bi.dto.sales.*;
import com.rest_erp.backend_bi_rest_erp.bi.service.sales.SalesKpiService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bi/sales")
@RequiredArgsConstructor
public class SalesKpiController {

    private final SalesKpiService salesKpiService;

    private SalesFilterRequest buildSalesFilters(
            Long customerId,
            String customerName,
            Integer productKey,
            Integer salespersonKey,
            String workstatusLabel,
            String customerCategory,
            String productCategory
    ) {
        SalesFilterRequest filters = new SalesFilterRequest();

        filters.setCustomerId(customerId);
        filters.setCustomerName(customerName);
        filters.setProductKey(productKey);
        filters.setSalespersonKey(salespersonKey);
        filters.setWorkstatusLabel(workstatusLabel);
        filters.setCustomerCategory(customerCategory);
        filters.setProductCategory(productCategory);

        return filters;
    }

    @GetMapping("/kpis")
    public SalesKpiResponse getSalesKpis(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) Integer productKey,
            @RequestParam(required = false) Integer salespersonKey,
            @RequestParam(required = false) String workstatusLabel,
            @RequestParam(required = false) String customerCategory,
            @RequestParam(required = false) String productCategory
    ) {
        SalesFilterRequest filters = buildSalesFilters(
                customerId,
                customerName,
                productKey,
                salespersonKey,
                workstatusLabel,
                customerCategory,
                productCategory
        );

        return salesKpiService.getSalesKpis(startDate, endDate, filters);
    }

    @GetMapping("/revenue-trend")
    public List<SalesRevenueTrendItem> getRevenueTrend(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) Integer productKey,
            @RequestParam(required = false) Integer salespersonKey,
            @RequestParam(required = false) String workstatusLabel,
            @RequestParam(required = false) String customerCategory,
            @RequestParam(required = false) String productCategory
    ) {
        SalesFilterRequest filters = buildSalesFilters(
                customerId,
                customerName,
                productKey,
                salespersonKey,
                workstatusLabel,
                customerCategory,
                productCategory
        );

        return salesKpiService.getRevenueTrend(startDate, endDate, filters);
    }

    @GetMapping("/pipeline-distribution")
    public ResponseEntity<List<Map<String, Object>>> getPipelineDistribution(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) Integer productKey,
            @RequestParam(required = false) Integer salespersonKey,
            @RequestParam(required = false) String workstatusLabel,
            @RequestParam(required = false) String customerCategory,
            @RequestParam(required = false) String productCategory
    ) {
        SalesFilterRequest filters = buildSalesFilters(
                customerId,
                customerName,
                productKey,
                salespersonKey,
                workstatusLabel,
                customerCategory,
                productCategory
        );

        return ResponseEntity.ok(
                salesKpiService.getPipelineDistribution(startDate, endDate, filters)
        );
    }

    @GetMapping("/recent-orders")
    public List<RecentSalesOrderItem> getRecentSalesOrders(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) Integer productKey,
            @RequestParam(required = false) Integer salespersonKey,
            @RequestParam(required = false) String workstatusLabel,
            @RequestParam(required = false) String customerCategory,
            @RequestParam(required = false) String productCategory
    ) {
        SalesFilterRequest filters = buildSalesFilters(
                customerId,
                customerName,
                productKey,
                salespersonKey,
                workstatusLabel,
                customerCategory,
                productCategory
        );

        return salesKpiService.getRecentSalesOrders(startDate, endDate, filters);
    }

    @GetMapping("/top-salespersons")
    public List<TopSalespersonItem> getTopSalespersons(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) Integer productKey,
            @RequestParam(required = false) Integer salespersonKey,
            @RequestParam(required = false) String workstatusLabel,
            @RequestParam(required = false) String customerCategory,
            @RequestParam(required = false) String productCategory
    ) {
        SalesFilterRequest filters = buildSalesFilters(
                customerId,
                customerName,
                productKey,
                salespersonKey,
                workstatusLabel,
                customerCategory,
                productCategory
        );

        return salesKpiService.getTopSalespersons(startDate, endDate, filters);
    }

    @GetMapping("/revenue-by-customer")
    public List<RevenueByCustomerItem> getRevenueByCustomer(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) Integer productKey,
            @RequestParam(required = false) Integer salespersonKey,
            @RequestParam(required = false) String workstatusLabel,
            @RequestParam(required = false) String customerCategory,
            @RequestParam(required = false) String productCategory
    ) {
        SalesFilterRequest filters = buildSalesFilters(
                customerId,
                customerName,
                productKey,
                salespersonKey,
                workstatusLabel,
                customerCategory,
                productCategory
        );

        return salesKpiService.getRevenueByCustomer(startDate, endDate, filters);
    }

    @GetMapping("/revenue-by-product")
    public List<RevenueByProductItem> getRevenueByProduct(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) Integer productKey,
            @RequestParam(required = false) Integer salespersonKey,
            @RequestParam(required = false) String workstatusLabel,
            @RequestParam(required = false) String customerCategory,
            @RequestParam(required = false) String productCategory
    ) {
        SalesFilterRequest filters = buildSalesFilters(
                customerId,
                customerName,
                productKey,
                salespersonKey,
                workstatusLabel,
                customerCategory,
                productCategory
        );

        return salesKpiService.getRevenueByProduct(startDate, endDate, filters);
    }

    @GetMapping("/customer-retention")
    public ResponseEntity<List<Map<String, Object>>> getCustomerRetention(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) Integer productKey,
            @RequestParam(required = false) Integer salespersonKey,
            @RequestParam(required = false) String workstatusLabel,
            @RequestParam(required = false) String customerCategory,
            @RequestParam(required = false) String productCategory
    ) {
        SalesFilterRequest filters = buildSalesFilters(
                customerId,
                customerName,
                productKey,
                salespersonKey,
                workstatusLabel,
                customerCategory,
                productCategory
        );

        return ResponseEntity.ok(
                salesKpiService.getCustomerRetention(startDate, endDate, filters)
        );
    }

    @GetMapping("/high-value-deals")
    public List<Map<String, Object>> getHighValueDeals(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) Integer productKey,
            @RequestParam(required = false) Integer salespersonKey,
            @RequestParam(required = false) String workstatusLabel,
            @RequestParam(required = false) String customerCategory,
            @RequestParam(required = false) String productCategory
    ) {
        SalesFilterRequest filters = buildSalesFilters(
                customerId,
                customerName,
                productKey,
                salespersonKey,
                workstatusLabel,
                customerCategory,
                productCategory
        );

        return salesKpiService.getHighValueDeals(startDate, endDate, filters);
    }

    @GetMapping("/filter-options/customers")
    public List<SalesFilterOption> getCustomerOptions() {
        return salesKpiService.getCustomerOptions();
    }

    @GetMapping("/filter-options/products")
    public List<SalesFilterOption> getProductOptions() {
        return salesKpiService.getProductOptions();
    }

    @GetMapping("/filter-options/salespersons")
    public List<SalesFilterOption> getSalespersonOptions() {
        return salesKpiService.getSalespersonOptions();
    }

    @GetMapping("/filter-options/workstatus")
    public List<SalesFilterOption> getWorkstatusOptions() {
        return salesKpiService.getWorkstatusOptions();
    }

    @GetMapping("/filter-options/customer-categories")
    public List<SalesFilterOption> getCustomerCategoryOptions() {
        return salesKpiService.getCustomerCategoryOptions();
    }

    @GetMapping("/filter-options/product-categories")
    public List<SalesFilterOption> getProductCategoryOptions() {
        return salesKpiService.getProductCategoryOptions();
    }
}