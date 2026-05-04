package com.rest_erp.backend_bi_rest_erp.dto.finance;

import java.math.BigDecimal;

public class FinanceKpiResponse {

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

    public FinanceKpiResponse() {
    }

    public FinanceKpiResponse(
            BigDecimal totalRevenue,
            BigDecimal totalExpenses,
            BigDecimal netProfit,
            BigDecimal grossMarginPercentage,
            BigDecimal cashBalance,
            BigDecimal bankAccountBalance,
            BigDecimal totalLiabilities,
            BigDecimal liquidityRatio,
            BigDecimal accountsReceivable,
            BigDecimal accountsPayable,
            Integer numberOfOpenInvoices,
            Integer dueInvoices,
            BigDecimal assetValue,
            BigDecimal depreciationExpense,
            BigDecimal vatCollected,
            BigDecimal vatPayable
    ) {
        this.totalRevenue = totalRevenue;
        this.totalExpenses = totalExpenses;
        this.netProfit = netProfit;
        this.grossMarginPercentage = grossMarginPercentage;
        this.cashBalance = cashBalance;
        this.bankAccountBalance = bankAccountBalance;
        this.totalLiabilities = totalLiabilities;
        this.liquidityRatio = liquidityRatio;
        this.accountsReceivable = accountsReceivable;
        this.accountsPayable = accountsPayable;
        this.numberOfOpenInvoices = numberOfOpenInvoices;
        this.dueInvoices = dueInvoices;
        this.assetValue = assetValue;
        this.depreciationExpense = depreciationExpense;
        this.vatCollected = vatCollected;
        this.vatPayable = vatPayable;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public BigDecimal getNetProfit() {
        return netProfit;
    }

    public void setNetProfit(BigDecimal netProfit) {
        this.netProfit = netProfit;
    }

    public BigDecimal getGrossMarginPercentage() {
        return grossMarginPercentage;
    }

    public void setGrossMarginPercentage(BigDecimal grossMarginPercentage) {
        this.grossMarginPercentage = grossMarginPercentage;
    }

    public BigDecimal getCashBalance() {
        return cashBalance;
    }

    public void setCashBalance(BigDecimal cashBalance) {
        this.cashBalance = cashBalance;
    }

    public BigDecimal getBankAccountBalance() {
        return bankAccountBalance;
    }

    public void setBankAccountBalance(BigDecimal bankAccountBalance) {
        this.bankAccountBalance = bankAccountBalance;
    }

    public BigDecimal getTotalLiabilities() {
        return totalLiabilities;
    }

    public void setTotalLiabilities(BigDecimal totalLiabilities) {
        this.totalLiabilities = totalLiabilities;
    }

    public BigDecimal getLiquidityRatio() {
        return liquidityRatio;
    }

    public void setLiquidityRatio(BigDecimal liquidityRatio) {
        this.liquidityRatio = liquidityRatio;
    }

    public BigDecimal getAccountsReceivable() {
        return accountsReceivable;
    }

    public void setAccountsReceivable(BigDecimal accountsReceivable) {
        this.accountsReceivable = accountsReceivable;
    }

    public BigDecimal getAccountsPayable() {
        return accountsPayable;
    }

    public void setAccountsPayable(BigDecimal accountsPayable) {
        this.accountsPayable = accountsPayable;
    }

    public Integer getNumberOfOpenInvoices() {
        return numberOfOpenInvoices;
    }

    public void setNumberOfOpenInvoices(Integer numberOfOpenInvoices) {
        this.numberOfOpenInvoices = numberOfOpenInvoices;
    }

    public Integer getDueInvoices() {
        return dueInvoices;
    }

    public void setDueInvoices(Integer dueInvoices) {
        this.dueInvoices = dueInvoices;
    }

    public BigDecimal getAssetValue() {
        return assetValue;
    }

    public void setAssetValue(BigDecimal assetValue) {
        this.assetValue = assetValue;
    }

    public BigDecimal getDepreciationExpense() {
        return depreciationExpense;
    }

    public void setDepreciationExpense(BigDecimal depreciationExpense) {
        this.depreciationExpense = depreciationExpense;
    }

    public BigDecimal getVatCollected() {
        return vatCollected;
    }

    public void setVatCollected(BigDecimal vatCollected) {
        this.vatCollected = vatCollected;
    }

    public BigDecimal getVatPayable() {
        return vatPayable;
    }

    public void setVatPayable(BigDecimal vatPayable) {
        this.vatPayable = vatPayable;
    }
}