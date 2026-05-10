package com.rest_erp.backend_bi_rest_erp.controller.overview;

import com.rest_erp.backend_bi_rest_erp.dto.overview.OverviewKpiResponse;
import com.rest_erp.backend_bi_rest_erp.service.overview.OverviewKpiService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import com.rest_erp.backend_bi_rest_erp.dto.overview.OverviewFinancialTrendItem;
import java.util.List;
import com.rest_erp.backend_bi_rest_erp.dto.overview.OverviewFinancialTrendItem;
import com.rest_erp.backend_bi_rest_erp.dto.overview.OverviewCashSummaryItem;
import com.rest_erp.backend_bi_rest_erp.dto.overview.OverviewPipelineFunnelItem;
import com.rest_erp.backend_bi_rest_erp.dto.overview.OverviewDealStatusItem;
import com.rest_erp.backend_bi_rest_erp.dto.overview.OverviewTopSalespersonItem;
import com.rest_erp.backend_bi_rest_erp.dto.overview.OverviewAttendanceTrendItem;
import com.rest_erp.backend_bi_rest_erp.dto.overview.OverviewDepartmentDistributionItem;
import com.rest_erp.backend_bi_rest_erp.dto.overview.OverviewCustomerRetentionItem;
import com.rest_erp.backend_bi_rest_erp.dto.overview.OverviewTopCustomerItem;
import com.rest_erp.backend_bi_rest_erp.dto.overview.OverviewOperationalAlertItem;
import com.rest_erp.backend_bi_rest_erp.dto.overview.OverviewExecutiveLedgerItem;
@RestController
@RequestMapping("/api/bi/overview")
@CrossOrigin(origins = "*")
public class OverviewKpiController {

    private final OverviewKpiService overviewKpiService;

    public OverviewKpiController(OverviewKpiService overviewKpiService) {
        this.overviewKpiService = overviewKpiService;
    }

    @GetMapping("/kpis")
    public ResponseEntity<OverviewKpiResponse> getOverviewKpis(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(
                overviewKpiService.getOverviewKpis(startDate, endDate)
        );
    }

    @GetMapping("/financial-trend")
    public ResponseEntity<List<OverviewFinancialTrendItem>> getFinancialTrend(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(
                overviewKpiService.getFinancialTrend(startDate, endDate)
        );
    }
    @GetMapping("/cash-summary")
    public ResponseEntity<OverviewCashSummaryItem> getCashSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(
                overviewKpiService.getCashSummary(startDate, endDate)
        );
    }

    @GetMapping("/sales-pipeline-funnel")
    public ResponseEntity<List<OverviewPipelineFunnelItem>> getSalesPipelineFunnel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(
                overviewKpiService.getSalesPipelineFunnel(startDate, endDate)
        );
    }

    @GetMapping("/deal-status")
    public ResponseEntity<List<OverviewDealStatusItem>> getDealStatus(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(
                overviewKpiService.getDealStatus(startDate, endDate)
        );
    }
    @GetMapping("/top-sales-performers")
    public ResponseEntity<List<OverviewTopSalespersonItem>> getTopSalesPerformers(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(
                overviewKpiService.getTopSalesPerformers(startDate, endDate)
        );
    }
    @GetMapping("/attendance-trend")
    public ResponseEntity<List<OverviewAttendanceTrendItem>> getAttendanceTrend(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(
                overviewKpiService.getAttendanceTrend(startDate, endDate)
        );
    }

    @GetMapping("/department-distribution")
    public ResponseEntity<List<OverviewDepartmentDistributionItem>> getDepartmentDistribution() {
        return ResponseEntity.ok(
                overviewKpiService.getDepartmentDistribution()
        );
    }

    @GetMapping("/customer-retention")
    public ResponseEntity<OverviewCustomerRetentionItem> getCustomerRetention() {
        return ResponseEntity.ok(
                overviewKpiService.getCustomerRetention()
        );
    }
    @GetMapping("/top-customers-revenue")
    public ResponseEntity<List<OverviewTopCustomerItem>> getTopCustomersByRevenue(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(
                overviewKpiService.getTopCustomersByRevenue(startDate, endDate)
        );
    }

    @GetMapping("/operational-alerts")
    public ResponseEntity<List<OverviewOperationalAlertItem>> getOperationalAlerts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(
                overviewKpiService.getOperationalAlerts(startDate, endDate)
        );
    }

    @GetMapping("/executive-ledger")
    public ResponseEntity<List<OverviewExecutiveLedgerItem>> getExecutiveLedger(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(
                overviewKpiService.getExecutiveLedger(startDate, endDate)
        );
    }
}