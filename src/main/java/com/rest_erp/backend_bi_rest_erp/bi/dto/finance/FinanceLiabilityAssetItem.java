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

public class FinanceLiabilityAssetItem {

    private BigDecimal currentAssets;
    private BigDecimal fixedAssets;
    private BigDecimal totalAssets;

    private BigDecimal currentLiabilities;
    private BigDecimal longTermLiabilities;
    private BigDecimal totalLiabilities;


}