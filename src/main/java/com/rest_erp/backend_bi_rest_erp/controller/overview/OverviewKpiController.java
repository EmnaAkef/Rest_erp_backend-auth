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
}