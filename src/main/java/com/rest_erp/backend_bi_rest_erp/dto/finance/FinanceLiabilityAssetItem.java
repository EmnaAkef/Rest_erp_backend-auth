package com.rest_erp.backend_bi_rest_erp.dto.finance;

import java.math.BigDecimal;

public class FinanceLiabilityAssetItem {

    private BigDecimal currentAssets;
    private BigDecimal fixedAssets;
    private BigDecimal totalAssets;

    private BigDecimal currentLiabilities;
    private BigDecimal longTermLiabilities;
    private BigDecimal totalLiabilities;

    public FinanceLiabilityAssetItem() {
    }

    public FinanceLiabilityAssetItem(
            BigDecimal currentAssets,
            BigDecimal fixedAssets,
            BigDecimal totalAssets,
            BigDecimal currentLiabilities,
            BigDecimal longTermLiabilities,
            BigDecimal totalLiabilities
    ) {
        this.currentAssets = currentAssets;
        this.fixedAssets = fixedAssets;
        this.totalAssets = totalAssets;
        this.currentLiabilities = currentLiabilities;
        this.longTermLiabilities = longTermLiabilities;
        this.totalLiabilities = totalLiabilities;
    }

    public BigDecimal getCurrentAssets() {
        return currentAssets;
    }

    public void setCurrentAssets(BigDecimal currentAssets) {
        this.currentAssets = currentAssets;
    }

    public BigDecimal getFixedAssets() {
        return fixedAssets;
    }

    public void setFixedAssets(BigDecimal fixedAssets) {
        this.fixedAssets = fixedAssets;
    }

    public BigDecimal getTotalAssets() {
        return totalAssets;
    }

    public void setTotalAssets(BigDecimal totalAssets) {
        this.totalAssets = totalAssets;
    }

    public BigDecimal getCurrentLiabilities() {
        return currentLiabilities;
    }

    public void setCurrentLiabilities(BigDecimal currentLiabilities) {
        this.currentLiabilities = currentLiabilities;
    }

    public BigDecimal getLongTermLiabilities() {
        return longTermLiabilities;
    }

    public void setLongTermLiabilities(BigDecimal longTermLiabilities) {
        this.longTermLiabilities = longTermLiabilities;
    }

    public BigDecimal getTotalLiabilities() {
        return totalLiabilities;
    }

    public void setTotalLiabilities(BigDecimal totalLiabilities) {
        this.totalLiabilities = totalLiabilities;
    }
}