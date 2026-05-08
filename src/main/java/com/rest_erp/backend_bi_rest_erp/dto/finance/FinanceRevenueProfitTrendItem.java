package com.rest_erp.backend_bi_rest_erp.dto.finance;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class FinanceRevenueProfitTrendItem {

    private String period;
    private BigDecimal revenue;
    private BigDecimal profit;



}