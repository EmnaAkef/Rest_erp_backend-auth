package com.rest_erp.backend_bi_rest_erp.dto.finance;

import java.math.BigDecimal;

public class FinanceCashFlowTrendItem {

    private String period;
    private BigDecimal inflow;
    private BigDecimal outflow;
    private BigDecimal netCashFlow;

    public FinanceCashFlowTrendItem() {
    }

    public FinanceCashFlowTrendItem(String period, BigDecimal inflow, BigDecimal outflow, BigDecimal netCashFlow) {
        this.period = period;
        this.inflow = inflow;
        this.outflow = outflow;
        this.netCashFlow = netCashFlow;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public BigDecimal getInflow() {
        return inflow;
    }

    public void setInflow(BigDecimal inflow) {
        this.inflow = inflow;
    }

    public BigDecimal getOutflow() {
        return outflow;
    }

    public void setOutflow(BigDecimal outflow) {
        this.outflow = outflow;
    }

    public BigDecimal getNetCashFlow() {
        return netCashFlow;
    }

    public void setNetCashFlow(BigDecimal netCashFlow) {
        this.netCashFlow = netCashFlow;
    }
}