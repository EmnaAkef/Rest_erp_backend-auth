package com.rest_erp.backend_bi_rest_erp.bi.dto.finance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinanceComplianceSummaryResponse {

    private String complianceStatus;
    private String complianceStatusIcon;

    private List<FilingDateItem> nextFilingDates;
    private List<TaxPaymentItem> taxPayments;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FilingDateItem {
        private String label;
        private String date;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TaxPaymentItem {
        private String code;
        private String label;
        private BigDecimal amount;
    }
}