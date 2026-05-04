package com.rest_erp.backend_bi_rest_erp.dto.finance;

import java.math.BigDecimal;

public class FinanceRevenueProfitTrendItem {

    private String period;
    private BigDecimal revenue;
    private BigDecimal profit;

    public FinanceRevenueProfitTrendItem() {
    }

    public FinanceRevenueProfitTrendItem(String period, BigDecimal revenue, BigDecimal profit) {
        this.period = period;
        this.revenue = revenue;
        this.profit = profit;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }
}