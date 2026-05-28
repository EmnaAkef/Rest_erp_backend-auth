package com.rest_erp.backend_bi_rest_erp.bi.controller.finance;

import com.rest_erp.backend_bi_rest_erp.bi.dto.finance.FinanceKpiResponse;
import com.rest_erp.backend_bi_rest_erp.bi.service.finance.FinanceKpiService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import com.rest_erp.backend_bi_rest_erp.bi.dto.finance.FinanceRevenueProfitTrendItem;
import com.rest_erp.backend_bi_rest_erp.bi.dto.finance.FinanceCashFlowTrendItem;
import com.rest_erp.backend_bi_rest_erp.bi.dto.finance.FinanceOutstandingInvoiceItem;
import com.rest_erp.backend_bi_rest_erp.bi.dto.finance.FinanceLiabilityAssetItem;
import com.rest_erp.backend_bi_rest_erp.bi.dto.finance.FinanceAssetDistributionItem;
import java.util.List;
import com.rest_erp.backend_bi_rest_erp.bi.dto.finance.FinanceTaxPaymentItem;
import com.rest_erp.backend_bi_rest_erp.bi.dto.finance.FinanceFilingDateItem;
import java.util.List;
import java.time.LocalDate;
import com.rest_erp.backend_bi_rest_erp.bi.dto.finance.FinanceFilterOptionsResponse;
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

    @GetMapping("/recent-tax-payments")
    public List<FinanceTaxPaymentItem> getRecentTaxPayments(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ) {
        return financeKpiService.getRecentTaxPayments(startDate, endDate);
    }

    @GetMapping("/next-filing-dates")
    public List<FinanceFilingDateItem> getNextFilingDates() {
        return financeKpiService.getNextFilingDates();
    }
    @GetMapping("/filter-options")
    public ResponseEntity<FinanceFilterOptionsResponse> getFinanceFilterOptions() {
        return ResponseEntity.ok(financeKpiService.getFinanceFilterOptions());
    }
    @GetMapping("/filter-options/search")
    public ResponseEntity<List<String>> searchFinanceFilterOptions(
            @RequestParam String field,
            @RequestParam(defaultValue = "") String q
    ) {
        return ResponseEntity.ok(
                financeKpiService.searchFinanceFilterOptions(field, q)
        );
    }
}