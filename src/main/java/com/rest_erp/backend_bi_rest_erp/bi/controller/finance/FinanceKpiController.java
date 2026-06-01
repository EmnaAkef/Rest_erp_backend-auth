package com.rest_erp.backend_bi_rest_erp.bi.controller.finance;

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
import com.rest_erp.backend_bi_rest_erp.bi.service.finance.FinanceKpiService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bi/finance")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FinanceKpiController {

    private final FinanceKpiService financeKpiService;

    @GetMapping("/kpis")
    public ResponseEntity<FinanceKpiResponse> getFinanceKpis(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @ModelAttribute FinanceFilterRequest filters
    ) {
        return ResponseEntity.ok(financeKpiService.getFinanceKpis(startDate, endDate, filters));
    }

    @GetMapping("/revenue-profit-trend")
    public ResponseEntity<List<FinanceRevenueProfitTrendItem>> getRevenueProfitTrend(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @ModelAttribute FinanceFilterRequest filters
    ) {
        return ResponseEntity.ok(financeKpiService.getRevenueProfitTrend(startDate, endDate, filters));
    }

    @GetMapping("/cash-flow-trend")
    public ResponseEntity<List<FinanceCashFlowTrendItem>> getCashFlowTrend(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @ModelAttribute FinanceFilterRequest filters
    ) {
        return ResponseEntity.ok(financeKpiService.getCashFlowTrend(startDate, endDate, filters));
    }

    @GetMapping("/top-outstanding-invoices")
    public ResponseEntity<List<FinanceOutstandingInvoiceItem>> getTopOutstandingInvoices(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @ModelAttribute FinanceFilterRequest filters
    ) {
        return ResponseEntity.ok(financeKpiService.getTopOutstandingInvoices(startDate, endDate, filters));
    }

    @GetMapping("/liability-vs-assets")
    public ResponseEntity<FinanceLiabilityAssetItem> getLiabilityVsAssets(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @ModelAttribute FinanceFilterRequest filters
    ) {
        return ResponseEntity.ok(financeKpiService.getLiabilityVsAssets(startDate, endDate, filters));
    }

    @GetMapping("/asset-distribution")
    public ResponseEntity<List<FinanceAssetDistributionItem>> getAssetDistribution(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @ModelAttribute FinanceFilterRequest filters
    ) {
        return ResponseEntity.ok(financeKpiService.getAssetDistribution(endDate, filters));
    }

    @GetMapping("/recent-tax-payments")
    public ResponseEntity<List<FinanceTaxPaymentItem>> getRecentTaxPayments(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @ModelAttribute FinanceFilterRequest filters
    ) {
        return ResponseEntity.ok(financeKpiService.getRecentTaxPayments(startDate, endDate, filters));
    }

    @GetMapping("/next-filing-dates")
    public ResponseEntity<List<FinanceFilingDateItem>> getNextFilingDates() {
        return ResponseEntity.ok(financeKpiService.getNextFilingDates());
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
        return ResponseEntity.ok(financeKpiService.searchFinanceFilterOptions(field, q));
    }
}
