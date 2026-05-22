package com.rest_erp.backend_bi_rest_erp.bi.dto.finance;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class FinanceKpiResponse {

    private String currency;

    private BigDecimal totalRevenue;
    private BigDecimal totalExpenses;
    private BigDecimal netProfit;
    private BigDecimal grossMarginPercentage;

    private BigDecimal cashBalance;
    private BigDecimal bankAccountBalance;
    private BigDecimal totalLiabilities;
    private BigDecimal liquidityRatio;

    private BigDecimal accountsReceivable;
    private BigDecimal accountsPayable;
    private Integer numberOfOpenInvoices;
    private Integer dueInvoices;

    private BigDecimal assetValue;
    private BigDecimal depreciationExpense;

    private BigDecimal vatCollected;
    private BigDecimal vatPayable;


}