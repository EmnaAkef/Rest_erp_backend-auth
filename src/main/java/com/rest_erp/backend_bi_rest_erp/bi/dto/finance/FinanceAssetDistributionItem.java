package com.rest_erp.backend_bi_rest_erp.bi.dto.finance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class FinanceAssetDistributionItem {

    private String assetType;
    private BigDecimal assetValue;


}