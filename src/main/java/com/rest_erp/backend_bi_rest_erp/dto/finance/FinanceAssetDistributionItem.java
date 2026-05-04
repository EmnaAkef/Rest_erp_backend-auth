package com.rest_erp.backend_bi_rest_erp.dto.finance;

import java.math.BigDecimal;

public class FinanceAssetDistributionItem {

    private String assetType;
    private BigDecimal assetValue;

    public FinanceAssetDistributionItem() {
    }

    public FinanceAssetDistributionItem(String assetType, BigDecimal assetValue) {
        this.assetType = assetType;
        this.assetValue = assetValue;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public BigDecimal getAssetValue() {
        return assetValue;
    }

    public void setAssetValue(BigDecimal assetValue) {
        this.assetValue = assetValue;
    }
}