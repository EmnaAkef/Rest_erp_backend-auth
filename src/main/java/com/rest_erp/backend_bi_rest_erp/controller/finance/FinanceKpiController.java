package com.rest_erp.backend_bi_rest_erp.controller.finance;

import com.rest_erp.backend_bi_rest_erp.dto.finance.*;
import com.rest_erp.backend_bi_rest_erp.service.finance.FinanceKpiService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bi/finance")
@CrossOrigin(origins = "*")
public class FinanceKpiController {

    private final FinanceKpiService financeKpiService;

    public FinanceKpiController(FinanceKpiService financeKpiService) {
        this.financeKpiService = financeKpiService;
    }

    private FinanceFilterRequest buildFilters(
            String customerName,
            String customerCategory,
            String vendorName,
            String vendorIndustry,
            String invoiceStatus,
            String statusGroup,
            String accountName,
            String accountType,
            String transactionType,
            String assetType,
            BigDecimal minAmount,
            BigDecimal maxAmount
    ) {
        return FinanceFilterRequest.builder()
                .customerName(customerName)
                .customerCategory(customerCategory)
                .vendorName(vendorName)
                .vendorIndustry(vendorIndustry)
                .invoiceStatus(invoiceStatus)
                .statusGroup(statusGroup)
                .accountName(accountName)
                .accountType(accountType)
                .transactionType(transactionType)
                .assetType(assetType)
                .minAmount(minAmount)
                .maxAmount(maxAmount)
                .build();
    }

    @GetMapping("/kpis")
    public ResponseEntity<FinanceKpiResponse> getFinanceKpis(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String customerCategory,
            @RequestParam(required = false) String vendorName,
            @RequestParam(required = false) String vendorIndustry,
            @RequestParam(required = false) String invoiceStatus,
            @RequestParam(required = false) String statusGroup,
            @RequestParam(required = false) String accountName,
            @RequestParam(required = false) String accountType,
            @RequestParam(required = false) String transactionType,
            @RequestParam(required = false) String assetType,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount
    ) {
        FinanceFilterRequest filters = buildFilters(
                customerName,
                customerCategory,
                vendorName,
                vendorIndustry,
                invoiceStatus,
                statusGroup,
                accountName,
                accountType,
                transactionType,
                assetType,
                minAmount,
                maxAmount
        );

        return ResponseEntity.ok(
                financeKpiService.getFinanceKpis(startDate, endDate, filters)
        );
    }

    @GetMapping("/revenue-profit-trend")
    public ResponseEntity<List<FinanceRevenueProfitTrendItem>> getRevenueProfitTrend(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String customerCategory,
            @RequestParam(required = false) String vendorName,
            @RequestParam(required = false) String vendorIndustry,
            @RequestParam(required = false) String invoiceStatus,
            @RequestParam(required = false) String statusGroup,
            @RequestParam(required = false) String accountName,
            @RequestParam(required = false) String accountType,
            @RequestParam(required = false) String transactionType,
            @RequestParam(required = false) String assetType,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount
    ) {
        FinanceFilterRequest filters = buildFilters(
                customerName,
                customerCategory,
                vendorName,
                vendorIndustry,
                invoiceStatus,
                statusGroup,
                accountName,
                accountType,
                transactionType,
                assetType,
                minAmount,
                maxAmount
        );

        return ResponseEntity.ok(
                financeKpiService.getRevenueProfitTrend(startDate, endDate, filters)
        );
    }

    @GetMapping("/cash-flow-trend")
    public ResponseEntity<List<FinanceCashFlowTrendItem>> getCashFlowTrend(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String customerCategory,
            @RequestParam(required = false) String vendorName,
            @RequestParam(required = false) String vendorIndustry,
            @RequestParam(required = false) String invoiceStatus,
            @RequestParam(required = false) String statusGroup,
            @RequestParam(required = false) String accountName,
            @RequestParam(required = false) String accountType,
            @RequestParam(required = false) String transactionType,
            @RequestParam(required = false) String assetType,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount
    ) {
        FinanceFilterRequest filters = buildFilters(
                customerName,
                customerCategory,
                vendorName,
                vendorIndustry,
                invoiceStatus,
                statusGroup,
                accountName,
                accountType,
                transactionType,
                assetType,
                minAmount,
                maxAmount
        );

        return ResponseEntity.ok(
                financeKpiService.getCashFlowTrend(startDate, endDate, filters)
        );
    }

    @GetMapping("/top-outstanding-invoices")
    public ResponseEntity<List<FinanceOutstandingInvoiceItem>> getTopOutstandingInvoices(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String customerCategory,
            @RequestParam(required = false) String vendorName,
            @RequestParam(required = false) String vendorIndustry,
            @RequestParam(required = false) String invoiceStatus,
            @RequestParam(required = false) String statusGroup,
            @RequestParam(required = false) String accountName,
            @RequestParam(required = false) String accountType,
            @RequestParam(required = false) String transactionType,
            @RequestParam(required = false) String assetType,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount
    ) {
        FinanceFilterRequest filters = buildFilters(
                customerName,
                customerCategory,
                vendorName,
                vendorIndustry,
                invoiceStatus,
                statusGroup,
                accountName,
                accountType,
                transactionType,
                assetType,
                minAmount,
                maxAmount
        );

        return ResponseEntity.ok(
                financeKpiService.getTopOutstandingInvoices(startDate, endDate, filters)
        );
    }

    @GetMapping("/liability-vs-assets")
    public ResponseEntity<FinanceLiabilityAssetItem> getLiabilityVsAssets(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String customerCategory,
            @RequestParam(required = false) String vendorName,
            @RequestParam(required = false) String vendorIndustry,
            @RequestParam(required = false) String invoiceStatus,
            @RequestParam(required = false) String statusGroup,
            @RequestParam(required = false) String accountName,
            @RequestParam(required = false) String accountType,
            @RequestParam(required = false) String transactionType,
            @RequestParam(required = false) String assetType,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount
    ) {
        FinanceFilterRequest filters = buildFilters(
                customerName,
                customerCategory,
                vendorName,
                vendorIndustry,
                invoiceStatus,
                statusGroup,
                accountName,
                accountType,
                transactionType,
                assetType,
                minAmount,
                maxAmount
        );

        return ResponseEntity.ok(
                financeKpiService.getLiabilityVsAssets(startDate, endDate, filters)
        );
    }

    @GetMapping("/asset-distribution")
    public ResponseEntity<List<FinanceAssetDistributionItem>> getAssetDistribution(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String customerCategory,
            @RequestParam(required = false) String vendorName,
            @RequestParam(required = false) String vendorIndustry,
            @RequestParam(required = false) String invoiceStatus,
            @RequestParam(required = false) String statusGroup,
            @RequestParam(required = false) String accountName,
            @RequestParam(required = false) String accountType,
            @RequestParam(required = false) String transactionType,
            @RequestParam(required = false) String assetType,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount
    ) {
        FinanceFilterRequest filters = buildFilters(
                customerName,
                customerCategory,
                vendorName,
                vendorIndustry,
                invoiceStatus,
                statusGroup,
                accountName,
                accountType,
                transactionType,
                assetType,
                minAmount,
                maxAmount
        );

        return ResponseEntity.ok(
                financeKpiService.getAssetDistribution(endDate, filters)
        );
    }

    @GetMapping("/compliance-summary")
    public ResponseEntity<FinanceComplianceSummaryResponse> getComplianceSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            @RequestParam(required = false) String customerName,
            @RequestParam(required = false) String customerCategory,
            @RequestParam(required = false) String vendorName,
            @RequestParam(required = false) String vendorIndustry,
            @RequestParam(required = false) String invoiceStatus,
            @RequestParam(required = false) String statusGroup,
            @RequestParam(required = false) String accountName,
            @RequestParam(required = false) String accountType,
            @RequestParam(required = false) String transactionType,
            @RequestParam(required = false) String assetType,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount
    ) {
        FinanceFilterRequest filters = buildFilters(
                customerName,
                customerCategory,
                vendorName,
                vendorIndustry,
                invoiceStatus,
                statusGroup,
                accountName,
                accountType,
                transactionType,
                assetType,
                minAmount,
                maxAmount
        );

        return ResponseEntity.ok(
                financeKpiService.getComplianceSummary(startDate, endDate, filters)
        );
    }
}