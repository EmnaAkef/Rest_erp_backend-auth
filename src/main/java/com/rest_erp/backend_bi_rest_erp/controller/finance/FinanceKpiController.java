package com.rest_erp.backend_bi_rest_erp.controller.finance;

import com.rest_erp.backend_bi_rest_erp.dto.finance.*;
import com.rest_erp.backend_bi_rest_erp.service.finance.FinanceKpiService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/kpis")
    public ResponseEntity<FinanceKpiResponse> getFinanceKpis(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        FinanceKpiResponse response = financeKpiService.getFinanceKpis(startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/revenue-profit-trend")
    public ResponseEntity<List<FinanceRevenueProfitTrendItem>> getRevenueProfitTrend(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(
                financeKpiService.getRevenueProfitTrend(startDate, endDate)
        );
    }
    @GetMapping("/cash-flow-trend")
    public ResponseEntity<List<FinanceCashFlowTrendItem>> getCashFlowTrend(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(
                financeKpiService.getCashFlowTrend(startDate, endDate)
        );
    }
    @GetMapping("/top-outstanding-invoices")
    public ResponseEntity<List<FinanceOutstandingInvoiceItem>> getTopOutstandingInvoices(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(
                financeKpiService.getTopOutstandingInvoices(startDate, endDate)
        );
    }
    @GetMapping("/liability-vs-assets")
    public ResponseEntity<FinanceLiabilityAssetItem> getLiabilityVsAssets(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(
                financeKpiService.getLiabilityVsAssets(startDate, endDate)
        );
    }
    @GetMapping("/asset-distribution")
    public ResponseEntity<List<FinanceAssetDistributionItem>> getAssetDistribution(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(
                financeKpiService.getAssetDistribution(endDate)
        );
    }
    @GetMapping("/compliance-summary")
    public ResponseEntity<FinanceComplianceSummaryResponse> getComplianceSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(
                financeKpiService.getComplianceSummary(startDate, endDate)
        );
    }
}